/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Index;

import uk.gov.nationalarchives.droid.profile.referencedata.Format;

/**
 * @author rflitcroft
 * 
 */
@Entity
@Table(name = "identification")
@org.hibernate.annotations.Table(appliesTo = "identification", 
        indexes = { @Index(name = "idx_format_id_join", columnNames = {
        "job_id", "format_id" }) })
public class FormatIdentification {

    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "identification_id")
    private Long id;

    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "job_id", nullable = false)
    private ProfileResourceNode node;

    @org.hibernate.annotations.Fetch(FetchMode.JOIN)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "format_id", nullable = false)
    private Format format;

    /**
     * @param node
     *            profile resource node.
     */
    public void setNode(ProfileResourceNode node) {
        this.node = node;
    }

    /**
     * @return the job
     */
    ProfileResourceNode getNode() {
        return node;
    }

    /**
     * @return the format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * @param format
     *            the format to set
     */
    public void setFormat(Format format) {
        this.format = format;
    }

}
