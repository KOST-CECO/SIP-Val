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
import java.util.SortedMap;
import java.util.TreeMap;

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
public class ListAllSignatureFilesCommandTest {

    private ListAllSignatureFilesCommand command;
    private SignatureManager signatureManager;
    private PrintWriter printWriter;
    
    @Before
    public void setup() {
        command = new ListAllSignatureFilesCommand();
        
        printWriter = mock(PrintWriter.class);
        command.setPrintWriter(printWriter);
        
        signatureManager = mock(SignatureManager.class);
        command.setSignatureManager(signatureManager);
        
    }
    
    @Test
    public void testExecuteWithSignatureFilesPresent() {
        SignatureFileInfo info1 = new SignatureFileInfo(33, false);
        info1.setFile(new File("foo/bar/version_33.xml"));
        SignatureFileInfo info2 = new SignatureFileInfo(45, false);
        info2.setFile(new File("foo/bar/version_45.xml"));
        SignatureFileInfo info3 = new SignatureFileInfo(78, true);
        info3.setFile(new File("foo/bar/version_78.xml"));
        
        SortedMap<String, SignatureFileInfo> sigFiles = new TreeMap<String, SignatureFileInfo>();
        sigFiles.put("33", info1);
        sigFiles.put("45", info2);
        sigFiles.put("78", info3);
        
        when(signatureManager.getAvailableSignatureFiles()).thenReturn(sigFiles);
        
        command.execute();
        
        verify(printWriter).println("Version: 33  File name: version_33.xml");
        verify(printWriter).println("Version: 45  File name: version_45.xml");
        verify(printWriter).println("Version: 78  File name: version_78.xml");
        
    }

    @Test
    public void testExecuteWithNoSignatureFilesPresent() {
        SortedMap<String, SignatureFileInfo> sigFiles = new TreeMap<String, SignatureFileInfo>();

        when(signatureManager.getAvailableSignatureFiles()).thenReturn(sigFiles);
        
        command.execute();
        
        verify(printWriter).println("No signature files available");
    }
}
