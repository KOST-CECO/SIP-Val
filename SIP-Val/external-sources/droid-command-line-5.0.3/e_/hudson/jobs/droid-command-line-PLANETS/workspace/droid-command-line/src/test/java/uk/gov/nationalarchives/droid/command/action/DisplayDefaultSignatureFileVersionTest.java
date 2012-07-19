/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.command.action;

import java.io.File;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;

/**
 * @author rflitcroft
 *
 */
public class DisplayDefaultSignatureFileVersionTest {

    private DisplayDefaultSignatureFileVersionCommand command;
    private PrintWriter printWriter;
    private SignatureManager signatureManager;
    
    @Before
    public void setup() {
        command = new DisplayDefaultSignatureFileVersionCommand();
        
        printWriter = mock(PrintWriter.class);
        command.setPrintWriter(printWriter);
        
        signatureManager = mock(SignatureManager.class);
        command.setSignatureManager(signatureManager);
    }
    
    @Test
    public void testDisplayDefaultSignatureFileVersion() throws Exception {
        File sigFile = new File("foo/bar/mySigFile.xml");
        SignatureFileInfo sigFileInfo = new SignatureFileInfo(69, false);
        sigFileInfo.setFile(sigFile);
        
        when(signatureManager.getDefaultSignature()).thenReturn(sigFileInfo);
        
        command.execute();
        
        verify(printWriter).println("Version: 69  File name: mySigFile.xml");
    }
}
