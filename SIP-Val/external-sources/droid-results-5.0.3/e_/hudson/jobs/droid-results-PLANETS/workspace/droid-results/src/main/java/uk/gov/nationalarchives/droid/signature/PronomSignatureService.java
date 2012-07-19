/**
 * <p>Copyright (c) The National Archives 2005-2010.  All rights reserved.
 * See Licence.txt for full licence details.
 * <p/>
 *
 * <p>DROID DCS Profile Tool
 * <p/>
 */
package uk.gov.nationalarchives.droid.signature;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;

import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.ClientCacheControlType;
import org.apache.cxf.transports.http.configuration.ConnectionType;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.transports.http.configuration.ProxyServerType;
import org.apache.xml.serialize.Method;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;

import uk.gov.nationalarchives.droid.profile.config.DroidGlobalProperty;
import uk.gov.nationalarchives.pronom.PronomService;
import uk.gov.nationalarchives.pronom.Version;

/**
 * @author rflitcroft
 * 
 */
public class PronomSignatureService implements SignatureService, ConfigurationListener {

    private final Log log = LogFactory.getLog(getClass());

    private PronomService pronomService;
    private String filenamePattern;
    private ProxySettings proxySettings = ProxySettings.newInstance();

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public SignatureFileInfo importSignatureFile(File targetDir) {
        Element sigFile = pronomService.getSignatureFileV1().getElement();

        // get the version number, which needs to be part of the filename...
        int version = Integer.valueOf(sigFile.getAttribute("Version"));
        boolean deprecated = Boolean
                .valueOf(sigFile.getAttribute("Deprecated"));

        SignatureFileInfo sigInfo = new SignatureFileInfo(version, deprecated);
        String fileName = String.format(filenamePattern, version);

        BufferedWriter writer = null;
        try {
            File outputFile = new File(targetDir, fileName);
            outputFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(outputFile));
            XMLSerializer serializer = new XMLSerializer(writer,
                    new OutputFormat(Method.XML, "UTF-8", true));
            serializer.serialize(sigFile);
            sigInfo.setFile(outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                log.warn("Error closing file writer", e);
            }
        }

        return sigInfo;
    }

    /**
     * @param pronomService
     *            the pronomService to set
     */
    public void setPronomService(PronomService pronomService) {
        this.pronomService = pronomService;
    }

    /**
     * @param filenamePattern
     *            the filename pattern to set
     */
    public void setFilenamePattern(String filenamePattern) {
        this.filenamePattern = filenamePattern;

    }

    /**
     * @return a SignatureFileInfo object representing the current version on
     *         the pronom website.
     */
    @Override
    public SignatureFileInfo getLatestVersion() {
        Holder<Version> version = new Holder<Version>();
        Holder<Boolean> deprecated = new Holder<Boolean>();

        pronomService.getSignatureFileVersionV1(version, deprecated);
        

        SignatureFileInfo info = new SignatureFileInfo(version.value
                .getVersion(), deprecated.value.booleanValue());
        return info;

    }

    /**
     * Sets the endpoint URL.
     * @param url the url to set
     */
    @Override
    public void setEndpointUrl(String url) {
        ((BindingProvider) pronomService).getRequestContext().put(
                BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                url); 
    }
    
    /**
     * Updates the proxy settings.
     * @param proxySettings the proxy settings to update
     */
    @Override
    public void setProxySettings(ProxySettings proxySettings) {
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnection(ConnectionType.CLOSE);
        httpClientPolicy.setAllowChunking(true);
        httpClientPolicy.setCacheControl(ClientCacheControlType.NO_CACHE);
        
        if (proxySettings.isEnabled()) {
            httpClientPolicy.setProxyServer(proxySettings.getProxyHost());
            httpClientPolicy.setProxyServerPort(proxySettings.getProxyPort());
            httpClientPolicy.setProxyServerType(proxySettings.getProxyType());
        } else {
            httpClientPolicy.setProxyServer(null);
            httpClientPolicy.unsetProxyServerPort();
            httpClientPolicy.setProxyServerType(null);
        }
        
        Client client = ClientProxy.getClient(pronomService);
        
        HTTPConduit http = (HTTPConduit) client.getConduit();
        http.setClient(httpClientPolicy);
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void configurationChanged(ConfigurationEvent evt) {
        final String propertyName = evt.getPropertyName();
        if (propertyName.startsWith("update.proxy")) {
            DroidGlobalProperty property = DroidGlobalProperty.forName(propertyName);
            switch (property) {
                case UPDATE_PROXY_HOST: 
                    proxySettings.setProxyHost((String) evt.getPropertyValue());
                    break;
                case UPDATE_PROXY_PORT:
                    proxySettings.setProxyPort((Integer) evt.getPropertyValue());
                    break;
                case UPDATE_USE_PROXY:
                    proxySettings.setEnabled((Boolean) evt.getPropertyValue());
                    break;
                case UPDATE_PROXY_TYPE:
                    proxySettings.setProxyType((String) evt.getPropertyValue());
                    break;
                default:
                    Exception e = new RuntimeException(String.format("Invalid proxy setting [%s]", propertyName));
                    log.error(e.getMessage(), e);
            }
            
            setProxySettings(proxySettings);
        } else if (propertyName.equals(DroidGlobalProperty.UPDATE_URL.getName())) {
            setEndpointUrl((String) evt.getPropertyValue());
        }
    }

    /**
     * Proxy server configuration.
     * @author rflitcroft
     *
     */
    public static final class ProxySettings {
        private String proxyHost;
        private int proxyPort;
        private ProxyServerType proxyType;
        private boolean enabled;
        /**
         * @return the proxyHost
         */
        public String getProxyHost() {
            return proxyHost;
        }
        /**
         * @return a nnew instance
         */
        public static ProxySettings newInstance() {
            return new ProxySettings();
        }
        /**
         * @param enabled the enabled to set
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
            
        }
        /**
         * @param proxyHost the proxyHost to set
         */
        public void setProxyHost(String proxyHost) {
            this.proxyHost = proxyHost;
        }
        /**
         * @return the proxyPort
         */
        public int getProxyPort() {
            return proxyPort;
        }
        /**
         * @param proxyPort the proxyPort to set
         */
        public void setProxyPort(int proxyPort) {
            this.proxyPort = proxyPort;
        }
        /**
         * @return the proxyType
         */
        public ProxyServerType getProxyType() {
            return proxyType;
        }
        /**
         * @param proxyType the proxyType to set
         */
        public void setProxyType(String proxyType) {
            try {
                this.proxyType = ProxyServerType.fromValue(proxyType);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(String.format(
                        "Invalid proxy type [%s]. Valid types are HTTP and SOCKS",
                        proxyType), e);
            }
        }
        
        /**
         * @return the enabled
         */
        public boolean isEnabled() {
            return enabled;
        }
    }
    
}
