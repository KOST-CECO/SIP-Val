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
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import uk.gov.nationalarchives.droid.command.i18n.I18N;
import uk.gov.nationalarchives.droid.profile.config.DroidGlobalConfig;
import uk.gov.nationalarchives.droid.profile.config.DroidGlobalProperty;
import uk.gov.nationalarchives.droid.signature.SignatureFileException;
import uk.gov.nationalarchives.droid.signature.SignatureFileInfo;
import uk.gov.nationalarchives.droid.signature.SignatureManager;

/**
 * @author rflitcroft
 *
 */
public class ConfigureDefaultSignatureFileVersionCommand implements DroidCommand {

    private PrintWriter printWriter;
    private SignatureManager signatureManager;
    private DroidGlobalConfig globalConfig;
    
    private int signatureFileVersion;
    
    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public void execute() throws CommandExecutionException {
        
        
        boolean validVersion = false;
        Map<String, SignatureFileInfo> sigFileInfos = signatureManager.getAvailableSignatureFiles();
        for (Map.Entry<String, SignatureFileInfo> entry : sigFileInfos.entrySet()) {
            String key = entry.getKey();
            SignatureFileInfo info = entry.getValue();
            if (info.getVersion() == signatureFileVersion) {
                validVersion = true;
                updateDefaultVersion(key);
                break;
            }
        }

        if (!validVersion) {
            throw new CommandExecutionException(I18N.getResource(
                    I18N.CONFIGURE_SIGNATURE_FILE_VERSION_INVALID,
                    signatureFileVersion));
        }
    }

    /**
     * @throws CommandExecutionException
     */
    private void updateDefaultVersion(String key) throws CommandExecutionException {
        final PropertiesConfiguration properties = globalConfig.getProperties();
        properties.setProperty(DroidGlobalProperty.DEFAULT_SIG_FILE_VERSION.getName(),
                key);
        try {
            properties.save();
            SignatureFileInfo sigFileInfo = signatureManager.getDefaultSignature();
            printWriter.println(I18N.getResource(I18N.CONFIGURE_SIGNATURE_FILE_VERSION_SUCCESS,
                    sigFileInfo.getVersion(), sigFileInfo.getFile().getName()));
                    
                    
        } catch (ConfigurationException e) {
            throw new CommandExecutionException(e);
        } catch (SignatureFileException e) {
            throw new CommandExecutionException(e);
        }
    };
    
    /**
     * @param printWriter the printWriter to set
     */
    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }
    
    /**
     * @param signatureManager the signatureManager to set
     */
    public void setSignatureManager(SignatureManager signatureManager) {
        this.signatureManager = signatureManager;
    }
    
    /**
     * @param globalConfig the globalConfig to set
     */
    public void setGlobalConfig(DroidGlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
    }
    
    /**
     * @param signatureFileVersion the signatureFileVersion to set
     */
    public void setSignatureFileVersion(int signatureFileVersion) {
        this.signatureFileVersion = signatureFileVersion;
    }
}
