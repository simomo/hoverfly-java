/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this classpath except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2016-2016 SpectoLabs Ltd.
 */
package io.specto.hoverfly.junit.core;


/**
 * Config builder interface for common settings of {@link Hoverfly}
 */
public interface HoverflyConfig {

    /**
     * New instance
     * @return a {@link LocalHoverflyConfig} implementation
     */
    static LocalHoverflyConfig configs() {
        return new HoverflyConfigBuilder();
    }

    /**
     * Sets the admin port for {@link Hoverfly}
     * @param adminPort the admin port
     * @return the {@link HoverflyConfig} for further customizations
     */
    HoverflyConfig adminPort(int adminPort);

    /**
     * Sets the proxy port for {@link Hoverfly}
     *
     * @param proxyPort the proxy port
     * @return the {@link HoverflyConfig} for further customizations
     */
    HoverflyConfig proxyPort(int proxyPort);

    /**
     * Sets destination filter to what target urls to simulate or capture
     * @param destination the destination filter
     * @return the {@link HoverflyConfig} for further customizations
     */
    HoverflyConfig destination(String destination);

    /**
     * Controls whether we want to proxy localhost.  If false then any request to localhost will not be proxied through {@link Hoverfly}.
     * @return the {@link HoverflyConfig} for further customizations
     */
    @Deprecated
    default HoverflyConfig proxyLocalHost(boolean proxyLocalHost) {
        if (proxyLocalHost) {
            return proxyLocalHost();
        }
        return this;
    }

    /**
     * Invoke to enable proxying of localhost requests
     * By default it is false
     * @return a config
     */
    HoverflyConfig proxyLocalHost();


    /**
     * Enable remote Hoverfly configurations
     * @return a {@link RemoteHoverflyConfig} implementation
     */
    default RemoteHoverflyConfig remote() {
        return new RemoteHoverflyConfigBuilder();
    }

    /**
     * Validate and build {@link HoverflyConfiguration}
     * @return a validated hoverfly configuration object
     */
    HoverflyConfiguration build();

    /**
     * Set proxy CA certificate to validate the authenticity of a Hoverfly instance.
     * If your hoverfly instance is not started with custom CA cert, then this option is not required.
     * @param proxyCaCert the path for the PEM encoded certificate relative to classpath
     * @return the {@link HoverflyConfig} for further customizations
     */
    HoverflyConfig proxyCaCert(String proxyCaCert);
}
