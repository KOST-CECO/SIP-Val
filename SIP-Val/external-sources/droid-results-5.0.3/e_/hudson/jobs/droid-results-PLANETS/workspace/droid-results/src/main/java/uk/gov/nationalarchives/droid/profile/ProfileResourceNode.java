/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import uk.gov.nationalarchives.droid.profile.types.UriType;

/**
 * @author rflitcroft
 *
 */
@TypeDef(name = "uri", typeClass = UriType.class)
@Entity
@Table(name = "profile_resource_node")
public class ProfileResourceNode {

    private static final int URI_LENGTH = 1000;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "node_id")
    private Long id;
    
    @ManyToOne(optional = true)
    @JoinColumn(name = "parent_id")
    private ProfileResourceNode parent;
    
    @Column(nullable = false, name = "uri", unique = false, length = URI_LENGTH)
    @Type(type = "uri")
    @Index(name = "idx_uri")
    private URI uri;
    
    @Column(name = "prefix")
    @Index(name = "idx_prefix")
    private String prefix;
    
    @Index(name = "idx_prefix_plus_one")
    @Column(name = "prefix_plus_one")
    private String prefixPlusOne;
    
    @org.hibernate.annotations.Fetch(FetchMode.SUBSELECT)
    @OneToMany(mappedBy = "node", fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private List<FormatIdentification> formatIdentifications = new ArrayList<FormatIdentification>();
    
    @Embedded
    private NodeMetaData metaData = new NodeMetaData();
    
    @Embedded 
    private IdentificationJob job = new IdentificationJob();

    /**
     * Default constructor.
     */
    ProfileResourceNode() { }
    
    /**
     * @param uri the URI of the resource node
     */
    public ProfileResourceNode(URI uri) {
        this.uri = uri;
    }
    
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the job
     */
    public IdentificationJob getJob() {
        return job;
    }

    /**
     * @return the uri
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @return the parent
     */
    public boolean allowsChildren() {
        return metaData.getResourceType().allowsChildren();
    }

    /**
     * @return the metaData
     */
    public NodeMetaData getMetaData() {
        return metaData;
    }

    /**
     * @param metaData the metaData to set
     */
    public void setMetaData(NodeMetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * @param identificationJob the identification job to set
     */
    public void setJob(IdentificationJob identificationJob) {
        job = identificationJob;
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(uri).toHashCode();
    }
    
    /**
     * @return the parent
     */
    public ProfileResourceNode getParent() {
        return parent;
    }
    
    /**
     * @param parent the parent to set
     */
    public void setParent(ProfileResourceNode parent) {
        this.parent = parent;
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
        
        ProfileResourceNode other = (ProfileResourceNode) obj;
        
        return new EqualsBuilder().append(uri, other.uri).isEquals();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append(uri).toString();
    }

    /**
     * @return prefix Getter method for prefix.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix Setter method for setting prefix.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return prefixPlusOne Getter method for getting prefixPlusOne
     */
    public String getPrefixPlusOne() {
        return prefixPlusOne;
    }

    /**
     * @param prefixPlusOne Setter method for prefixPlusOne.
     */
    public void setPrefixPlusOne(String prefixPlusOne) {
        this.prefixPlusOne = prefixPlusOne;
    }

    /**
     * Adds an identification to the identification job.
     * @param formatIdentification the identification to add
     */
    public void addFormatIdentification(FormatIdentification formatIdentification) {
        formatIdentifications.add(formatIdentification);
        formatIdentification.setNode(this);
        
    }

    /**
     * @return List of format identifications
     */
    public List<FormatIdentification> getFormatIdentifications() {
        return Collections.unmodifiableList(formatIdentifications);
    }

    
}
