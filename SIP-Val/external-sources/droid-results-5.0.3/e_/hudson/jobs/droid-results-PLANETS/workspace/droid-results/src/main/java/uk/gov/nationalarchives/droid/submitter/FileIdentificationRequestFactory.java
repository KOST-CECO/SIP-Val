/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.submitter;

import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.RequestIdentifier;
import uk.gov.nationalarchives.droid.core.interfaces.archive.IdentificationRequestFactory;
import uk.gov.nationalarchives.droid.core.interfaces.resource.FileSystemIdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.resource.RequestMetaData;

/**
 * @author rflitcroft
 *
 */
public class FileIdentificationRequestFactory implements IdentificationRequestFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    public IdentificationRequest newRequest(RequestMetaData metaData, RequestIdentifier identifier) {
        IdentificationRequest request = new FileSystemIdentificationRequest(metaData, identifier);
        return request;
    }
    
}
