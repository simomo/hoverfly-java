package io.specto.hoverfly.junit.core;

/**
 * Config builder interface for settings specific to {@link Hoverfly} managed internally
 */
public interface LocalHoverflyConfig extends HoverflyConfig {

    /**
     * Sets the SSL certificate file for overriding default Hoverfly self-signed certificate
     * The file can be in any PEM encoded certificate, in .crt or .pem extensions
     * @param sslCertificatePath certificate file in classpath
     * @return the {@link LocalHoverflyConfig} for further customizations
     */
    LocalHoverflyConfig sslCertificatePath(String sslCertificatePath);

    /**
     * Sets the SSL key file for overriding default Hoverfly SSL key
     * The file can be in any PEM encoded key, in .key or .pem extensions
     * @param sslKeyPath key file in classpath
     * @return the {@link LocalHoverflyConfig} for further customizations
     */
    LocalHoverflyConfig sslKeyPath(String sslKeyPath);
}
