/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.core;

import java.io.File;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.gov.nationalarchives.droid.core.interfaces.DroidCore;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationMethod;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationRequest;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultCollection;
import uk.gov.nationalarchives.droid.core.interfaces.IdentificationResultImpl;
import uk.gov.nationalarchives.droid.core.signature.droid4.AnalysisController;
import uk.gov.nationalarchives.droid.core.signature.droid4.FileFormatHit;
import uk.gov.nationalarchives.droid.core.signature.droid4.bytereader.ByteReader;
import uk.gov.nationalarchives.droid.core.signature.droid4.signaturefile.FFSignatureFile;

/**
 * Implementation of DroidCore which uses the Legacy DROID v.4 code to perform 
 * identifications.
 * @author rflitcroft
 *
 */
public class Droid4LegacyDroid implements DroidCore {

    private final Log log = LogFactory.getLog(getClass());

    private FFSignatureFile sigFile;
    private SignatureFileParser sigFileParser = new SignatureFileParser();
    private URI signatureFile;
    
    /**
     * Default constructor.
     */
    public Droid4LegacyDroid() { }
    
    /**
     * Initialses this drod core with its signature file.
     */
    public void init() {
        sigFile = sigFileParser.parseSigFile(signatureFile.getPath());
        sigFile.prepareForUse();
    }
    
    /**
     * Sets the signature file.
     * @param signatureFile the signature file to set
     */
    @Override
    public void setSignatureFile(String signatureFile) {
        this.signatureFile = new File(signatureFile).toURI();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public IdentificationResultCollection submit(IdentificationRequest request) {
        IdentificationResultCollection results = new IdentificationResultCollection(request);
        results.setRequestMetaData(request.getRequestMetaData());
        ByteReader byteReader = new IdentificationRequestByteReaderAdapter(request);
        
        sigFile.runFileIdentification(byteReader);
        
        results.start();
        
        for (int i = 0; i < byteReader.getNumHits(); i++) {
            FileFormatHit hit = byteReader.getHit(i);
            
            IdentificationResultImpl result = new IdentificationResultImpl();
            result.setMimeType(hit.getMimeType());
            result.setName(hit.getFileFormatName());
            result.setPuid(hit.getFileFormatPUID());
            
            switch (hit.getHitType()) {
                case AnalysisController.HIT_TYPE_POSITIVE_GENERIC:
                    result.setMethod(IdentificationMethod.BINARY_SIGNATURE);
                    break;
                case AnalysisController.HIT_TYPE_POSITIVE_SPECIFIC:
                    result.setMethod(IdentificationMethod.BINARY_SIGNATURE);
                    break;
                case AnalysisController.HIT_TYPE_TENTATIVE:
                    result.setMethod(IdentificationMethod.EXTENSION);
                    break;
                case AnalysisController.HIT_TYPE_POSITIVE_GENERIC_OR_SPECIFIC:
                    result.setMethod(IdentificationMethod.BINARY_SIGNATURE);
                    break;
                default:
                    // FIXME: Do nothing (!!!)
            }
            
            log.debug(String.format("PUID = %s; name = %s; version = %s; type = %s",
                    hit.getFileFormatPUID(),
                    hit.getFileFormatName(),
                    hit.getFileFormatVersion(),
                    hit.getHitTypeVerbose()));

            results.addResult(result);
        }
        
        results.stop();
        
        results.setFileLength(request.size());
        results.setRequestMetaData(request.getRequestMetaData());
        
        return results;
    }
    
}
