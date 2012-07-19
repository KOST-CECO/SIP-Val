package ch.bedag.a6z.sipvalidator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipException;

import ch.enterag.utils.zip.EntryOutputStream;
import ch.enterag.utils.zip.FileEntry;
import ch.enterag.utils.zip.Zip64File;

/**
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 * Diese Klasse benutzt die Zip64File Library zum Komprimieren und Archivieren von Dateien, welche grösser
 * als 4 G sein können. Es gibt momentan keine andere Software ausser pkzip, welche ein kommerzielles Produkt ist,
 * die dazu in der Lage wäre. Bspw. führt Izarc die Archivierung zwar durch, aber erzeugt fehlerhafte Metadaten,
 * die das Dekomprimieren dann verunmöglichen. Uebrigens werden von IZArc auch die Zeichenkodierungen nicht richtig 
 * abgehandelt.
 */

public class Zip64Archiver {

    static byte[] buffer = new byte[8192];

    // Process only directories under dir
    private static void visitAllDirs(File dir, Zip64File zip64File, File originalDir) 
        throws FileNotFoundException, ZipException, IOException {
        if (dir.isDirectory()) {
            String sDirToCreate = dir.getAbsolutePath();

            sDirToCreate = sDirToCreate.replace(originalDir.getAbsolutePath(), "");
            if (sDirToCreate.startsWith("/") || sDirToCreate.startsWith("\\")) {
                sDirToCreate = sDirToCreate.substring(1);
            }
            if (!sDirToCreate.endsWith("/") && sDirToCreate.length() > 0) {
                sDirToCreate = sDirToCreate + "/";
            }
            sDirToCreate = sDirToCreate.replaceAll("\\\\", "/");

            if (sDirToCreate.length() > 0) {

                buffer = new byte[0];
                Date dateModified = new Date(dir.lastModified());
                EntryOutputStream eos = zip64File.openEntryOutputStream(sDirToCreate, FileEntry.iMETHOD_STORED,
                        dateModified);
                eos.write(buffer, 0, buffer.length);
                eos.close();
                
            }

            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                visitAllDirs(new File(dir, children[i]), zip64File, originalDir);
            }
        }
    }

    // Process only files under dir
    private static void visitAllFiles(File dir, Zip64File zip64File, File originalDir) 
        throws FileNotFoundException, ZipException, IOException {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                visitAllFiles(new File(dir, children[i]), zip64File, originalDir);
            }
        } else {

            String sFileToCreate = dir.getAbsolutePath();

            sFileToCreate = sFileToCreate.replace(originalDir.getAbsolutePath(), "");
            if (sFileToCreate.startsWith("/") || sFileToCreate.startsWith("\\")) {
                sFileToCreate = sFileToCreate.substring(1);
            }
            sFileToCreate = sFileToCreate.replaceAll("\\\\", "/");

            if (sFileToCreate.length() > 0) {
                buffer = new byte[8192];
                Date dateModified = new Date(dir.lastModified());
                FileInputStream fis = new FileInputStream(dir);
                EntryOutputStream eos = zip64File.openEntryOutputStream(sFileToCreate, FileEntry.iMETHOD_DEFLATED,
                        dateModified);
                for (int iRead = fis.read(buffer); iRead >= 0; iRead = fis.read(buffer)) {
                    eos.write(buffer, 0, iRead);
                }
                fis.close();
                eos.close();
               
            }

        }
    }

    public static void archivate(File inputDir, File outpFile) throws FileNotFoundException, ZipException, IOException {
        Zip64File zip64File = new Zip64File(outpFile);
        // create all necessary folders first
        Zip64Archiver.visitAllDirs(inputDir, zip64File, inputDir);
        // then create the file entries
        Zip64Archiver.visitAllFiles(inputDir, zip64File, inputDir);

        zip64File.close();            
    }

    public static void main(String[] args) {

        // C:\ludin\A6Z-SIP-Validator\SIP-Beispiele etc\SIP_20101018_RIS_4
        String sInputDir = "C:\\ludin\\A6Z-SIP-Validator\\SIP-Beispiele etc\\SIP_20101018_RIS_4";
        //String sInputDir = "C:\\ludin\\A6Z-SIP-Validator\\SIP-Beispiele 20110112\\SIP_20110112_KOST_1a-2d.3c.3d(sehrGROSS)";
        //String sInputDir = "C:\\ludin\\tmp10\\ArchivierungFehlersuche";
        File inputDir = new File(sInputDir);

        //String sOutpuFile = new String("C:\\ludin\\tmp10\\ArchivierungFehlersuche.zip");
        //String sOutpuFile = new String("C:\\ludin\\tmp10\\SIP_20110112_KOST_1a-2d.3c.3d(sehrGROSS).zip");
        String sOutpuFile = new String("C:\\ludin\\tmp2\\SIP_20101018_RIS_4.zip");
        File outputFile = new File(sOutpuFile);

        try {
            Zip64Archiver.archivate(inputDir, outputFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
