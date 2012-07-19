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
import java.io.FileFilter;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import uk.gov.nationalarchives.droid.profile.config.DroidGlobalConfig;
import uk.gov.nationalarchives.droid.profile.config.DroidGlobalProperty;
import uk.gov.nationalarchives.droid.signature.PronomSignatureService.ProxySettings;

/**
 * @author rflitcroft
 *
 */
public class SignatureManagerImpl implements SignatureManager {

    private static final String INVALID_SIGNATURE_FILE = "Invalid signature file [%s]";

    private final Log log = LogFactory.getLog(getClass());
    
    private DroidGlobalConfig config;
    private PronomSignatureService pronomService;
    
    /**
     * Initailisation post-construct.
     */
    public void init() {
        config.getProperties().addConfigurationListener(pronomService);
        Configuration configuration = config.getProperties();
        ProxySettings proxySettings = new ProxySettings();
        proxySettings.setEnabled(configuration.getBoolean(DroidGlobalProperty.UPDATE_USE_PROXY.getName()));
        proxySettings.setProxyHost(configuration.getString(DroidGlobalProperty.UPDATE_PROXY_HOST.getName()));
        proxySettings.setProxyPort(configuration.getInt(DroidGlobalProperty.UPDATE_PROXY_PORT.getName()));
        proxySettings.setProxyType(configuration.getString(DroidGlobalProperty.UPDATE_PROXY_TYPE.getName()));
        proxySettings.setEnabled(configuration.getBoolean(DroidGlobalProperty.UPDATE_USE_PROXY.getName()));
        pronomService.setProxySettings(proxySettings);
        pronomService.setEndpointUrl(configuration.getString(DroidGlobalProperty.UPDATE_URL.getName()));
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public SortedMap<String, SignatureFileInfo> getAvailableSignatureFiles() {
        File sigFileDir = config.getSignatureFileDir();
        
        
        File[] files = sigFileDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && "xml".equals(FilenameUtils.getExtension(pathname.getName()));
            }
        });
        
        SignatureInfoParser parser = new SignatureInfoParser();
        SortedMap<String, SignatureFileInfo> sigFiles = new TreeMap<String, SignatureFileInfo>();
        
        for (File file : files) {
            String fileName = FilenameUtils.getBaseName(file.getName());
            try {
                sigFiles.put(fileName, forFile(file, parser));
            } catch (SignatureFileException e) {
                log.warn(String.format("Unreadable signature file [%s]", file), e);
            }
        }
        
        return sigFiles;
    }
    
    private static SignatureFileInfo forFile(File file, SignatureInfoParser parser) throws SignatureFileException {
        final SignatureFileInfo signatureFileInfo = parser.parse(file);
        signatureFileInfo.setFile(file);
        return signatureFileInfo;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(DroidGlobalConfig config) {
        this.config = config;
    }
    
    private static final class SignatureInfoParser {

        SignatureFileInfo parse(File sigFile) throws SignatureFileException {
        
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            final SignatureFileVersionHandler handler = new SignatureFileVersionHandler();
            try {
                SAXParser saxParser = saxParserFactory.newSAXParser();
                saxParser.parse(sigFile, handler);
                throw new SignatureFileException(String.format(
                        INVALID_SIGNATURE_FILE, sigFile), ErrorCode.INAVID_SIGNATURE_FILE);
            } catch (ValidSignatureFileException e) {
                return e.getInfo();
            } catch (SAXException e) {
                throw new SignatureFileException(String.format(
                        INVALID_SIGNATURE_FILE, sigFile), e,
                        ErrorCode.INAVID_SIGNATURE_FILE);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e.getMessage(), e);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
    
    /**
     * Handler for &lt;FFSignatureFile&gt; elements.
     * 
     */
    private static final class SignatureFileVersionHandler extends DefaultHandler {

        private static final String ROOT_ELEMENT = "FFSignatureFile";

        @Override
        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {

            if (ROOT_ELEMENT.equals(qName)) {
                int version = Integer.valueOf(attributes.getValue("Version"));
                SignatureFileInfo info = new SignatureFileInfo(version, false);
                throw new ValidSignatureFileException(info);
            }
            
            throw new SAXException(
                    String.format("Invalid signature file - root element was not [%s]", ROOT_ELEMENT));
        }
        
    }
    
    /**
     * Exception thrown purely to stop SAX parsing when a valid signature file is found.
     * @author rflitcroft
     *
     */
    private static final class ValidSignatureFileException extends RuntimeException {

        private static final long serialVersionUID = 5955330716555328779L;
        private final SignatureFileInfo info;
        
        /**
         * @param info the signature file info
         */
        ValidSignatureFileException(SignatureFileInfo info) {
            this.info = info;
        }
        
        /**
         * @return the info
         */
        public SignatureFileInfo getInfo() {
            return info;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SignatureFileInfo getLatestSignatureFile() throws SignatureManagerException {
        try {
            final SignatureFileInfo latest = pronomService.getLatestVersion();
            final Configuration properties = config.getProperties();
            properties.setProperty(DroidGlobalProperty.LAST_UPDATE_CHECK.getName(), System.currentTimeMillis());
            return getAvailableSignatureFiles().values().contains(latest) ? null : latest;
        // CHECKSTYLE:OFF
        } catch (RuntimeException e) {
            // CHECKSTYLE:ON
            log.error(e);
            throw new SignatureManagerException(e);
        }
    }
    
    /**
     * @param pronomService the pronomService to set
     */
    public void setPronomService(PronomSignatureService pronomService) {
        this.pronomService = pronomService;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SignatureFileInfo downloadLatest() throws SignatureManagerException {
        try {
            SignatureFileInfo info = pronomService.importSignatureFile(config.getSignatureFileDir());
            final String autoSetDefaultSigFile = DroidGlobalProperty.UPDATE_AUTOSET_DEFAULT.getName();
            final PropertiesConfiguration properties = config.getProperties();
            if (properties.getBoolean(autoSetDefaultSigFile)) {
                properties.setProperty(DroidGlobalProperty.DEFAULT_SIG_FILE_VERSION.getName(), 
                        FilenameUtils.getBaseName(info.getFile().getName()));
                try {
                    config.getProperties().save();
                } catch (ConfigurationException e) {
                    log.error(e);
                    throw new SignatureManagerException(e);
                }
            }
            return info;
        // CHECKSTYLE:OFF
        } catch (RuntimeException e) {
            // CHECKSTYLE:ON
            throw new SignatureManagerException(e);
        }
    }
    
    /**
     * {@inheritDoc}
     * @throws SignatureFileException 
     */
    @Override
    public SignatureFileInfo getDefaultSignature() throws SignatureFileException {
        final String defaultSigFileKey = config.getProperties()
            .getString(DroidGlobalProperty.DEFAULT_SIG_FILE_VERSION.getName());
        SignatureFileInfo sigFileInfo = getAvailableSignatureFiles().get(defaultSigFileKey);
        if (sigFileInfo == null) {
            String errorMessage = String.format(
                    "Default signature file %s could not be found. Please check your signature settings.",
                    defaultSigFileKey);
            throw new SignatureFileException(errorMessage, ErrorCode.FILE_NOT_FOUND);
        }
        return sigFileInfo;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SignatureFileInfo upload(File signatureFile, boolean setDefault) throws SignatureFileException {
        
        SignatureInfoParser parser = new SignatureInfoParser();
        SignatureFileInfo sigFileInfo = forFile(signatureFile, parser);

        try {
            FileUtils.copyFileToDirectory(signatureFile, config.getSignatureFileDir(), true);
            File newSignatureFile = new File(config.getSignatureFileDir(), signatureFile.getName());
            
            sigFileInfo.setFile(newSignatureFile);
            
            if (setDefault) {
                config.getProperties().setProperty(DroidGlobalProperty.DEFAULT_SIG_FILE_VERSION.getName(), 
                        FilenameUtils.getBaseName(newSignatureFile.getName()));
            }

            return sigFileInfo;
        } catch (IOException e) {
            log.error(e);
            throw new SignatureFileException(e.getMessage(), e, ErrorCode.FILE_NOT_FOUND);
        }
    }
    
}
