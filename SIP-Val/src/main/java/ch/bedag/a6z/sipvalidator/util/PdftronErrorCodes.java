package ch.bedag.a6z.sipvalidator.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 * Mit dieser Klasse werden die Error Codes, welche von Pdftron generiert werden, übersetzt.
 * Bsp: 
 * <Error Code="e_PDFA173" Message="The value of Length does not match the number of bytes" ...
 * wobei die erste Ziffer nach e_PDFA den Code darstellt.
 * 
 * Als Code kann auch "e_PDF_Unknown" zurückgegeben werden, dies wird dann als Code 0 / General 
 * interpretiert.
 * 
 */

public class PdftronErrorCodes {
    final static Map<String, String> pdftronErrorCodes = new HashMap<String, String>();
    
    static{
        pdftronErrorCodes.put("0", "General");
        pdftronErrorCodes.put("1", "File structure");
        pdftronErrorCodes.put("2", "Graphics");
        pdftronErrorCodes.put("3", "Fonts");
        pdftronErrorCodes.put("4", "Transparency");
        pdftronErrorCodes.put("5", "Annotations");
        pdftronErrorCodes.put("6", "Actions");
        pdftronErrorCodes.put("7", "Metadata");
        pdftronErrorCodes.put("8", "Pdf-1 a");
        pdftronErrorCodes.put("9", "Interactive Forms");
    }
    
    /**
     * Gibt die Bezeichnung des Error Codes zurück.
     * @param errorCode Pdftron Error Code
     * @return Error Label
     */
    public static String getErrorLabel(String errorCode){
        return pdftronErrorCodes.get(errorCode);
    }
    
}
