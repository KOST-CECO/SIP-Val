/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.command.i18n;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * @author rflitcroft
 *
 */
public final class I18N {
    
    /** Options header. */
    public static final String OPTIONS_HEADER = "options.header";
    /** Help help. */
    public static final String HELP_HELP = "help.help";
    /** Version help. */
    public static final String VERSION_HELP = "version.help";
    /** Bad options. */
    public static final String BAD_OPTIONS = "invalid.options";
    
    /** Export description. */
    public static final String EXPORT_HELP = "export.help";
    
    /** Profiles decription. */
    public static final String PROFILES_HELP = "profiles.help";
    /** Report decription. */
    public static final String REPORT_HELP = "report.help";

    /** Report name. */
    public static final String REPORT_NAME_HELP = "report.name.help";
    /** Narrowing filter. */
    public static final String ALL_FILTER = "all.filter.help";
    /** Any filter (widenting). */
    public static final String ANY_FILTER = "any.filter.help";
    
    /** List filter fields. */
    public static final String LIST_FILTER_FIELD = "filter.field.help";
    
    /** Run a profile. */
    public static final String RUN_PROFILE_HELP = "profile.run.help";
    
    /** Recurse subdirectories. */
    public static final String RECURSE_HELP = "recurse.help";
    
    /** Only log at ERROR and above to the console. */
    public static final String QUIET_HELP = "quiet.help";
    
    /** Check for signature update. */
    public static final String CHECK_SIGNATURE_UPDATE_HELP = "signature_update.check.help";
    
    /** Check signature updates - none available. */
    public static final String CHECK_SIGNATURE_UPDATE_UNAVAILABLE = "signature_update.check.unavailable";

    /** Check signature updates - success. */
    public static final String CHECK_SIGNATURE_UPDATE_SUCCESS = "signature_update.check.success";

    /** Check signature updates - error. */
    public static final String CHECK_SIGNATURE_UPDATE_ERROR = "signature_update.check.error";
    
    /** Download the latest signature update. */
    public static final String DOWNLOAD_SIGNATURE_UPDATE_HELP = "signature_update.download.help";

    /** Download the latest signature update. */
    public static final String DOWNLOAD_SIGNATURE_UPDATE_SUCCESS = "signature_update.download.success";

    /** Download the latest signature update. */
    public static final String DOWNLOAD_SIGNATURE_UPDATE_ERROR = "signature_update.download.error";
    
    /** Display the default signature file version help. */
    public static final String DEFAULT_SIGNATURE_VERSION_HELP = "signature.display_default.help";

    /** Display the default signature file version. */
    public static final String DEFAULT_SIGNATURE_VERSION = "signature.display";
    
    /** Configure default signature file version help. */
    public static final String CONFIGURE_DEFAULT_SIGNATURE_VERSION_HELP = "signature.configure_default.help";
    
    /** "Version". */
    public static final String VERSION = "version";
    
    /** Configure default siognature file success message. */
    public static final String CONFIGURE_SIGNATURE_FILE_VERSION_SUCCESS = "signature.configure_default.success";
    
    /** Configure default signature file invalid message. */
    public static final String CONFIGURE_SIGNATURE_FILE_VERSION_INVALID = "signature.configure_default.invalid";
    
    /** List all signature file versions help. */
    public static final String LIST_SIGNATURE_VERSIONS_HELP = "signature.list_all.help";
    
    /** No Signature files available. */
    public static final String NO_SIG_FILES_AVAILABLE = "signature.none";

    private I18N() { }
    
    /**
     * Resolves a key to an internatyionalised String.
     * @param key the key
     * @return the internationalised String
     */
    public static String getResource(final String key) {
        return ResourceBundle.getBundle("options").getString(key);
    }
    
    /**
     * Resolves a key to an internationalised String with replacement parameters.
     * @param key the key
     * @param params the parameters
     * @return the internationalised String
     */
    public static String getResource(final String key, Object... params) {
        String pattern = getResource(key);
        return MessageFormat.format(pattern, params);
    }

}
