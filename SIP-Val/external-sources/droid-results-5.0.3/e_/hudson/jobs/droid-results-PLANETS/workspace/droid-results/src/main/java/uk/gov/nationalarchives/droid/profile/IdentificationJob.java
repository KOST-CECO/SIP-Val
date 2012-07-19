/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * An identification Job.
 * @author rflitcroft
 *
 */
@Embeddable
public class IdentificationJob {
    
    @Column(name = "finished_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finished;
    
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private JobStatus status;
    
    @Column(name = "duration_millis")
    private Long durationMillis;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    /**
     * Default Constructor.
     */
    public IdentificationJob() {
    }
            
    /**
     * @return the finished
     */
    public Date getFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(Date finished) {
        this.finished = finished;
    }

    /**
     * @return the status
     */
    public JobStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    /**
     * @return the durationMillis
     */
    public Long getDurationMillis() {
        return durationMillis;
    }

    /**
     * @param durationMillis the durationMillis to set
     */
    public void setDurationMillis(Long durationMillis) {
        this.durationMillis = durationMillis;
    }


    /**
     * @param message the error message to set
     */
    public void setErrorMessage(String message) {
        errorMessage = message;
    }
    
    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

}
