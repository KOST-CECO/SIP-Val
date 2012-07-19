/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.results.handlers;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.nationalarchives.droid.profile.IdentificationJob;
import uk.gov.nationalarchives.droid.profile.ProfileResourceNode;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;

/**
 * @author rflitcroft
 * 
 */
public class JpaResultHandlerDao implements ResultHandlerDao {
 
    private static final int NINENTYEIGHT = 98;
    private static final int THIRTYTHREE = 33;
    private static final int NINENTYFOUR = 94;

    private static final int HEX_F = 0xF;
    
    private static final int HEX_7F = 0x7F;

    private static final int UNSIGNED_RIGHT_SHIFT_BY_4 = 4;
    private static final int UNSIGNED_RIGHT_SHIFT_BY_11 = 11;
    private static final int UNSIGNED_RIGHT_SHIFT_BY_18 = 18;
    private static final int UNSIGNED_RIGHT_SHIFT_BY_25 = 25;

    private static final int ARRAYLENGTH = 5;

    private final Log log = LogFactory.getLog(getClass());
    
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void save(ProfileResourceNode node, Long parentId) {

        entityManager.persist(node);
        Long nodeId = node.getId();
        String nodePrefix = getBase128Integer(nodeId);
        String nodePrefixPlusOne = getBase128Integer(nodeId + 1);

        String parentsPrefixString = "";
        if (parentId != null) {
            ProfileResourceNode parent = entityManager.getReference(
                    ProfileResourceNode.class, parentId);
            parentsPrefixString = parent.getPrefix();
            node.setParent(parent);
        }
        node.setPrefix(parentsPrefixString + nodePrefix);
        node.setPrefixPlusOne(parentsPrefixString + nodePrefixPlusOne);

        /*
         * String itemPath = node.getUri().getPath(); if(itemPath.length() ==
         * itemPath.lastIndexOf(pathSaparator) + 1){ itemPath =
         * itemPath.substring(0,itemPath.lastIndexOf(pathSaparator));
         * node.setItemName
         * (itemPath.substring(itemPath.lastIndexOf(pathSaparator)+ 1,
         * itemPath.length())); } else {
         * node.setItemName(itemPath.substring(itemPath
         * .lastIndexOf(pathSaparator)+ 1, itemPath.length())); }
         */
    }

    private static long printableValue(long value) {
        return (value < NINENTYFOUR) ? value + THIRTYTHREE : value + NINENTYEIGHT;
    }
    
    /**
     * COnverts an long to base 128 integer.
     * @param value Value to convet to base 128 integer.
     * @return Base 128Integer.
     */

    public static String getBase128Integer(long value) {
        // Use printable characters in this range:
        // ASCII & UTF-8: 33 - 126 (no space) = 94 values.
        // ISO Latin 1 & UTF-8: 192 - 226 = 34 values.
        // Map 0-93 to 33-126
        // Map 93-127 to 192-226
        char[] values = new char[ARRAYLENGTH];
        
        int i = 0;
        values[i++] = (char) printableValue((value >>> UNSIGNED_RIGHT_SHIFT_BY_25) & HEX_7F); // bits 26-32
        values[i++] = (char) printableValue((value >>> UNSIGNED_RIGHT_SHIFT_BY_18) & HEX_7F); // bits 19-25
        values[i++] = (char) printableValue((value >>> UNSIGNED_RIGHT_SHIFT_BY_11) & HEX_7F); // bits 12-18
        values[i++] = (char) printableValue((value >>> UNSIGNED_RIGHT_SHIFT_BY_4) & HEX_7F); // bits 5-11
        values[i++] = (char) printableValue(value & HEX_F); // bits 1-4
        return new String(values);
//        Character.toString(values[0]) + Character.toString(values[ONE])
//                + Character.toString(values[TWO]) + Character.toString(values[THREE])
//                + Character.toString(values[FOUR]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public IdentificationJob loadJob(Long jobId) {
        return entityManager.getReference(IdentificationJob.class, jobId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Format loadFormat(String puid) {
        return entityManager.find(Format.class, puid);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public ProfileResourceNode loadNode(Long nodeId) {
        return entityManager.find(ProfileResourceNode.class, nodeId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteNode(Long nodeId) {
        ProfileResourceNode node = entityManager.getReference(ProfileResourceNode.class, nodeId);
        log.info(String.format("Deleting Node [%s]", node.getUri()));
        
        // delete identifications
        String idDelete = " delete FormatIdentification fi "
            + " where fi.node.id in ("
            + " select id from ProfileResourceNode n "
            + " where n.prefix >= ?  "
            + " and n.prefix < ?) ";
        
        Query q = entityManager.createQuery(idDelete);
        q.setParameter(1, node.getPrefix());
        q.setParameter(2, node.getPrefixPlusOne());
        int idCount = q.executeUpdate();
        log.info(String.format("Deleted [%s] identifications", idCount));
        
        
        // delete nodes
        String nodeDelete = "delete ProfileResourceNode n "
            + " where n.prefix >= ? "
            + " and n.prefix < ? ";
        
        Query qNode = entityManager.createQuery(nodeDelete);
        qNode.setParameter(1, node.getPrefix());
        qNode.setParameter(2, node.getPrefixPlusOne());
        
        int deleteCount = qNode.executeUpdate();

        log.info(String.format("Deleted [%s] nodes", deleteCount));
    }

}
