/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.filter.domain;

import org.apache.commons.lang.StringUtils;

import uk.gov.nationalarchives.droid.core.interfaces.filter.CriterionFieldEnum;
import uk.gov.nationalarchives.droid.core.interfaces.filter.CriterionOperator;
import uk.gov.nationalarchives.droid.core.interfaces.filter.FilterValue;

/**
 * Identification confidence metadata.
 * @author adash
 *
 */

public class IndentificationConfidanceMetadata extends GenericMetadata {

    private static final String HIGH = "HIGH";
    private static final String MEDIUM = "MEDIUM";
    private static final String LOW = "LOW";

    private static final String DISPLAY_NAME = "Identification Confidence";

    /**
     * Default constructor.
     */
    public IndentificationConfidanceMetadata() {
        super(CriterionFieldEnum.IDENTIFICATION_STATUS);

        addOperation(CriterionOperator.ANY_OF);
        addOperation(CriterionOperator.NONE_OF);
        addPossibleValue(new FilterValue(0, LOW, LOW));
        addPossibleValue(new FilterValue(1, MEDIUM, MEDIUM));
        addPossibleValue(new FilterValue(2, HIGH, HIGH));

    }

    @Override
    public boolean isFreeText() {
        return false;
    }

    @Override
    public void validate(String stringTovalidate) throws FilterValidationException {
        if (StringUtils.isBlank(stringTovalidate)) {
            throw new FilterValidationException("Identification confidance can not be blank");
        }
    }
    
}
