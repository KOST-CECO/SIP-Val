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
public enum IdentificationMethod {

    /** Binary Signature identification. */
    BINARY_SIGNATURE("Binary Signature",
            "File is identified using pronum signature defination"),
    /** Identification by extension. */
    EXTENSION("Extension", "File is identified using extension."), 
    
    /** Identified by operting system. */
    OPERATING_SYSTEM("OS", "Operating systemn identified the resource.");

    private String method;
    private String methodDescription;
    
    
    /**
     * Constructor
     * @param method Identification method Binary or extension.
     * @param methodDescription Identification method description.
     */
    IdentificationMethod(String method, String methodDescription) {
        this.method = method;
        this.methodDescription = methodDescription;
    }

    /**
     * @return the id
     */
    public long getId() {
        return ordinal();
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the methodDescription
     */
    public String getMethodDescription() {
        return methodDescription;
    }

}
