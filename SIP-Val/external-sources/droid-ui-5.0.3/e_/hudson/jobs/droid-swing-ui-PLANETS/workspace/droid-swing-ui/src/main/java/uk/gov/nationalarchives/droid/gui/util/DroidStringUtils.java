/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.gui.util;

/**
 * Some bespoke string utils.
 * @author rflitcroft
 *
 */
public final class DroidStringUtils {

    private static final String ELIPSES = "...";
    
    private DroidStringUtils() { }
    
    /**
     * Abbreviates a String by replacing a chunk in the middle by elipses (...).
     * @param str the string ot abbreviate.
     * @param minLeft preserves a minimum number of characters at the start
     * @param maxLength the length of the abbreviated String
     * This will be ignored if less than minLeft + minRight + 3
     * @return the abbreviated string
     */
    public static String abbreviate(String str, int minLeft, int maxLength) {
        if (str.length() <= maxLength) {
            return str;
        }
        
        int left = maxLength - ELIPSES.length();
        if (left > minLeft) {
            left = minLeft;
        }
        
        int length = str.length();
        StringBuilder sb = new StringBuilder();
        sb.append(str.substring(0, left));
        sb.append(ELIPSES);

        int removeCount = length - maxLength + sb.length();
        if (removeCount > 0) {
            sb.append(str.substring(removeCount, length));
        }
        
        return sb.toString();
        
    }
}
