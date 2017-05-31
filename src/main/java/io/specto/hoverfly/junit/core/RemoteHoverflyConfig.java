package io.specto.hoverfly.junit.core;

/**
 * Config builder interface for settings specific to external/remote {@link Hoverfly} instance
 */
public interface RemoteHoverflyConfig extends HoverflyConfig {

    /**
     * Sets hostname for the external Hoverfly, default to localhost
     * @param host the hostname
     * @return the {@link RemoteHoverflyConfig} for further customizations
     */
    RemoteHoverflyConfig host(String host);

    /**
     * Sets up custom authentication header for secured remote Hoverfly instance.
     * Gets auth token from an environment variable "HOVERFLY_AUTH_TOKEN"
     *
     * @return the {@link RemoteHoverflyConfig} for further customizations
     */
    RemoteHoverflyConfig withAuthHeader();

    /**
     * Sets up custom authentication header for secured remote Hoverfly instance
     * @param authToken a token for Hoverfly authentication
     * @return the {@link RemoteHoverflyConfig} for further customizations
     */
    RemoteHoverflyConfig withAuthHeader(String authToken);

    /**
     * Sets up to use admin endpoint over HTTPS
     * @return the {@link RemoteHoverflyConfig} for further customizations
     */
    RemoteHoverflyConfig withHttpsAdminEndpoint();

    // TODO add support for custom server certificate for admin endpoint
}
