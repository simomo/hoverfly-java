package io.specto.hoverfly.junit.core;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import io.specto.hoverfly.junit.api.HoverflyClient;
import io.specto.hoverfly.junit.core.model.Simulation;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.After;
import org.junit.Test;
import org.mockito.InOrder;
import org.powermock.reflect.Whitebox;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.exec.StartedProcess;

import javax.net.ssl.SSLContext;
import java.net.URL;

import static io.specto.hoverfly.junit.core.HoverflyConfig.configs;
import static io.specto.hoverfly.junit.core.HoverflyMode.SIMULATE;
import static io.specto.hoverfly.junit.core.SimulationSource.classpath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;

public class HoverflyTest {

    private static final int EXPECTED_PROXY_PORT = 8890;
    private Hoverfly hoverfly;
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldStartHoverflyOnConfiguredPort() throws Exception {

        hoverfly = new Hoverfly(configs().proxyPort(EXPECTED_PROXY_PORT), SIMULATE);
        hoverfly.start();
        assertThat(System.getProperty("http.proxyPort")).isEqualTo(String.valueOf(EXPECTED_PROXY_PORT));
        assertThat(hoverfly.getHoverflyConfig().getProxyPort()).isEqualTo(EXPECTED_PROXY_PORT);
    }

    @Test
    public void shouldDeleteTempFilesWhenStoppingHoverfly() throws Exception {
        // Given
        hoverfly = new Hoverfly(SIMULATE);
        TempFileManager tempFileManager = spy(TempFileManager.class);
        Whitebox.setInternalState(hoverfly, "tempFileManager", tempFileManager);

        // When
        hoverfly.close();

        // Then
        verify(tempFileManager).purge();
    }

    @Test
    public void shouldImportSimulation() throws Exception {
        startDefaultHoverfly();
        // When
        URL resource = Resources.getResource("test-service.json");
        Simulation importedSimulation = mapper.readValue(resource, Simulation.class);
        hoverfly.importSimulation(classpath("test-service.json"));

        // Then
        Simulation exportedSimulation = hoverfly.getSimulation();
        assertThat(exportedSimulation).isEqualTo(importedSimulation);
    }

    @Test
    public void shouldThrowExceptionWhenProxyPortIsAlreadyInUse() throws Exception {
        // Given
        startDefaultHoverfly();

        try (Hoverfly portClashHoverfly = new Hoverfly(configs().proxyPort(hoverfly.getHoverflyConfig().getProxyPort()), SIMULATE)) {
            // When
            Throwable throwable = catchThrowable(portClashHoverfly::start);

            //Then
            assertThat(throwable)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Port is already in use");
        }
    }

    @Test
    public void shouldThrowExceptionWhenAdminPortIsAlreadyInUse() throws Exception {
        // Given
        startDefaultHoverfly();

        try (Hoverfly portClashHoverfly = new Hoverfly(configs().adminPort(hoverfly.getHoverflyConfig().getAdminPort()), SIMULATE)) {
            // When
            Throwable throwable = catchThrowable(portClashHoverfly::start);

            //Then
            assertThat(throwable)
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Port is already in use");
        }
    }

    @Test
    public void shouldWarnWhenStartHoverflyInstanceTwice() {
        // Given
        // Reference: https://dzone.com/articles/unit-testing-asserting-line
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        final Appender mockAppender = mock(Appender.class);
        when(mockAppender.getName()).thenReturn("test-shouldWarnWhenStartHoverflyInstanceTwice");
        root.addAppender(mockAppender);

        startDefaultHoverfly();

        // when
        hoverfly.start();

        // then
        verify(mockAppender).doAppend(argThat(argument -> {
            LoggingEvent event = (LoggingEvent) argument;
            return event.getLevel().levelStr.equals("WARN") &&
                    event.getMessage().contains("Local Hoverfly is already running");
        }));
    }


    @Test
    public void shouldNotOverrideDefaultTrustManager() throws Exception {
        startDefaultHoverfly();

        HttpClient client = HttpClientBuilder.create().setSSLContext(SSLContext.getDefault()).build();

        // TODO: Find better way to test trust store
        HttpResponse response = client.execute(new HttpGet("https://specto.io"));
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(OK.value());
    }


    @Test
    public void shouldWaitForHoverflyProcessTerminatedBeforeDeletingBinary() throws Exception {
        // Given
        hoverfly = new Hoverfly(SIMULATE);

        TempFileManager tempFileManager = spy(TempFileManager.class);
        Whitebox.setInternalState(hoverfly, "tempFileManager", tempFileManager);

        StartedProcess mockStartedProcess = mock(StartedProcess.class);
        Whitebox.setInternalState(hoverfly, "startedProcess", mockStartedProcess);
        Process mockProcess = mock(Process.class);
        when(mockStartedProcess.getProcess()).thenReturn(mockProcess);

        // When
        hoverfly.close();

        // Then
        InOrder inOrder = inOrder(mockProcess, tempFileManager);
        inOrder.verify(mockProcess).destroy();
        inOrder.verify(mockProcess).waitFor();
        inOrder.verify(tempFileManager).purge();
    }


