/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces;

import java.net.URI;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Class which holds the information needed to identify the source of an 
 * identification request. 
 * @author rflitcroft
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class RequestIdentifier {

    @XmlAttribute(name = "NodeId")
    private Long nodeId;
    
    @XmlAttribute(name = "ParentId")
    private Long parentId;

    @XmlAttribute(name = "AncestorId")
    private Long ancestorId;

    @XmlValue
    private URI uri;
    
    /**
     * Default Constructor. 
     */
    RequestIdentifier() { }
    
    /**
     * 
     * @param uri the URI of the request's data.
     */
    public RequestIdentifier(URI uri) {
        this.uri = uri;
    }

    /**
     * @return the nodeId
     */
    public Long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId the nodeId to set
     */
    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the parentId
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * @param parentId the parentId to set
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return the ancestorId
     */
    public Long getAncestorId() {
        return ancestorId;
    }

    /**
     * @param ancestorId the ancestorId to set
     */
    public void setAncestorId(Long ancestorId) {
        this.ancestorId = ancestorId;
    }

    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(ancestorId)
            .append(nodeId)
            .append(parentId)
            .append(uri)
            .toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RequestIdentifier other = (RequestIdentifier) obj;
        
        return new EqualsBuilder()
            .append(ancestorId, other.ancestorId)
            .append(nodeId, other.nodeId)
            .append(parentId, other.parentId)
            .append(uri, other.uri)
            .isEquals();
            
    }

}
