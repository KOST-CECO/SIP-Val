/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.command.action;

import java.io.PrintWriter;
import java.net.UnknownHostException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;
import uk.gov.nationalarchives.droid.signature.SignatureManagerException;

/**
 * @author rflitcroft
 *
 */
public class CheckSignatureUpdateCommandTest {

    private CheckSignatureUpdateCommand command;
    private SignatureManager signatureManager;
    private PrintWriter printWriter;

    @Before
    public void setup() {
        command = new CheckSignatureUpdateCommand();
        
        signatureManager = mock(SignatureManager.class);
        command.setSignatureManager(signatureManager);
        
        printWriter = mock(PrintWriter.class);
        command.setPrintWriter(printWriter);
    }
    
    @Test
    public void testCheckSignatureUpdateWhenUpdateIsAvailable() throws Exception {
        
        SignatureFileInfo sigFileInfo = new SignatureFileInfo(69, false);
        when(signatureManager.getLatestSignatureFile()).thenReturn(sigFileInfo);
        
        command.execute();
        
        verify(printWriter).println("Signature update version 69 is available");
    }
    
    
    @Test
    public void testCheckSignatureUpdateWhenNoUpdateIsAvailable() throws Exception {
        
        when(signatureManager.getLatestSignatureFile()).thenReturn(null);

        command.execute();
        
        verify(printWriter).println("No signature updates are available");
    }
    
    @Test
    public void testCheckSignatureUpdateWhenConnectionFails() throws SignatureManagerException {
        
        Throwable cause = new UnknownHostException("proxy");
        
        SignatureManagerException e = new SignatureManagerException(new RuntimeException("Failed", cause));
        when(signatureManager.getLatestSignatureFile()).thenThrow(e);
        
        try {
            command.execute();
            fail("Expected CommandExecutionEception");
        } catch (CommandExecutionException cee) {
            assertEquals("Error checking for signature update: Failed\n"
                    + "Caused by java.net.UnknownHostException: proxy", cee.getMessage());
        }
        
    }
    
}
