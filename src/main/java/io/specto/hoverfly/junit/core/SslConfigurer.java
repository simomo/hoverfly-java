package io.specto.hoverfly.junit.core;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import static io.specto.hoverfly.junit.core.HoverflyUtils.findResourceOnClasspath;


/**
 * A component for configuring SSL context to enable HTTPS connection to hoverfly instance
 */
class SslConfigurer {

    private static final String TLS_PROTOCOL = "TLSv1.2";

    void setDefaultSslContext() {
        setDefaultSslContext("cert.pem");
    }
    /**
     * Sets the JVM trust store so Hoverfly's SSL certificate is trusted
     */

    void setDefaultSslContext(String pemFilename) {
        setDefaultSslContext(findResourceOnClasspath(pemFilename));
    }

    private void setDefaultSslContext(URL pemFile) {
        try (InputStream pemInputStream = pemFile.openStream()) {

            KeyStore trustStore = createTrustStore(pemInputStream);
            TrustManager[] trustManagers = createTrustManagers(trustStore);

            SSLContext sslContext = createSslContext(trustManagers);
            SSLContext.setDefault(sslContext);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set SSLContext from hoverfly certificate " + pemFile.toString(), e);
        }
    }

    private static KeyStore createTrustStore(InputStream pemInputStream) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        CertificateFactory certFactory = CertificateFactory.getInstance("X509");
        X509Certificate cert = (X509Certificate) certFactory.generateCertificate(pemInputStream);

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null);

        String alias = cert.getSubjectX500Principal().getName();
        trustStore.setCertificateEntry(alias, cert);
        return trustStore;
    }

    /**
     * Create custom trust manager that verify server authenticity using both default JVM trust store and hoverfly default trust store
     */
    private TrustManager[] createTrustManagers(KeyStore hoverflyTrustStore) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        // initialize a trust manager factory with default trust store
        X509TrustManager defaultTm = getTrustManager(tmf, null);

        // initialize a trust manager factory with hoverfly trust store
        X509TrustManager hoverflyTm = getTrustManager(tmf, hoverflyTrustStore);

        X509TrustManager customTm = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                defaultTm.checkClientTrusted(x509Certificates, s);
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                try {
                    hoverflyTm.checkServerTrusted(x509Certificates, s);
                } catch (CertificateException e) {
                    defaultTm.checkServerTrusted(x509Certificates, s);
                }
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return defaultTm.getAcceptedIssuers();
            }
        };
        return new TrustManager[] { customTm };
    }

    private SSLContext createSslContext(TrustManager[] trustManagers) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance(TLS_PROTOCOL);
        sslContext.init(null, trustManagers, null);
        return sslContext;
    }

    private X509TrustManager getTrustManager(TrustManagerFactory trustManagerFactory, KeyStore trustStore) throws KeyStoreException {
        trustManagerFactory.init(trustStore);

        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        return Arrays.stream(trustManagers)
                    .filter(tm -> tm instanceof X509TrustManager)
                    .map(tm -> (X509TrustManager) tm)
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);
    }
}
