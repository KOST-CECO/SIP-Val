/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile.referencedata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author rflitcroft
 *
 */
@Entity
@Table(name = "format")
public class Format {

    /** The NULL format. */
    public static final Format NULL = nullFormat();
    
    @Column(name = "ext_id")
    private String extId;
    
    @Id
    @Column(name = "puid")
    private String puid;
    
    @Column(name = "mime_type")
    private String mimeType;
    
    @Column(name = "name")
    private String name;

    @Column(name = "version")
    private String version;
    
    /**
     * @return the extId
     */
    public String getExtId() {
        return extId;
    }

    /**
     * @param extId the extId to set
     */
    public void setExtId(String extId) {
        this.extId = extId;
    }

    /**
     * @return the puid
     */
    public String getPuid() {
        return this.equals(NULL) ? null : puid;
    }

    /**
     * @param puid the puid to set
     */
    public void setPuid(String puid) {
        this.puid = puid;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(puid).toHashCode();
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
        Format other = (Format) obj;
        return new EqualsBuilder().append(puid, other.puid).isEquals();
    }
    
    /**
     * @return the null Format.
     */
    private static Format nullFormat() {
        Format fmt = new Format();
        fmt.setPuid("NULL");
        return fmt;
    }
    
}
