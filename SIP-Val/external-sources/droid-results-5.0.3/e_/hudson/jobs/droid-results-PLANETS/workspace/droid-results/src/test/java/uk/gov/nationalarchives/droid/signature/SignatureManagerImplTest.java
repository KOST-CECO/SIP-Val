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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import uk.gov.nationalarchives.droid.profile.config.DroidGlobalConfig;
import uk.gov.nationalarchives.droid.profile.config.DroidGlobalProperty;

/**
 * @author rflitcroft
 *
 */
public class SignatureManagerImplTest {

    private SignatureManagerImpl signatureManager;
    private DroidGlobalConfig config;
    private PropertiesConfiguration configuration;
    
    @Before
    public void setup() {
        signatureManager = new SignatureManagerImpl();
        config = mock(DroidGlobalConfig.class);
        when(config.getSignatureFileDir()).thenReturn(new File("test_sig_files"));
        
        configuration = mock(PropertiesConfiguration.class);
        when(config.getProperties()).thenReturn(configuration);
        signatureManager.setConfig(config);
    }
    
    @Test
    public void testGetAvailableSignatureFiles() {
        
        
        Map<String, SignatureFileInfo> infos = signatureManager.getAvailableSignatureFiles();
        assertEquals(16, infos.get("DROID_SignatureFile_V16").getVersion());
        assertEquals(26, infos.get("DROID_SignatureFile_V26").getVersion());
        assertEquals(16, infos.get("malformed").getVersion());

        assertEquals(3, infos.size());
    }
    
    @Test
    public void testCheckForNewSignatureFileWhenNewFileIsAvailable() throws SignatureManagerException {
        
        PronomSignatureService signatureService = mock(PronomSignatureService.class);
        SignatureFileInfo signatureFileInfo = new SignatureFileInfo(666, false);
        
        when(signatureService.getLatestVersion()).thenReturn(signatureFileInfo);
        
        signatureManager.setPronomService(signatureService);
        
        SignatureFileInfo newSignature = signatureManager.getLatestSignatureFile();
        assertNotNull(newSignature);
        
        assertEquals(666, newSignature.getVersion());
        assertEquals(false, newSignature.isDeprecated());
        
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(configuration).setProperty(eq(DroidGlobalProperty.LAST_UPDATE_CHECK.getName()), 
                captor.capture());
        
        assertThat((double) captor.getValue(), Matchers.closeTo(System.currentTimeMillis(), 200L));

    }

    @Test
    public void testCheckForNewSignatureFileWhenNewFileIsNotAvailable() throws SignatureManagerException {
        
        PronomSignatureService signatureService = mock(PronomSignatureService.class);
        SignatureFileInfo signatureFileInfo = new SignatureFileInfo(26, false);
        
        when(signatureService.getLatestVersion()).thenReturn(signatureFileInfo);
        
        signatureManager.setPronomService(signatureService);
        
        SignatureFileInfo newSignature = signatureManager.getLatestSignatureFile();
        assertNull(newSignature);
        
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(configuration).setProperty(eq(DroidGlobalProperty.LAST_UPDATE_CHECK.getName()), 
                captor.capture());
        
        assertThat((double) captor.getValue(), Matchers.closeTo(System.currentTimeMillis(), 50L));
        
    }
    
    @Test
    public void testDownloadLatest() throws SignatureManagerException {
        PronomSignatureService signatureService = mock(PronomSignatureService.class);
        signatureManager.setPronomService(signatureService);
        
        signatureManager.downloadLatest();
        verify(signatureService).importSignatureFile(config.getSignatureFileDir());
    }
}
