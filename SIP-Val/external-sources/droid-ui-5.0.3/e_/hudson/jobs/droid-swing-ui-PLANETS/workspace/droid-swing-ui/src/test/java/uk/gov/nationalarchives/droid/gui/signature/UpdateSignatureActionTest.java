/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.signature;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;


/**
 * @author rflitcroft
 *
 */
public class UpdateSignatureActionTest {

    private CheckSignatureUpdateAction action;
    private SignatureManager signatureManager;
    
    @Before
    public void setup() {
        signatureManager = mock(SignatureManager.class);
        action = new CheckSignatureUpdateAction();
        action.setSignatureManager(signatureManager);
    }
    
    @Test
    public void testCheckForUpdatesWhenNewFileIsAvailable() throws Exception {
        
        SignatureFileInfo sigFileInfo = new SignatureFileInfo(666, true);
        when(signatureManager.getLatestSignatureFile()).thenReturn(sigFileInfo);
        
        action.execute();
        SignatureFileInfo availableUpdate = action.get();
        assertNotNull(availableUpdate);
        assertEquals(666, availableUpdate.getVersion());
    }
    
    @Test
    public void testCheckForUpdatesWhenNewFileIsNotAvailable() throws Exception {
        when(signatureManager.getLatestSignatureFile()).thenReturn(null);

        action.execute();
        SignatureFileInfo availableUpdate = action.get();
        assertNull(availableUpdate);
        assertNull(action.getSignatureFileInfo());
    }
    
//    @Test
//    public void testDownloadLatestSignatureFile() throws SignatureManagerException {
//        
//        action.downloadLatest();
//        
//        verify(signatureManager).downloadLatest();
//        
//    }
}
