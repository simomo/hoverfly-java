package io.specto.hoverfly.ruletest;

import com.google.common.io.Resources;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import io.specto.hoverfly.webserver.CaptureModeTestWebServer;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.specto.hoverfly.junit.core.HoverflyConfig.configs;
import static java.nio.charset.Charset.defaultCharset;

public class CaptureModeTest {


    private static final Path RECORDED_SIMULATION_FILE = Paths.get("src/test/resources/hoverfly/recorded-simulation.json");
    private static final String RECORDED_SIMULATION_JSON = "recorded-simulation.json";
    private static final String EXPECTED_SIMULATION_JSON = "expected-simulation.json";

    // tag::captureModeExample[]
    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode(RECORDED_SIMULATION_JSON, configs().proxyLocalHost());
    // end::captureModeExample[]

    private URI webServerBaseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    // We have to assert after the rule has executed because that's when the classpath is written to the filesystem
    @AfterClass
    public static void after() throws IOException, JSONException {
        final String expectedSimulation = Resources.toString(Resources.getResource(EXPECTED_SIMULATION_JSON), defaultCharset());
        final String actualSimulation = new String(Files.readAllBytes(RECORDED_SIMULATION_FILE), defaultCharset());
        JSONAssert.assertEquals(expectedSimulation, actualSimulation, JSONCompareMode.LENIENT);

        CaptureModeTestWebServer.terminate();
    }

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(RECORDED_SIMULATION_FILE);
        webServerBaseUrl = CaptureModeTestWebServer.run();
    }

    @Test
    public void shouldRecordInteractions() throws Exception {
        // When
        restTemplate.getForObject(webServerBaseUrl, String.class);
    }

}
