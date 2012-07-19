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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.Test;

import uk.gov.nationalarchives.droid.profile.config.DroidGlobalConfig;
import uk.gov.nationalarchives.droid.profile.config.DroidGlobalProperty;
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;

/**
 * @author rflitcroft
 *
 */
public class ConfigureDefaultSignatureFileVersionCommandTest {

    private ConfigureDefaultSignatureFileVersionCommand command;
    private PrintWriter printWriter;
    private SignatureManager signatureManager;
    private DroidGlobalConfig globalConfig;
    
    @Before
    public void setup() {
        command = new ConfigureDefaultSignatureFileVersionCommand();
        
        printWriter = mock(PrintWriter.class);
        command.setPrintWriter(printWriter);
        
        signatureManager = mock(SignatureManager.class);
        command.setSignatureManager(signatureManager);
        
        globalConfig = mock(DroidGlobalConfig.class);
        command.setGlobalConfig(globalConfig);
    }
    
    @Test
    public void testExecuteWithAValidVersion() throws Exception {
        
        SignatureFileInfo info1 = new SignatureFileInfo(33, false);
        SignatureFileInfo info2 = new SignatureFileInfo(45, false);
        info2.setFile(new File("foo/bar/version_45.xml"));

        SortedMap<String, SignatureFileInfo> sigFileInfos = new TreeMap<String, SignatureFileInfo>();
        sigFileInfos.put("v33", info1);
        sigFileInfos.put("v45", info2);
        
        when(signatureManager.getAvailableSignatureFiles()).thenReturn(sigFileInfos);
        when(signatureManager.getDefaultSignature()).thenReturn(info2);
        
        PropertiesConfiguration properties = mock(PropertiesConfiguration.class);
        
        when(globalConfig.getProperties()).thenReturn(properties);
        
        command.setSignatureFileVersion(45);
        command.execute();
        
        verify(properties).setProperty(DroidGlobalProperty.DEFAULT_SIG_FILE_VERSION.getName(), "v45");
        verify(properties).save();
        
        verify(printWriter).println("Default signature file updated. Version: 45  File name: version_45.xml");
    }
    
    @Test
    public void testExecuteWithAnUnknownVersion() {
        
        SignatureFileInfo info1 = new SignatureFileInfo(33, false);
        SignatureFileInfo info2 = new SignatureFileInfo(45, false);

        SortedMap<String, SignatureFileInfo> sigFileInfos = new TreeMap<String, SignatureFileInfo>();
        sigFileInfos.put("v33", info1);
        sigFileInfos.put("v45", info2);
        
        when(signatureManager.getAvailableSignatureFiles()).thenReturn(sigFileInfos);
        
        command.setSignatureFileVersion(78);
        
        try {
            command.execute();
            fail("Expected CommandExecutionException");
        } catch (CommandExecutionException e) {
            assertEquals("Unknown signature file version: 78", e.getMessage());
        }
        
    }
}
