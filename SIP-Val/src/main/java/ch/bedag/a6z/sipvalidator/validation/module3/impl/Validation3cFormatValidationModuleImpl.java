package ch.bedag.a6z.sipvalidator.validation.module3.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.gov.nationalarchives.droid.core.signature.droid4.Droid;
import uk.gov.nationalarchives.droid.core.signature.droid4.FileFormatHit;
import uk.gov.nationalarchives.droid.core.signature.droid4.IdentificationFile;
import uk.gov.nationalarchives.droid.core.signature.droid4.signaturefile.FileFormat;
import ch.bedag.a6z.sipvalidator.enums.PronomUniqueIdEnum;
import ch.bedag.a6z.sipvalidator.exception.module3.Validation3cFormatValidationException;
import ch.bedag.a6z.sipvalidator.service.ConfigurationService;
import ch.bedag.a6z.sipvalidator.service.JhoveService;
import ch.bedag.a6z.sipvalidator.service.PdftronService;
import ch.bedag.a6z.sipvalidator.service.vo.ValidatedFormat;
import ch.bedag.a6z.sipvalidator.util.PdftronErrorCodes;
import ch.bedag.a6z.sipvalidator.util.Util;
import ch.bedag.a6z.sipvalidator.validation.ValidationModuleImpl;
import ch.bedag.a6z.sipvalidator.validation.module3.Validation3cFormatValidationModule;

public class Validation3cFormatValidationModuleImpl extends ValidationModuleImpl implements Validation3cFormatValidationModule {

    private PdftronService pdftronService;
    private ConfigurationService configurationService;
    private JhoveService jhoveService;
    
    public static String NEWLINE = System.getProperty("line.separator"); 
    
    public PdftronService getPdftronService() {
        return pdftronService;
    }

    public void setPdftronService(PdftronService pdftronService) {
        this.pdftronService = pdftronService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    
    public void setJhoveService(JhoveService jhoveService) {
        this.jhoveService = jhoveService;
    }

    public JhoveService getJhoveService() {
        return jhoveService;
    }

    @Override
    public boolean validate(File sipDatei) throws Validation3cFormatValidationException {
        
        boolean isValid = true;
        
        Map<String, File> filesInSipFile = new HashMap<String, File>();
        
        Map<String, ValidatedFormat> mapValidatedFormats = new HashMap<String, ValidatedFormat>();
        List<ValidatedFormat> validatedFormats = getConfigurationService().getValidatedFormats();
        for (Iterator<ValidatedFormat> iterator = validatedFormats.iterator(); iterator.hasNext();) {
            ValidatedFormat validatedFormat = iterator.next();
            mapValidatedFormats.put(validatedFormat.getPronomUniqueId(), validatedFormat);
        }
        
        String nameOfSignature = getConfigurationService().getNameOfDroidSignatureFile();
        if (nameOfSignature == null) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    getTextResourceService().getText(MESSAGE_CONFIGURATION_ERROR_NO_SIGNATURE));                                
            return false;
        }
        