    @Test
    public void shouldSetTrustStoreWhenStartingHoverfly() throws Exception {
        // Given
        hoverfly = new Hoverfly(SIMULATE);
        SslConfigurer sslConfigurer = mock(SslConfigurer.class);
        Whitebox.setInternalState(hoverfly, "sslConfigurer", sslConfigurer);

        // When
        hoverfly.start();

        // Then
        verify(sslConfigurer).setDefaultSslContext();
    }

    @Test
    public void shouldNotSetJVMTrustStoreIfSslCertificatePathExists() throws Exception {
        // Given
        hoverfly = new Hoverfly(configs()
                .sslCertificatePath("ssl/ca.crt")
                .sslKeyPath("ssl/ca.key"), SIMULATE);
        SslConfigurer sslConfigurer = mock(SslConfigurer.class);
        Whitebox.setInternalState(hoverfly, "sslConfigurer", sslConfigurer);

        // When
        hoverfly.start();

        // Then
        verify(sslConfigurer, never()).setDefaultSslContext();
    }

    @Test
    public void shouldSetSslCertForRemoteInstance() throws Exception {

        hoverfly = new Hoverfly(configs().remote().host("remotehost").proxyCaCert("ssl/ca.crt"), SIMULATE);

        SslConfigurer sslConfigurer = mock(SslConfigurer.class);
        Whitebox.setInternalState(hoverfly, "sslConfigurer", sslConfigurer);

        HoverflyClient hoverflyClient = mock(HoverflyClient.class);
        Whitebox.setInternalState(hoverfly, "hoverflyClient", hoverflyClient);

        when(hoverflyClient.getHealth()).thenReturn(true);

        // When
        hoverfly.start();

        // Then
        verify(sslConfigurer).setDefaultSslContext("ssl/ca.crt");
    }

    @Test
    public void shouldCopySslCertAndKeyToTempFolderIfPresent () throws Exception {
        // Given
        hoverfly = new Hoverfly(configs()
                .sslCertificatePath("ssl/ca.crt")
                .sslKeyPath("ssl/ca.key"), SIMULATE);
        TempFileManager tempFileManager = spy(TempFileManager.class);
        Whitebox.setInternalState(hoverfly, "tempFileManager", tempFileManager);

        // When
        hoverfly.start();

        // Then
        verify(tempFileManager).copyClassPathResource("ssl/ca.crt", "ca.crt");
        verify(tempFileManager).copyClassPathResource("ssl/ca.key", "ca.key");
    }


    @Test
    public void shouldCopyHoverflyBinaryToTempFolderOnStart() throws Exception {

        // Given
        hoverfly = new Hoverfly(SIMULATE);
        TempFileManager tempFileManager = spy(TempFileManager.class);
        Whitebox.setInternalState(hoverfly, "tempFileManager", tempFileManager);

        // When
        hoverfly.start();

        // Then
        verify(tempFileManager).copyHoverflyBinary(any(SystemConfig.class));
    }

    @Test
    public void shouldValidateHoverflyConfigBeforeStart() throws Exception {

        hoverfly = new Hoverfly(SIMULATE);

        assertThat(hoverfly.getHoverflyConfig().getProxyPort()).isNotZero();
        assertThat(hoverfly.getHoverflyConfig().getAdminPort()).isNotZero();
    }


    @Test
    public void shouldSetSystemPropertiesForLocalHoverflyInstance() throws Exception {

        startDefaultHoverfly();

        assertThat(System.getProperty("http.proxyHost")).isEqualTo("localhost");
        assertThat(System.getProperty("https.proxyHost")).isEqualTo("localhost");

        assertThat(System.getProperty("http.proxyPort")).isEqualTo(String.valueOf(hoverfly.getHoverflyConfig().getProxyPort()));
        assertThat(System.getProperty("https.proxyPort")).isEqualTo(String.valueOf(hoverfly.getHoverflyConfig().getProxyPort()));

        assertThat(System.getProperty("http.nonProxyHosts")).isEqualTo("local|*.local|169.254/16|*.169.254/16");

    }

    @Test
    public void shouldSetNonProxyHostSystemPropertyToEmptyIfIsProxyLocalHost() throws Exception {
        hoverfly = new Hoverfly(configs().proxyLocalHost(), SIMULATE);
        hoverfly.start();

        assertThat(System.getProperty("http.nonProxyHosts")).isEqualTo("");
    }

    @Test
    public void shouldBeAbleToUseHoverflyInTryWithResourceStatement() throws Exception {
        StartedProcess startedProcess = null;
        try (Hoverfly hoverfly = new Hoverfly(SIMULATE)) {

            hoverfly.start();
            startedProcess = Whitebox.getInternalState(hoverfly, "startedProcess");
        } finally {

            assertThat(startedProcess.getProcess().isAlive()).isFalse();
        }

    }

    @After
    public void tearDown() throws Exception {
        if (hoverfly != null) {
            hoverfly.close();
        }
    }

    private void startDefaultHoverfly() {
        hoverfly = new Hoverfly(SIMULATE);
        hoverfly.start();
    }

}
