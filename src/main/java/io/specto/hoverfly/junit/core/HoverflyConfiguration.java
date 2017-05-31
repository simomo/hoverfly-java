package io.specto.hoverfly.junit.core;

import java.util.Optional;

import static io.specto.hoverfly.junit.core.HoverflyConstants.HTTP;
import static io.specto.hoverfly.junit.core.HoverflyConstants.LOCALHOST;

/**
 * Configurations for Hoverfly instance
 */
public class HoverflyConfiguration {

    private String scheme = HTTP;
    private String host = LOCALHOST;
    private int proxyPort;
    private int adminPort;
    private boolean proxyLocalHost;
    private String destination;
    private String sslCertificatePath;
    private String sslKeyPath;
    private String authToken;
    private boolean isRemoteInstance;
    private String adminCertificate;
    private String proxyCaCertificate;


    /**
     * Create configurations for external hoverfly
     */
    HoverflyConfiguration(String scheme, String host, int proxyPort, int adminPort, boolean proxyLocalHost, String destination, String proxyCaCertificate, String authToken, String adminCertificate) {
        this(proxyPort, adminPort, proxyLocalHost, destination, proxyCaCertificate, null, null);
        setScheme(scheme);
        setHost(host);
        this.authToken = authToken;
        this.adminCertificate = adminCertificate;
        this.isRemoteInstance = true;
    }

    /**
     * Create configurations for internal-managed hoverfly
     */
    HoverflyConfiguration(int proxyPort, int adminPort, boolean proxyLocalHost, String destination, String proxyCaCertificate, String sslCertificatePath, String sslKeyPath) {
        this.proxyPort = proxyPort;
        this.adminPort = adminPort;
        this.proxyLocalHost = proxyLocalHost;
        this.destination = destination;
        this.proxyCaCertificate = proxyCaCertificate;
        this.sslCertificatePath = sslCertificatePath;
        this.sslKeyPath = sslKeyPath;
    }

    /**
     * Returns the host for the remote instance of hoverfly
     *
     * @return the remote host
     */
    public String getHost() {
        return host;
    }

    public String getScheme() {
        return scheme;
    }

    /**
     * Gets the proxy port {@link Hoverfly} is configured to run on
     * @return the proxy port
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Gets the admin port {@link Hoverfly} is configured to run on
     * @return the admin port
     */
    public int getAdminPort() {
        return adminPort;
    }

    public boolean isProxyLocalHost() {
        return proxyLocalHost;
    }

    /**
     * Gets the path to SSL certificate
     * @return the SSL certificate path
     */
    public String getSslCertificatePath() {
        return sslCertificatePath;
    }

    /**
     * Gets the path to SSL key
     * @return the SSL key path
     */
    public String getSslKeyPath() {
        return sslKeyPath;
    }

    public String getDestination() {
        return destination;
    }

    public Optional<String> getAuthToken() {
        return Optional.ofNullable(authToken);
    }

    public boolean isRemoteInstance() {
        return isRemoteInstance;
    }

    public Optional<String> getProxyCaCertificate() {
        return Optional.ofNullable(proxyCaCertificate);
    }

    public String getAdminCertificate() {
        return adminCertificate;
    }

    void setHost(String host) {
        if (host != null) {
            this.host = host;
        }
    }

    void setScheme(String scheme) {
        if (scheme != null) {
            this.scheme = scheme;
        }
    }

    void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    void setAdminPort(int adminPort) {
        this.adminPort = adminPort;
    }

}