        Droid droid = null;
        try {
            Util.switchOffConsole();
            droid = new Droid();
            
            String pathOfDroidConfig = getConfigurationService().getPathOfDroidSignatureFile();            
            droid.readSignatureFile(pathOfDroidConfig);
        }
        catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    getTextResourceService().getText(ERROR_CANNOT_INITIALIZE_DROID));                                
            return false;
        } finally {
            Util.switchOnConsole();            
        }
        
       
        
        // TODO: es wäre viel besser, wenn die DROID Identifikation auch über Streams statt über Files
        // durchgeführt werden könnte. Noch keine Ahnung, ob und wie das möglich ist. Die Dokumentation
        // zu Droid ist quasi nicht vorhanden.
        
        // Die Archivdatei wurde bereits vom Schritt 1d in das Arbeitsverzeichnis entpackt
        int countContentFiles = 0;
        String pathToWorkDir = getConfigurationService().getPathToWorkDir();
        File workDir = new File(pathToWorkDir);
        Map<String, File> fileMap = Util.getFileMap(workDir, true);
        Set<String> fileMapKeys = fileMap.keySet();
        for (Iterator<String> iterator = fileMapKeys.iterator(); iterator.hasNext();) {
            String entryName = iterator.next();
            File newFile = fileMap.get(entryName);
          
            if (!newFile.isDirectory() && newFile.getAbsolutePath().contains("\\content\\")) {
                filesInSipFile.put(newFile.getAbsolutePath(), newFile);
                countContentFiles++;
            }
        }
        
        getMessageService().logError(
                getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                getTextResourceService().getText(MESSAGE_DASHES) + 
                String.valueOf(countContentFiles) + 
                " " +
                getTextResourceService().getText(MESSAGE_MODULE_CD_NUMBER_OF_CONTENT_FILES));                                

        
        
        List<String> filesToProcessWithJhove = new ArrayList<String>();
        List<String> filesToProcessWithPdftron = new ArrayList<String>();
        
        
        Set<String> fileKeys = filesInSipFile.keySet();
        
        for (Iterator<String> iterator = fileKeys.iterator(); iterator.hasNext();) {
            String fileKey = iterator.next();
            File file = filesInSipFile.get(fileKey);
            
            // eine der PUIDs des archivierten Files muss in der Konfiguration als validatedformat vorkommen,
            // diese Konfiguration bestimmt, ob ein File selektiert wird zur Format-Validierung mit JHOVE oder Pdftron
            boolean selected = false;
            ValidatedFormat value = null;
            
            IdentificationFile ifile = droid.identify(file.getAbsolutePath());
                        
            for (int x = 0; x < ifile.getNumHits(); x++) {
                FileFormatHit ffh = ifile.getHit(x);
                FileFormat ff = ffh.getFileFormat();
                String puid = ff.getPUID();
                                
                value = mapValidatedFormats.get(puid);
                
                if (value != null) {
                    selected = true;
                    break;
                }
            }

            // die PUID des SIP-Files wurde in der Liste der zu validierenden Formate (gemäss Konfigurationsdatei) gefunden
            if (selected) {
                // in der Konfiguration wird bestimmt, welcher PUID-Typ mit welchem Validator (JHOVe oder Pdftron)
                // untersucht wird
                if (value.getValidator().equals(PronomUniqueIdEnum.PDFTRON.name())) {                                        
                    filesToProcessWithPdftron.add(fileKey);
                } else if (value.getValidator().equals(PronomUniqueIdEnum.JHOVE.name())) {
                    filesToProcessWithJhove.add(fileKey);                    
                }
            }
        }
        
        // alt: alle Files, die mit JHove verarbeitet werden, bulk-mässig an die Applikation übergeben
        // neu: die Files werden nach Typ sortiert und aufgeteilt, also z.B. alle wav werden zusammen
        // übergeben, dann all pdf etc.
        // Die Outputs werden in einem einzigen File konkatiniert.
       
        Map<String, StringBuffer> extensionsMap = new HashMap<String, StringBuffer>();
        
        for (String pathToProcessWithJhove : filesToProcessWithJhove) {
            int idxLastDot = pathToProcessWithJhove.lastIndexOf(".");
            String extension = pathToProcessWithJhove.substring(idxLastDot + 1);            
            StringBuffer sbPath = extensionsMap.get(extension);
            if (sbPath == null) {
                sbPath = new StringBuffer("\"");
                sbPath.append(pathToProcessWithJhove);
            } else {
                sbPath.append("\"");
                sbPath.append(pathToProcessWithJhove);
            }
            sbPath.append("\" ");
            extensionsMap.put(extension, sbPath);
        }
        
        File jhoveReport = null;
        
        Map<String, Integer> countPerExtensionValid = new HashMap<String, Integer>();
        Map<String, Integer> countPerExtensionInvalid = new HashMap<String, Integer>();
        
        StringBuffer concatenatedOutputs = new StringBuffer();
        
        String pathToJhoveJar = getConfigurationService().getPathToJhoveJar();
        String pathToJhoveOutput = getConfigurationService().getPathToJhoveOutput();
        
        Set<String> extMapKeys = extensionsMap.keySet();
        for (String extMapKey : extMapKeys) {
            
            StringBuffer pathsJhove = extensionsMap.get(extMapKey);
                    
            try {
                jhoveReport = getJhoveService().executeJhove(
                        pathToJhoveJar, pathsJhove.toString(), pathToJhoveOutput, sipDatei.getName(), extMapKey);
                                
                BufferedReader in = new BufferedReader(new FileReader(jhoveReport));
                String line;
                while ((line = in.readLine()) != null) {
                    
                    concatenatedOutputs.append(line);
                    concatenatedOutputs.append(NEWLINE);
                    
                    // die Status-Zeile enthält eine von diesen beiden Möglichkeiten:
                    // Status: Well-Formed and valid
                    // Status: Not well-formed 
                    if (line.contains("Status:")) {
                        if (line.contains("Not well-formed")) {
                            Integer countInvalid = countPerExtensionInvalid.get(extMapKey);
                            if (countInvalid == null) {
                                countPerExtensionInvalid.put(extMapKey, new Integer(1));
                            } else {
                                countInvalid = countInvalid + 1;
                                countPerExtensionInvalid.put(extMapKey, countInvalid);
                            }
                            isValid = false;
                            
                        } else {
                            Integer countValid = countPerExtensionValid.get(extMapKey);
                            if (countValid == null) {
                                countPerExtensionValid.put(extMapKey, new Integer(1));
                            } else {
                                countValid = countValid + 1;
                                countPerExtensionValid.put(extMapKey, countValid);
                            }
                        }
                    }
                }
                in.close();
                
            } catch (Exception e) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        e.getMessage());                                
                return false;
            }

        }
        
        
        // die im StringBuffer konkatinierten Outputs der einzelnen JHove-Verarbeitungen
        // wieder in das Output-File zurückschreiben
        if (jhoveReport != null) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(jhoveReport));
                out.write(concatenatedOutputs.toString());
                out.close();
                Util.setPathToReportJHove(jhoveReport.getAbsolutePath());
                
            } catch (IOException e) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                        getTextResourceService().getText(MESSAGE_DASHES) +
                        getTextResourceService().getText(MESSAGE_MODULE_CC_CANNOTWRITEJHOVEREPORT));
                return false;
            }
        }
        
        
        Set<String> validKeys = countPerExtensionValid.keySet();
        for (String validKey : validKeys) {
            Integer valid = countPerExtensionValid.get(validKey);
            if (valid == null) {
                valid = new Integer(0);
            }
            Integer invalid = countPerExtensionInvalid.get(validKey);
            if (invalid == null) {
                invalid = new Integer(0);
            }
            
            String msg = validKey + " Valid = " + valid.toString() + ", Invalid = " + invalid.toString();
            
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + msg);
        }
        
        
        Set<String> invalidKeys = countPerExtensionInvalid.keySet();
        for (String invalidKey : invalidKeys) {
            Integer invalid = countPerExtensionInvalid.get(invalidKey);
            if (invalid == null) {
                invalid = new Integer(0);
            }
            Integer valid = countPerExtensionValid.get(invalidKey);
            if (valid == null) {
                valid = new Integer(0);
            }
            
            String msg = invalidKey + " Valid = " + valid.toString() + ", Invalid = " + invalid.toString();
            
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + msg);
        }
        
        // alle Files, die mit Pdftron verarbeitet werden, bulk-mässig an die Applikation übergeben
        if (filesToProcessWithPdftron.size() > 0) {
    
            StringBuffer pathsPdftron = new StringBuffer();
            for (String pathToProcessWithPdftron : filesToProcessWithPdftron) {
                pathsPdftron.append("\"");
                pathsPdftron.append(pathToProcessWithPdftron);
                pathsPdftron.append("\"");
                pathsPdftron.append(" ");
            }
            
            String pathToPdftronExe = getConfigurationService().getPathToPdftronExe();
            getPdftronService().setPathToPdftronExe(pathToPdftronExe);
            String pathToPdftronOutput = getConfigurationService().getPathToPdftronOutputFolder();
            
            try {
                String pathToPdftronReport = getPdftronService().executePdftron(
                        pathToPdftronExe, pathsPdftron.toString(),  pathToPdftronOutput, sipDatei.getName());
                
                
                Util.setPathToReportPdftron(pathToPdftronReport);
                
                // aus dem Output von Pdftron die Fehlercodes extrahieren und übersetzen
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(pathToPdftronReport));
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(bis);
                doc.normalize();
                NodeList nodeLst = doc.getElementsByTagName("Error");
    
                Map<String, Integer> errorCounter = new HashMap<String, Integer>();
                
                // Bsp. für einen Error Code: <Error Code="e_PDFA173" 
                // die erste Ziffer nach e_PDFA ist der Error Code.
                for (int s = 0; s < nodeLst.getLength(); s++) {
                    Node dateiNode = nodeLst.item(s);
                    NamedNodeMap nodeMap = dateiNode.getAttributes();
                    Node errorNode = nodeMap.getNamedItem("Code");
                    String errorCode = errorNode.getNodeValue();
                    String errorDigit = errorCode.substring(6, 7);
                    
                    // der Error Code kann auch "Unknown" sein, dieser wird in den Code "0" übersetzt
                    if (errorDigit.equals("U")) {
                        errorDigit = "0";
                    }
                    
                    Integer errorCount = errorCounter.get(errorDigit);
                    if (errorCount == null) {
                        errorCounter.put(errorDigit, 1);
                    } else {
                        errorCount = errorCount + 1;
                        errorCounter.put(errorDigit, errorCount);
                    }
                }
                    
                StringBuffer errorSummary = new StringBuffer();
                errorSummary.append(getTextResourceService().getText(MESSAGE_MODULE_CC_ERRORS_IN));
                Integer totalErrors = new Integer(0);
                
                List<String> sortedKeys = new ArrayList<String>();
                sortedKeys.addAll(errorCounter.keySet());
                Collections.sort(sortedKeys);
                
                for (Iterator<String> iterator = sortedKeys.iterator(); iterator.hasNext();) {
                    String errorCounterKey = iterator.next();
                                        
                    Integer value = errorCounter.get(errorCounterKey);
                    totalErrors = totalErrors + value;
                    
                    errorSummary.append(value.toString());
                    errorSummary.append(" ");
                    errorSummary.append(PdftronErrorCodes.getErrorLabel(errorCounterKey));
                    if (iterator.hasNext()) {
                        errorSummary.append(", ");
                    } else {
                        errorSummary.append(")");
                    }
                }
                
                int iValidPdfs = filesToProcessWithPdftron.size() - totalErrors;
                String sValidPdfs = "pdf Valid = " + iValidPdfs + ", ";
                
                if (totalErrors == 0) {
                    errorSummary = new StringBuffer(
                            sValidPdfs +
                            getTextResourceService().getText(MESSAGE_MODULE_CC_INVALID) + totalErrors);
                } else {
                    isValid = false;
                    errorSummary = new StringBuffer(
                            sValidPdfs +
                            getTextResourceService().getText(MESSAGE_MODULE_CC_INVALID) + totalErrors + 
                            " " + errorSummary.toString());
                }
                                
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + errorSummary.toString());
                
            } catch (Exception e) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Cc) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        e.getMessage());                                
                return false;
            }
        }
       
        return isValid;
    }

}