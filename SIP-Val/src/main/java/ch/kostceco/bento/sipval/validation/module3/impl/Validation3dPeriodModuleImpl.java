/*== SIP-Val ==================================================================================
The SIP-Val v0.9.0 application is used for validate Submission Information Package (SIP).
Copyright (C) 2011 Claire Röthlisberger (KOST-CECO), Daniel Ludin (BEDAG AG)
-----------------------------------------------------------------------------------------------
SIP-Val is a development of the KOST-CECO. All rights rest with the KOST-CECO. 
This application is free software: you can redistribute it and/or modify it under the 
terms of the GNU General Public License as published by the Free Software Foundation, 
either version 3 of the License, or (at your option) any later version. 
BEDAG AG and Daniel Ludin hereby disclaims all copyright interest in the program 
SIP-Val v0.2.0 written by Daniel Ludin (BEDAG AG). Switzerland, 1 March 2011.
This application is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
See the follow GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program; 
if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, 
Boston, MA 02110-1301 USA or see <http://www.gnu.org/licenses/>.
==============================================================================================*/

package ch.kostceco.bento.sipval.validation.module3.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import ch.kostceco.bento.sipval.exception.module3.Validation3dPeriodException;
import ch.kostceco.bento.sipval.validation.ValidationModuleImpl;
import ch.kostceco.bento.sipval.validation.module3.Validation3dPeriodModule;
import ch.enterag.utils.zip.EntryInputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

public class Validation3dPeriodModuleImpl extends ValidationModuleImpl implements Validation3dPeriodModule {

    DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat formatter2 = new SimpleDateFormat("dd.MM.yyyy");
    DateFormat formatter3 = new SimpleDateFormat("yyyy");

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(File sipDatei) throws Validation3dPeriodException {
        
        String toplevelDir = sipDatei.getName();
        int lastDotIdx = toplevelDir.lastIndexOf(".");
        toplevelDir = toplevelDir.substring(0, lastDotIdx);

        boolean valid = true;
        FileEntry metadataxml = null;        
        
        try {
            Zip64File zipfile = new Zip64File(sipDatei);
            List<FileEntry> fileEntryList = zipfile.getListFileEntries();
            for (FileEntry fileEntry : fileEntryList) {
                
                if (fileEntry.getName().equals("header/" + METADATA) || 
                        fileEntry.getName().equals(toplevelDir + "/header/" + METADATA)) {
                    metadataxml = fileEntry;
                }
            }
            
            // keine metadata.xml in der SIP-Datei gefunden
            if (metadataxml == null) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Cd) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        getTextResourceService().getText(ERROR_MODULE_AE_NOMETADATAFOUND));                                
                return false;
            }
            
            EntryInputStream eis = zipfile.openEntryInputStream(metadataxml.getName());
            BufferedInputStream is = new BufferedInputStream(eis);

            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(is);
                doc.normalize();
                
                XPath xpath = XPathFactory.newInstance().newXPath();
                Element elementAblDatumVon = (Element)xpath.evaluate(
                        "/paket/ablieferung/entstehungszeitraum/von/datum", doc, XPathConstants.NODE);

                Element elementAblDatumBis = (Element)xpath.evaluate(
                        "/paket/ablieferung/entstehungszeitraum/bis/datum", doc, XPathConstants.NODE);

                Element elementAblCaVon = (Element)xpath.evaluate(
                        "/paket/ablieferung/entstehungszeitraum/von/ca", doc, XPathConstants.NODE);
                
                Element elementAblCaBis = (Element)xpath.evaluate(
                        "/paket/ablieferung/entstehungszeitraum/bis/ca", doc, XPathConstants.NODE);
                
                // Es wurde kein Ablieferungs-Entstehungszeitraum angegeben,
                // die Validierung wird nun von Dossier abwärts an fortgesetzt,
                // sofern die Dokumente einen Entstehungszeitraum aufweisen
                
                boolean dateAblieferungUseable = true;
                // Existiert das "Datum Von"?
                Calendar calAblieferungVon = Calendar.getInstance();
                if (elementAblDatumVon != null) {
                    Date date = parseDatumVon(elementAblDatumVon.getTextContent());
                    if (date == null) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(ERROR_MODULE_CD_UNPARSEABLE_DATE));
                        return false;
                    }
                    calAblieferungVon.setTime(date);
                } else {
                    valid = false;
                    dateAblieferungUseable = false;
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_CD_DATUM_VON_NOT_EXISTING));
                }
                
                
                // Existiert das "Datum Bis"?
                Calendar calAblieferungBis = Calendar.getInstance();
                if (elementAblDatumBis != null) {
                    Date date = parseDatumBis(elementAblDatumBis.getTextContent()); 
                    if (date == null) {
                        getMessageService().logError(
                                getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                getTextResourceService().getText(MESSAGE_DASHES) + 
                                getTextResourceService().getText(ERROR_MODULE_CD_UNPARSEABLE_DATE));
                        return false;
                    }
                    calAblieferungBis.setTime(date);
                } else {
                    valid = false;
                    dateAblieferungUseable = false;
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_CD_DATUM_BIS_NOT_EXISTING));
                } 
                
                // falls das Ablieferungs-Datum "Bis" vor dem Datum "Von" liegt, gehen wir davon aus,
                // das eine Verwechslung vorliegt, und tauschen die Daten gegeneinander aus.
                if (calAblieferungBis.before(calAblieferungVon)) {
                    Calendar calTmp = calAblieferungBis;
                    calAblieferungBis = calAblieferungVon;
                    calAblieferungVon = calTmp;
                    
                    String[] params = new String[4];
                    params[0] = (elementAblCaVon != null && elementAblCaVon.getTextContent().equals("true")) ? "ca. " : "" ;
                    params[1] = formatter2.format(calAblieferungBis.getTime());
                    params[2] = (elementAblCaBis != null && elementAblCaBis.getTextContent().equals("true")) ? "ca. " : "" ;
                    params[3] = formatter2.format(calAblieferungVon.getTime());

                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_CD_INVALID_ABLIEFERUNG_RANGE, 
                                    (Object[])params));

                    valid = false;
                }
                
                String datumVon = formatter2.format(calAblieferungVon.getTime());
                String datumBis = formatter2.format(calAblieferungBis.getTime());
                
                // Liegt eines der Daten in der Zukunft?
                Calendar calNow = Calendar.getInstance();
                if (calAblieferungVon.after(calNow)) {                        
                    valid = false;
                    dateAblieferungUseable = false;
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_CD_DATUM_VON_IN_FUTURE, 
                                    datumVon));
                }
                if (calAblieferungBis.after(calNow)) {                        
                    valid = false;
                    dateAblieferungUseable = false;
                    getMessageService().logError(
                            getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                            getTextResourceService().getText(MESSAGE_DASHES) + 
                            getTextResourceService().getText(ERROR_MODULE_CD_DATUM_BIS_IN_FUTURE, 
                                    datumBis));
                }
                
                // über alle Dossiers iterieren
                boolean noDateValidation = false;
                NodeList nodeLstDossier = doc.getElementsByTagName("dossier");
                for (int s = 0; s < nodeLstDossier.getLength(); s++) {
                    Node dossierNode = nodeLstDossier.item(s);
                    
                    // den Entstehungszeitraum des Dossiers extrahieren
                    NodeIterator nl = XPathAPI.selectNodeIterator(dossierNode, "entstehungszeitraum/von/datum");
                    Node nameNode = nl.nextNode();
                    String dateDossierVon = nameNode.getTextContent();
                    NodeIterator nlCa = XPathAPI.selectNodeIterator(dossierNode, "entstehungszeitraum/von/ca");                                      
                    Node circaDossierVonNode = nlCa.nextNode();
                   
                    nl = XPathAPI.selectNodeIterator(dossierNode, "entstehungszeitraum/bis/datum");
                    nameNode = nl.nextNode();
                    String dateDossierBis = nameNode.getTextContent();
                    nlCa = XPathAPI.selectNodeIterator(dossierNode, "entstehungszeitraum/bis/ca");                                      
                    Node circaDossierBisNode = nlCa.nextNode();
                    
                    // Wenn beide Dossierdaten vorhanden und valid sind (= innerhalb des Ablieferungsentstehungszeitraums)
                    // wird diese zur weiteren Validierung mit ihren untergeordneten Dokumenten verwendet.
                    Calendar calDossierVon = Calendar.getInstance();
                    Calendar calDossierBis = Calendar.getInstance();
                    boolean dossierRangeOk = true;
                    
                    if (dateDossierVon != null && dateDossierBis != null) {
                        
                        Date date = parseDatumVon(dateDossierVon); 
                        if (date == null) {
                            getMessageService().logError(
                                    getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                    getTextResourceService().getText(MESSAGE_DASHES) + 
                                    getTextResourceService().getText(ERROR_MODULE_CD_UNPARSEABLE_DATE));
                            return false;
                        }

                        calDossierVon.setTime(date);
                        
                        date = parseDatumBis(dateDossierBis); 
                        if (date == null) {
                            getMessageService().logError(
                                    getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                    getTextResourceService().getText(MESSAGE_DASHES) + 
                                    getTextResourceService().getText(ERROR_MODULE_CD_UNPARSEABLE_DATE));
                            return false;
                        }

                        calDossierBis.setTime(date);
                        
                        // wurden die Dossier-Daten eventuell vertauscht?
                        if (calDossierVon.after(calDossierBis)) {
                            Calendar calTmp = calDossierVon;
                            calDossierVon = calDossierBis;
                            calDossierBis = calTmp;
                            
                            Element dossierElement = (Element)dossierNode;
                            String dossierId = dossierElement.getAttribute("id");

                            String[] params = new String[5];
                            params[0] = dossierId;
                            params[1] = (circaDossierVonNode != null && circaDossierVonNode.equals("true")) ? "ca. " : "" ;
                            params[2] = formatter2.format(calDossierBis.getTime());
                            params[3] = (circaDossierBisNode != null && circaDossierBisNode.equals("true")) ? "ca. " : "" ;
                            params[4] = formatter2.format(calDossierVon.getTime());

                            getMessageService().logError(
                                    getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                    getTextResourceService().getText(MESSAGE_DASHES) + 
                                    getTextResourceService().getText(ERROR_MODULE_CD_INVALID_DOSSIER_RANGE_CA, 
                                            (Object[])params));

                            valid = false;
                            
                        }
                        
                        // nur wenn ein gültiger Ablieferungszeitraum vorhanden ist, wird ein Dossierzeitraum darauf
                        // geprüft, dass er in diesen hineinpasst.
                        if (dateAblieferungUseable) {                            
                            if ((calDossierVon.before(calAblieferungVon) || calDossierBis.after(calAblieferungBis)) || 
                                    calDossierVon.after(calDossierBis)) {
                                // der Dossierzeitraum liegt nicht innerhalb des Ablieferungszeitraums -> Fehler
                                Element dossierElement = (Element)dossierNode;
                                String dossierId = dossierElement.getAttribute("id");
                                String datumDossierVon = formatter2.format(calDossierVon.getTime());
                                String datumDossierBis = formatter2.format(calDossierBis.getTime());
                                
                                String[] params = new String[9];
                                params[0] = dossierId;
                                params[1] = (circaDossierVonNode != null && circaDossierVonNode.equals("true")) ? "ca. " : "" ;
                                params[2] = datumDossierVon;
                                params[3] = (circaDossierBisNode != null && circaDossierBisNode.equals("true")) ? "ca. " : "" ;
                                params[4] = datumDossierBis;
                                params[5] = (elementAblCaVon != null && elementAblCaVon.getTextContent().equals("true")) ? "ca. " : "" ;
                                params[6] = formatter2.format(calAblieferungBis.getTime());
                                params[7] = (elementAblCaBis != null && elementAblCaBis.getTextContent().equals("true")) ? "ca. " : "" ;
                                params[8] = formatter2.format(calAblieferungVon.getTime());

                                getMessageService().logError(
                                        getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                        getTextResourceService().getText(MESSAGE_DASHES) + 
                                        getTextResourceService().getText(ERROR_MODULE_CD_INVALID_DOSSIER_RANGE_CA_ABL, 
                                        (Object[])params));
    
                                dossierRangeOk = false;
                                valid = false;
                            }
                        }
                        
                        
                    } 

                    if (!dossierRangeOk) {
                        // kein gültiger Dossierzeitraum vorhanden
                        if (dateAblieferungUseable) {
                            // wir haben keinen gültigen Dossierzeitraum, jedoch Ablieferungszeitraum und verwenden
                            // diesen als Validierungszeitraum für untergeordnete Dokumente
                            calDossierVon = calAblieferungVon;
                            calDossierBis = calAblieferungBis;
                        } else {
                            // wir haben weder einen gültigen Dossier- noch Ablieferungs-Zeitraum,
                            // allfällige untergeordnete Dokumente werden also nicht validiert.
                            noDateValidation = true;
                        }
                    }
                    
                    if (!noDateValidation) {
                        
                        NodeIterator nlEntstehungszeitraumDok = XPathAPI.selectNodeIterator(dossierNode, "dokument/entstehungszeitraum");
                        Node dokEntstehungszeitraumNode = null;
                        while ((dokEntstehungszeitraumNode = nlEntstehungszeitraumDok.nextNode()) != null){

                            // id des Dossier-Nodes ermitteln
                            Element dossierElement = (Element)dossierNode;
                            String dossierId = dossierElement.getAttribute("id");

                            // id des Dokument-Nodes ermitteln
                            Node dokNode = dokEntstehungszeitraumNode.getParentNode();
                            Element dokElement = (Element)dokNode;
                            String dokumentId = dokElement.getAttribute("id");                            
                           
                            NodeIterator nlVon = XPathAPI.selectNodeIterator(dokEntstehungszeitraumNode, "von/datum");
                            Node nodeVon = nlVon.nextNode();
                            
                            NodeIterator nlBis = XPathAPI.selectNodeIterator(dokEntstehungszeitraumNode, "bis/datum");
                            Node nodeBis = nlBis.nextNode();
                            
                            // hat das "von" Element ein "ca" Element?
                            NodeIterator nlCaVon = XPathAPI.selectNodeIterator(dokEntstehungszeitraumNode, "von/ca");
                            Node nodeCaVon = nlCaVon.nextNode();
                            
                            // hat das "bis" Element ein "ca" Element?
                            NodeIterator nlCaBis = XPathAPI.selectNodeIterator(dokEntstehungszeitraumNode, "bis/ca");
                            Node nodeCaBis = nlCaBis.nextNode();
                            
                            Date dateDokVon = null;
                            if (nodeVon != null && nodeVon.getTextContent() != null) {
                                dateDokVon = parseDatumVon(nodeVon.getTextContent());
                                if (dateDokVon == null) {
                                    getMessageService().logError(
                                            getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                            getTextResourceService().getText(MESSAGE_DASHES) + 
                                            getTextResourceService().getText(ERROR_MODULE_CD_UNPARSEABLE_DATE));
                                    return false;
                                }

                            }
                            Date dateDokBis = null;
                            if (nodeBis != null && nodeBis.getTextContent() != null) {
                                dateDokBis = parseDatumBis(nodeBis.getTextContent());
                                if (dateDokBis == null) {
                                    getMessageService().logError(
                                            getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                            getTextResourceService().getText(MESSAGE_DASHES) + 
                                            getTextResourceService().getText(ERROR_MODULE_CD_UNPARSEABLE_DATE));
                                    return false;
                                }
                            }
                                                                                                               
                            Calendar calDokVon = Calendar.getInstance();
                            calDokVon.setTime(dateDokVon);
                            Calendar calDokBis = Calendar.getInstance();
                            calDokBis.setTime(dateDokBis);
                            
                            // wurden die Daten von/bis eventuell vertauscht?
                            if (calDokVon.after(calDokBis)) {
                                Calendar calTmp = calDokVon;
                                calDokVon = calDokBis;
                                calDokBis = calTmp;
                            }
                            
                            // liegt das Datum "Entstehungszeitraum von" in der Zukunft?
                            if (calDokVon.after(calNow)) {
                                valid = false;
                                
                                String[] params = new String[3];
                                params[0] = dokumentId;
                                params[1] = nodeCaVon == null ? "" : "ca. " ;
                                params[2] = formatter2.format(calDokVon.getTime());

                                getMessageService().logError(
                                        getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                        getTextResourceService().getText(MESSAGE_DASHES) + 
                                        getTextResourceService().getText(ERROR_MODULE_CD_DATUM_ENTSTEHUNG_VON_IN_FUTURE, 
                                                (Object[])params));
                                valid = false;
                            }
                            
                            // liegt das Datum "Entstehungszeitraum bis" in der Zukunft?
                            if (calDokBis.after(calNow)) {
                                
                                String[] params = new String[3];
                                params[0] = dokumentId;
                                params[1] = nodeCaBis == null ? "" : "ca. " ;
                                params[2] = formatter2.format(calDokBis.getTime());

                                getMessageService().logError(
                                        getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                        getTextResourceService().getText(MESSAGE_DASHES) + 
                                        getTextResourceService().getText(ERROR_MODULE_CD_DATUM_ENTSTEHUNG_BIS_IN_FUTURE, 
                                                (Object[])params));
                                valid = false;
                                
                            }
                            
                            // liegt der Entstehungszeitraum des Dokuments ausserhalb des Entstehungszeitraums
                            // der Ablieferung/des Dossiers?
                            if (calDokVon.before(calDossierVon) || calDokBis.after(calDossierBis)) {
                                
                                String datumDokumentVon = formatter2.format(calDokVon.getTime());
                                String datumDokumentBis = formatter2.format(calDokBis.getTime());
                                String datumDossierVon = formatter2.format(calDossierVon.getTime());
                                String datumDossierBis = formatter2.format(calDossierBis.getTime());
                                
                                String[] params = new String[10];
                                params[0] = dokumentId;
                                params[1] = (nodeCaVon != null && nodeCaVon.equals("true")) ? "ca. " : "" ;
                                params[2] = datumDokumentVon;
                                params[3] = (nodeCaBis != null && nodeCaBis.equals("true")) ? "ca. " : "" ;
                                params[4] = datumDokumentBis;
                                params[5] = dossierId;
                                params[6] = (circaDossierVonNode != null && circaDossierVonNode.equals("true")) ? "ca. " : "" ;
                                params[7] = datumDossierVon;
                                params[8] = (circaDossierBisNode != null && circaDossierBisNode.equals("true")) ? "ca. " : "" ;
                                params[9] = datumDossierBis;
                                
                                getMessageService().logError(
                                        getTextResourceService().getText(MESSAGE_MODULE_Cd) +
                                        getTextResourceService().getText(MESSAGE_DASHES) + 
                                        getTextResourceService().getText(ERROR_MODULE_CD_INVALID_DOKUMENT_RANGE_CA, 
                                                (Object[])params));
                                
                                valid = false;
                            }
                        }
                    }
                }
                
                zipfile.close();
                is.close();
                
            } catch (Exception e) {
                getMessageService().logError(
                        getTextResourceService().getText(MESSAGE_MODULE_Cd) + 
                        getTextResourceService().getText(MESSAGE_DASHES) + 
                        e.getMessage());                                
                return false;
            }
        } catch (Exception e) {
            getMessageService().logError(
                    getTextResourceService().getText(MESSAGE_MODULE_Cd) + 
                    getTextResourceService().getText(MESSAGE_DASHES) + 
                    e.getMessage());                                
            return false;
        }
        
        return valid;
    }

    /**
     * Diese Methode generiert aus einem String ein Datum, der String
     * kann folgende Werte haben:
     * 
     * 2007-09-30       => 30.09.2007
     * 2007             => 31.12.2007
     * "keine Angabe"   => aktuelles Datum
     * 
     * @param sDate der Datumsstring aus dem Entstehungszeitraum in metadata.xml
     * @return das umgewandelte Datum
     */
    private Date parseDatumBis(String sDate){
        Date date = null;
        
        // "keine Angabe"
        if (sDate.equals("keine Angabe")) {
            date = new Date();
            return date;
        }
        
        // "2007"
        if (sDate.length() == 4) {
            int year = Integer.parseInt(sDate);
            Calendar endOfYear = new GregorianCalendar(year, Calendar.DECEMBER, 31);
            return endOfYear.getTime();
        }
        
        // "2007-09-30"
        try {
            date = (Date)formatter1.parse(sDate);
            return date;
        } catch (ParseException e) {
            return null;
        } 
    }
    
    /**
     * Diese Methode generiert aus einem String ein Datum, der String
     * kann folgende Werte haben:
     * 
     * 2007-09-30       => 30.09.2007
     * 2007             => 01.01.2007
     * "keine Angabe"   => 01.01.0000
     * 
     * @param sDate der Datumsstring aus dem Entstehungszeitraum in metadata.xml
     * @return das umgewandelte Datum
     */
    private Date parseDatumVon(String sDate){
        Date date = null;
        
        // "keine Angabe"
        if (sDate.equals("keine Angabe")) {
            Calendar earliestPossibleDate = new GregorianCalendar(0, Calendar.JANUARY, 1);
            return earliestPossibleDate.getTime();
        }
        
        // "2007"
        if (sDate.length() == 4) {
            int year = Integer.parseInt(sDate);
            Calendar endOfYear = new GregorianCalendar(year, Calendar.JANUARY, 1);
            return endOfYear.getTime();
        }
        
        // "2007-09-30"
        try {
            date = (Date)formatter1.parse(sDate);
            return date;
        } catch (ParseException e) {
            return null;
        } 
    }
    
    public static void main(String[] args) {
        Validation3dPeriodModuleImpl v3d = new Validation3dPeriodModuleImpl();
        
        //String sDate = "2007-06-18";
        //String sDate = "2007";
        String sDate = "keine Angabe";
        v3d.parseDatumVon(sDate);
    }
}
