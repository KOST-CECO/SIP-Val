/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces;

/**
 * @author Alok Kumar Dash
 */

public enum NodeStatus {
    
    /** All those which are not processed.  */
    NotDone("Not Done", "This file is not processed yet."),
    
    /** Nodes which are  identified. */
    Identified("Identified", "Positive identification"),

    /** Nodes not identified.  */
    NotIdentified("Not Identified", "This file could not be identifed"),

    /** Nodes with multiple identification.  */
    MultipleIdentification("Multiple Identification",
            "Droid has identified more then one file type for this file."),

    /** Nodes with identification error.   */
    Error("Error", "An error has occurred while processing the file.");

    private String status;
    private String statusDescription;
    
    
    /**
     * Constructor for NodeStatusInMemory  
     * @param status node status 
     * @param statusDescription node status description.
     */
    NodeStatus(String status, String statusDescription) {
        this.status = status;
        this.statusDescription = statusDescription;
    }

    /**
     * @return the id
     */
    public long getId() {
        return ordinal();
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return the statusDescription
     */
    public String getStatusDescription() {
        return statusDescription;
    }
    
    /**
     * Resolves a number of results to the appropriate value.
     * @param size the results count.
     * @return the Node status
     */
    public static NodeStatus forResultSize(int size) {
        NodeStatus status;
        switch (size) {
            case 0: 
                status = NotIdentified;
                break;
            case 1: 
                status = Identified;
                break;
            default: 
                status = MultipleIdentification;
        }   
        
        return status;
    }

}
