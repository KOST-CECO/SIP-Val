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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.droid.signature.PronomSignatureService.ProxySettings;

/**
 * @author rflitcroft
 *
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/spring-signature.xml")
public class PronomSignatureServiceTest {

    private static final String ENDPOINT_URL = "http://www.nationalarchives.gov.uk/pronom/service.asmx";
//    private static final String ENDPOINT_URL = "http://localhost:6666/pronom/service.asmx";
    
    private static final int PROXY_PORT = 8080;
//    private static final String PROXY_HOST = "localhost";
    private static final String PROXY_HOST = "wb-cacheclst1.web.local";
    
    @Autowired
    private PronomSignatureService importer;
    
    private File sigFileDir;
    
    @Before
    public void setup() throws Exception {

        sigFileDir = new File("tmp");
        FileUtils.forceDelete(sigFileDir);
        sigFileDir.mkdir();
        new File("tmp/DROID_SignatureFile_V28.xml").delete();
        importer.setEndpointUrl(ENDPOINT_URL);
        ProxySettings proxySettings = new ProxySettings();
        proxySettings.setEnabled(false);
        importer.setProxySettings(proxySettings);
    }
    
    @Test
    public void testGetSigFileFromRemoteWebServiceSavesFileLocallyViaProxy() {
        
        ProxySettings proxySettings = new ProxySettings();
        
        proxySettings.setProxyHost(PROXY_HOST);
        proxySettings.setProxyPort(PROXY_PORT);
        proxySettings.setProxyType("HTTP");
        proxySettings.setEnabled(true);
        
        importer.setProxySettings(proxySettings);
        
        
        SignatureFileInfo info = importer.importSignatureFile(sigFileDir);
        File file = new File("tmp/DROID_SignatureFile_V29.xml");
        assertTrue(file.exists());
        
        assertEquals(29, info.getVersion());
        assertEquals(false, info.isDeprecated());
    }

    @Test
    public void testGetLatestSigFileVersion() {
        SignatureFileInfo info = importer.getLatestVersion();
        
        assertEquals(29, info.getVersion());
        assertEquals(false, info.isDeprecated());
    }
    
    @Test
    public void testGetLatestSigFileVersionViaProxy() {
        
        ProxySettings proxySettings = new ProxySettings();
        
        proxySettings.setProxyHost(PROXY_HOST);
        proxySettings.setProxyPort(PROXY_PORT);
        proxySettings.setProxyType("HTTP");
        proxySettings.setEnabled(true);
        
        importer.setProxySettings(proxySettings);
        SignatureFileInfo info = importer.getLatestVersion();
        
        assertEquals(29, info.getVersion());
        assertEquals(false, info.isDeprecated());
    }

    @Test
    public void testGetSigFileFromRemoteWebServiceSavesFileLocally() {
        
        SignatureFileInfo info = importer.importSignatureFile(sigFileDir);
        File file = new File("tmp/DROID_SignatureFile_V29.xml");
        assertTrue(file.exists());
        
        assertEquals(29, info.getVersion());
        assertEquals(false, info.isDeprecated());
    }
    
}

