package io.specto.hoverfly.junit.core;

import com.google.common.io.Resources;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import io.specto.hoverfly.webserver.CaptureModeTestWebServer;
import org.json.JSONException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
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

public class MultiCaptureTest {

    private static final Path FIRST_RECORDED_SIMULATION_FILE = Paths.get("src/test/resources/hoverfly/firstScenario.json");
    private static final Path SECOND_RECORDED_SIMULATION_FILE = Paths.get("src/test/resources/hoverfly/secondScenario.json");
    private static final Path THIRD_RECORDED_SIMULATION_FILE = Paths.get("src/test/resources/hoverfly/thirdScenario.json");
    private static final String EXPECTED_SIMULATION_JSON = "expected-simulation.json";
    private static final String OTHER_EXPECTED_SIMULATION_JSON = "expected-simulation-other.json";

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode(configs().proxyLocalHost(true));

    private URI webServerBaseUrl;
    private RestTemplate restTemplate = new RestTemplate();

    @Before
    public void setUp() throws Exception {
        Files.deleteIfExists(FIRST_RECORDED_SIMULATION_FILE);
        Files.deleteIfExists(SECOND_RECORDED_SIMULATION_FILE);
        Files.deleteIfExists(THIRD_RECORDED_SIMULATION_FILE);
        webServerBaseUrl = CaptureModeTestWebServer.run();
    }

    @Test
    public void shouldRecordMultipleScenarios() throws Exception {
        // Given
        hoverflyRule.capture("firstScenario.json");

        // When
        restTemplate.getForObject(webServerBaseUrl, String.class);

        // Given
        hoverflyRule.capture("secondScenario.json");

        // When
        restTemplate.getForObject(webServerBaseUrl + "/other", String.class);

        // Given
        hoverflyRule.capture("thirdScenario.json");

        // When
        restTemplate.getForObject(webServerBaseUrl, String.class);
    }

    // We have to assert after the rule has executed because that's when the classpath is written to the filesystem
    @AfterClass
    public static void after() throws IOException, JSONException {
        final String expectedSimulation = Resources.toString(Resources.getResource(EXPECTED_SIMULATION_JSON), defaultCharset());
        final String otherExpectedSimulation = Resources.toString(Resources.getResource(OTHER_EXPECTED_SIMULATION_JSON), defaultCharset());

        final String firstActualSimulation = new String(Files.readAllBytes(FIRST_RECORDED_SIMULATION_FILE), defaultCharset());
        final String secondActualSimulation = new String(Files.readAllBytes(SECOND_RECORDED_SIMULATION_FILE), defaultCharset());
        final String thirdActualSimulation = new String(Files.readAllBytes(THIRD_RECORDED_SIMULATION_FILE), defaultCharset());

        JSONAssert.assertEquals(expectedSimulation, firstActualSimulation, JSONCompareMode.LENIENT);
        JSONAssert.assertEquals(otherExpectedSimulation, secondActualSimulation, JSONCompareMode.LENIENT);
        JSONAssert.assertEquals(expectedSimulation, thirdActualSimulation, JSONCompareMode.LENIENT);

        CaptureModeTestWebServer.terminate();
    }
}
