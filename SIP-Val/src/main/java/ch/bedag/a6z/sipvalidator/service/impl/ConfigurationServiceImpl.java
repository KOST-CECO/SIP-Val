package ch.bedag.a6z.sipvalidator.service.impl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import ch.bedag.a6z.sipvalidator.SipValidator;
import ch.bedag.a6z.sipvalidator.logging.Logger;
import ch.bedag.a6z.sipvalidator.service.ConfigurationService;
import ch.bedag.a6z.sipvalidator.service.TextResourceService;
import ch.bedag.a6z.sipvalidator.service.vo.ValidatedFormat;

public class ConfigurationServiceImpl implements ConfigurationService {
    
    private static final Logger LOGGER = new Logger(ConfigurationServiceImpl.class);
    XMLConfiguration config = null;
    private TextResourceService textResourceService;

    public TextResourceService getTextResourceService() {
        return textResourceService;
    }
    public void setTextResourceService(TextResourceService textResourceService) {
        this.textResourceService = textResourceService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getAllowedXsdFileNames() {
        List<String> result = new ArrayList<String>();

        Object prop = getConfig().getProperty("allowedxsdfiles.allowedxsdfile.filename");
        
        if (prop instanceof ArrayList<?>) {
            result = (List<String>) prop;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> getAllowedPuids() {

        Map<String, String> result = new HashMap<String, String>();
       
        List<HierarchicalConfiguration> fields = getConfig().configurationsAt("allowedformats.allowedformat");
        for(Iterator<HierarchicalConfiguration> it = fields.iterator(); it.hasNext();) {
            HierarchicalConfiguration sub = it.next();
            // sub contains now all data about a single field
            String fieldPuid = sub.getString("puid");
            String fieldExt  = sub.getString("extension");
            result.put(fieldPuid, fieldExt);
        }
        return result;
    }

    @Override
    public Integer getMaximumPathLength() {
        Object prop = getConfig().getProperty("allowedlengthofpaths");
        if (prop instanceof String) {
            String value = (String) prop;
            Integer intValue = new Integer(value);
            return intValue;
        }
        return null;
    }

    @Override
    public Integer getMaximumFileLength() {
        Object prop = getConfig().getProperty("allowedlengthoffiles");
        if (prop instanceof String) {
            String value = (String) prop;
            Integer intValue = new Integer(value);
            return intValue;
        }
        return null;
    }

    private XMLConfiguration getConfig(){
        if (this.config == null) {
            
            try {
                
                String path = "configuration/sipvalidator.conf.xml";

                URL locationOfJar = SipValidator.class.getProtectionDomain().getCodeSource().getLocation();
                String locationOfJarPath = locationOfJar.getPath();
                
                if (locationOfJarPath.endsWith(".jar")) {
                    File file = new File(locationOfJarPath);
                    String fileParent = file.getParent();
                    path = fileParent + "/" + path;
                }
                
                config = new XMLConfiguration(path);

            } catch (ConfigurationException e) {                
                LOGGER.logError(getTextResourceService().getText(MESSAGE_CONFIGURATION_ERROR_1));
                LOGGER.logError(getTextResourceService().getText(MESSAGE_CONFIGURATION_ERROR_2));
                LOGGER.logError(getTextResourceService().getText(MESSAGE_CONFIGURATION_ERROR_3));
                System.exit(0);
            }
        }
        return config;
    }

    @Override
    public String getPathOfDroidSignatureFile() throws MalformedURLException {

        String pathSignature = "configuration/" + getNameOfDroidSignatureFile();        
        
        URL locationOfJar = SipValidator.class.getProtectionDomain().getCodeSource().getLocation();
        String locationOfJarPath = locationOfJar.getPath();
        
        if (locationOfJarPath.endsWith(".jar")) {
            File file = new File(locationOfJarPath);
            String fileParent = file.getParent();
            pathSignature = fileParent + "/" + pathSignature;
        }        
        
        File fileSigfile = new File(pathSignature);        
        URL urlSigfile = fileSigfile.toURI().toURL();
        String result = urlSigfile.getFile();
        
        return result;
    }

    @Override
    public String getNameOfDroidSignatureFile() {
        Object prop = getConfig().getProperty("nameofdroidsignature");
        
        if (prop instanceof String) {
            String value = (String) prop;
            return value;
        }
        return null;
    }
    
    @Override
    public String getPathToPdftronExe() {
        Object prop = getConfig().getProperty("pathtopdftronexe");
        
        if (prop instanceof String) {
            String value = (String) prop;
            return value;
        }
        return null;
    }
    
    @Override
    public String getPathToPdftronOutputFolder() {
        Object prop = getConfig().getProperty("pathtopdftronoutput");
        
        if (prop instanceof String) {
            String value = (String) prop;
            return value;
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ValidatedFormat> getValidatedFormats() {

        List<ValidatedFormat> result = new ArrayList<ValidatedFormat>();

        List<HierarchicalConfiguration> fields = getConfig().configurationsAt("validatedformats.validatedformat");
        for(Iterator<HierarchicalConfiguration> it = fields.iterator(); it.hasNext();) {
            HierarchicalConfiguration sub = it.next();
            String pronomuniqueid = sub.getString("pronomuniqueid");
            String validator = sub.getString("validator");
            String extension = sub.getString("extension");
            String description = sub.getString("description");
            
            result.add(new ValidatedFormat(pronomuniqueid, validator, extension, description));
            
        }

        
        return result;
    }
    
    @Override
    public String getPathToWorkDir() {
        Object prop = getConfig().getProperty("pathtoworkdir");
        
        if (prop instanceof String) {
            String value = (String) prop;
            return value;
        }
        return null;
    }
    
    @Override
    public String getPathToJhoveJar() {
        Object prop = getConfig().getProperty("pathtojhovejar");
        
        if (prop instanceof String) {
            String value = (String) prop;
            return value;
        }
        return null;
    }
    
    @Override
    public String getPathToJhoveOutput() {
        Object prop = getConfig().getProperty("pathtojhoveoutput");
        
        if (prop instanceof String) {
            String value = (String) prop;
            return value;
        }
        return null;
    }

    @Override
    public String getPathToJhoveConfiguration() {
        Object prop = getConfig().getProperty("pathtojhoveconfig");
        
        if (prop instanceof String) {
            String value = (String) prop;
            return value;
        }
        return null;
    }
}
