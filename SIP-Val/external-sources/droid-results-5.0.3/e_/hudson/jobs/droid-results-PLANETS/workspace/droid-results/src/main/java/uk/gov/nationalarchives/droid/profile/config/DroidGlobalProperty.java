/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.profile.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;

/**
 * All DROID global properties.
 * @author rflitcroft
 *
 */
public enum DroidGlobalProperty {
    

    /** Default throttle. */
    DEFAULT_THROTTLE("profile.defaultThrottle", PropertyType.INTEGER, true),
    
    /** THe default version for signature files. */
    DEFAULT_SIG_FILE_VERSION("profile.defaultSigFileVersion", PropertyType.TEXT, true),
    
    /** Update URL. */
    UPDATE_URL("update.url", PropertyType.TEXT, true),

    /** Update throttle. */
    UPDATE_AUTO_CHECK("update.autoCheck", PropertyType.BOOLEAN, true),
    
    /** Update throttle. */
    UPDATE_FREQUENCY_DAYS("update.frequency.days", PropertyType.INTEGER, true),
    
    /** Update throttle. */
    UPDATE_ON_STARTUP("update.frequency.startup", PropertyType.BOOLEAN, true),
    
    /** Update proxy server used. */
    UPDATE_USE_PROXY("update.proxy", PropertyType.BOOLEAN, true),
    
    /** Update proxy host. */
    UPDATE_PROXY_HOST("update.proxy.host", PropertyType.TEXT, true),

    /** Update proxy port. */
    UPDATE_PROXY_PORT("update.proxy.port", PropertyType.INTEGER, true),
    
    /** Update proxy type. */
    UPDATE_PROXY_TYPE("update.proxy.type", PropertyType.TEXT, true),
    
    /** Autoset the default signature to latest downloaded. */
    UPDATE_AUTOSET_DEFAULT("update.autoSetDefault", PropertyType.BOOLEAN, true),
    
    /** Autoset the default signature to latest downloaded. */
    UPDATE_DOWNLOAD_PROMPT("update.downloadPrompt", PropertyType.BOOLEAN, true),

    /** Autoset the default signature to latest downloaded. */
    LAST_UPDATE_CHECK("update.lastCheck", PropertyType.LONG, false), 
    
    /** Development mode. */
    DEV_MODE("development_mode", PropertyType.BOOLEAN, false),
    
    /** Whether to process files in archives. */
    PROCESS_ARCHIVES("profile.processArchives", PropertyType.BOOLEAN, true),

    
    /** Whether to process files in archives. */
    PUID_URL_PATTERN("puid.urlPattern", PropertyType.TEXT, true);

    
    private static Map<String, DroidGlobalProperty> allValues = new HashMap<String, DroidGlobalProperty>(); 
    
    static {
        for (DroidGlobalProperty prop : DroidGlobalProperty.values()) {
            allValues.put(prop.getName(), prop);
        }
    }
    
    private String name;
    private PropertyType type;
    private boolean userConfigurable;
    
    private DroidGlobalProperty(String name, PropertyType type, boolean userConfigurable) {
        this.name = name;
        this.type = type;
        this.userConfigurable = userConfigurable;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return the numeric
     */
    public PropertyType getType() {
        return type;
    }
    
    /**
     * @return the userConfigurable
     */
    boolean isUserConfigurable() {
        return userConfigurable;
    }
    
    /**
     * @author rflitcroft
     *
     */
    public static enum PropertyType {
        /** Text. */
        TEXT { 
            @Override
            public Object getTypeSafeValue(Configuration config, String key) {
                return config.getString(key);
            }
        },
        
        /** Numeric. */
        INTEGER {
            @Override
            public Object getTypeSafeValue(Configuration config, String key) {
                return config.getInt(key);
            }
        },
        
        /** Boolean. */
        BOOLEAN {
            @Override
            public Object getTypeSafeValue(Configuration config, String key) {
                return config.getBoolean(key);
            }
        }, 
        
        /** Long Integer. */
        LONG {
            @Override
            public Object getTypeSafeValue(Configuration config, String key) {
                return config.getBigInteger(key);
            }
        };

        /**
         * Converts a String property to a type-safe value.
         * @param config the configuration
         * @param key the key
         * @return a type-safe object
         */
        public abstract Object getTypeSafeValue(Configuration config, String key);
        
    }

    /**
     * @param key the name
     * @return a DroidGlobalProperty
     */
    public static DroidGlobalProperty forName(String key) {
        DroidGlobalProperty property = allValues.get(key);
        return property != null && property.isUserConfigurable() ? property : null;
    }


}
