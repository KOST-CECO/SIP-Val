/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core.interfaces.filter;

/**
 * @author rflitcroft
 *
 */
public enum CriterionOperator {
    
    /** Less than. */
    LT("<", false),
    /** Less than or equal. */
    LTE("<=", false),
    /** Equals. */
    EQ("=", false),
    /** Greater than or equal. */
    GTE(">=", false),
    /** Greater than. */
    GT(">", false),
    /** Not equal. */
    NE("<>", false),
    
    /** Any of. */
    ANY_OF("any of", true),
    /** None of. */
    NONE_OF("none of", true),
    
    /** String starts with. */
    STARTS_WITH("starts with", false),
    /** String ends with. */
    ENDS_WITH("ends with", false),
    /** String contains. */
    CONTAINS("contains", false);
    
    private String name;
    private boolean setOperator;
    
    private CriterionOperator(String name, boolean setOperator) {
        this.name = name;
        this.setOperator = setOperator;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
    
    /**
     * @return true if the operator applies to a set of values, false otherwise
     */
    public boolean isSetOperator() {
        return setOperator;
    }
}
