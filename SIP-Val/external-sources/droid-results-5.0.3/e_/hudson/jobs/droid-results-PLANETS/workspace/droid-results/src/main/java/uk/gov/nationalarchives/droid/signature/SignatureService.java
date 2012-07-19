/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.signature;

import java.io.File;

import uk.gov.nationalarchives.droid.signature.PronomSignatureService.ProxySettings;

/**
 * @author rflitcroft
 *
 */
public interface SignatureService {

    /**
     * Imports a signature file.
     * @param targetDir the target directory for the signature file.
     * @return the signature file's meta-data.
     */
    SignatureFileInfo importSignatureFile(File targetDir);
    
    /**
     * Gets the latest version info from the signature registry via a proxy.
     * @return latest version info.
     */
    SignatureFileInfo getLatestVersion();

    /**
     * Sets the endpoint URL.
     * @param url the url to set
     */
    void setEndpointUrl(String url);

    /**
     * Updates the proxy settings.
     * @param proxySettings the proxy settings to update
     */
    void setProxySettings(ProxySettings proxySettings);

}
