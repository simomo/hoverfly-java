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
 * Config used to change the settings for {@link Hoverfly}
 */
class HoverflyConfigBuilder implements LocalHoverflyConfig {

    private int proxyPort;
    private int adminPort;
    private boolean proxyLocalHost;
    // TODO should be combined field?
    private String sslCertificatePath;
    private String sslKeyPath;
    private String destination;
    private String proxyCaCert;


    @Override
    public HoverflyConfig proxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        return this;
    }


    @Override
    public HoverflyConfig adminPort(int adminPort) {
        this.adminPort = adminPort;
        return this;
    }

    @Override
    public HoverflyConfig proxyLocalHost() {
        this.proxyLocalHost = true;
        return this;
    }

    @Override
    public LocalHoverflyConfig sslCertificatePath(String sslCertificatePath) {
        this.sslCertificatePath = sslCertificatePath;
        return this;
    }


    @Override
    public LocalHoverflyConfig sslKeyPath(String sslKeyPath) {
        this.sslKeyPath = sslKeyPath;
        return this;
    }


    @Override
    public HoverflyConfig proxyCaCert(String proxyCaCert) {
        this.proxyCaCert = proxyCaCert;
        return this;
    }

    @Override
    public HoverflyConfig destination(String destination) {
        this.destination = destination;
        return this;
    }

    @Override
    public HoverflyConfiguration build() {
        HoverflyConfiguration configs = new HoverflyConfiguration(proxyPort, adminPort, proxyLocalHost, destination, proxyCaCert, sslCertificatePath, sslKeyPath);
        HoverflyConfigValidator validator = new HoverflyConfigValidator();
        return validator.validate(configs);
    }


}
