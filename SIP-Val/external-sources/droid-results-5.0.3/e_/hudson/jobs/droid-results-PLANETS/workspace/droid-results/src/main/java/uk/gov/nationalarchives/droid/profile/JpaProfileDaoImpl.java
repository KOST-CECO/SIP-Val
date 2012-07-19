/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.nationalarchives.droid.core.interfaces.filter.Filter;
import uk.gov.nationalarchives.droid.core.interfaces.filter.FilterCriterion;
import uk.gov.nationalarchives.droid.core.interfaces.filter.RestrictionFactory;
import uk.gov.nationalarchives.droid.core.interfaces.filter.expressions.Junction;
import uk.gov.nationalarchives.droid.core.interfaces.filter.expressions.QueryBuilder;
import uk.gov.nationalarchives.droid.core.interfaces.filter.expressions.Restrictions;
import uk.gov.nationalarchives.droid.profile.referencedata.Format;

/**
 * JPA implementation of ProfileDao.
 * 
 * @author rflitcroft
 */
public class JpaProfileDaoImpl implements ProfileDao {

    private final Log log = LogFactory.getLog(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Flushes the DROID entity manager.
     */
    void flush() {
        entityManager.flush();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Format> getAllFormats() {
        String query = "from Format order by name";
        Query q = entityManager.createQuery(query);
        return q.getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveFormat(Format format) {
        entityManager.persist(format);

    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ProfileResourceNode> findProfileResourceNodes(Long parentId) {

        String query = "from ProfileResourceNode n "
//                + "inner join fetch n.formatIdentifications i "
//                + "inner join fetch i.format " 
                + " where n.parent.id  " + (parentId == null ? " is null" : " = ? ");

        log.debug("query = " + query);
        Query q = entityManager.createQuery(query);
        log.debug("parent Id  = " + parentId);
        if (parentId != null) {
            q.setParameter(1, parentId);
        }

        long start = System.currentTimeMillis();
        List<ProfileResourceNode> results = q.getResultList();
        log.debug("Query time (ms) =  " + (System.currentTimeMillis() - start));

        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRED)
    public List<ProfileResourceNode> findProfileResourceNodes(Long parentId,
            Filter filter) {

        QueryBuilder queryBuilder = QueryBuilder
                .forAlias("profileResourceNode").createAlias("fis");
        if (filter.isNarrowed()) {
            for (FilterCriterion criterion : filter.getCriteria()) {
                queryBuilder.add(RestrictionFactory.forFilterCriterion(criterion));
            }
        } else {
            Junction disjunction = Restrictions.disjunction();
            for (FilterCriterion criterion : filter.getCriteria()) {
                disjunction.add(RestrictionFactory.forFilterCriterion(criterion));
            }
            queryBuilder.add(disjunction);
        }

        String query = "from ProfileResourceNode n  "
//                + "inner join fetch n.formatIdentifications formatId "
//                + "inner join fetch formatId.format format"
                + " where n.parent.id " + (parentId == null ? "is null" : " = ?")
                + " and exists "
                + "(select profileResourceNode.id from ProfileResourceNode "
                + "profileResourceNode inner join profileResourceNode.formatIdentifications "
                + "fis where profileResourceNode.prefix >= n.prefix and profileResourceNode.prefix < n.prefixPlusOne "
                + " AND " + queryBuilder.toEjbQl() + " )";

        log.debug("The query is : " + query);

        Query q = entityManager.createQuery(query);
        log.debug("parentId = " + parentId);

        int i = 1;
        if (parentId != null) {
            q.setParameter(i++, parentId);
        }

        Object[] values = queryBuilder.getValues();
        for (int j = 0; j < values.length; j++) {
            q.setParameter(i++, values[j]);
        }

        long start = System.currentTimeMillis();
        List<ProfileResourceNode> results = q.getResultList();
        log.debug("Query time (ms) = " + (System.currentTimeMillis() - start));

        return results;
    }

}
