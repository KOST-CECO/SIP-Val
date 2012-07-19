package ch.bedag.a6z.sipvalidator.service.impl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import ch.bedag.a6z.sipvalidator.service.TextResourceService;
/**
 * Dieser Service managt die Zugriffe auf die Resource Bundles.
 * 
 * @author razm Daniel Ludin, Bedag AG @version 2.0
 *
 */
public class TextResourceServiceImpl implements TextResourceService {
    // Per Default ist es dieser Name, kann jedoch auch mittels Dependency
    // Injection überschrieben werden.
    private String bundleBaseName = "messages";

    /**
     * Gibt den Wert des Attributs <code>bundleBaseName</code> zurück.
     * 
     * @return Wert des Attributs bundleBaseName.
     */
    public String getBundleBaseName() {
        return bundleBaseName;
    }

    /**
     * Setzt den Wert des Attributs <code>bundleBaseName</code>.
     * 
     * @param bundleBaseName
     *            Wert für das Attribut bundleBaseName.
     */
    public void setBundleBaseName(String bundleBaseName) {
        this.bundleBaseName = bundleBaseName;
    }

    /**
     * {@inheritDoc}
     */
    public String getText(String aKey, Object... values) {

        // For the time being, we use the VM Default Locale
        Locale locale = Locale.getDefault();
        /*
        Locale locale = null;
        UserContext userContext = UserContextHolder.getUserContext();
        if (userContext != null) {
            locale = userContext.getLocale();
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        */

        return this.getText(locale, aKey, values);
    }

    /**
     * {@inheritDoc}
     */
    public String getText(Locale locale, String aKey, Object... values) {

        String theValue = ResourceBundle.getBundle(this.bundleBaseName, locale).getString(aKey);
        return MessageFormat.format(theValue, values);
    }

}
