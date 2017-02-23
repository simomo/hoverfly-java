package io.specto.hoverfly.ruletest;

import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;

import static io.specto.hoverfly.junit.core.SimulationSource.classpath;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;

public class RestEasyClientTest {

    @Rule
    public HoverflyRule hoverflyRule = HoverflyRule.inSimulationMode(classpath("test-service.json"));

    private ResteasyClient client;

    @Before
    public void setUp() {
        client = new ResteasyClientBuilder().defaultProxy("localhost", hoverflyRule.getProxyPort()).build();
    }

    @Test
    public void shouldBeAbleToGetABookingUsingRestEasyClientAndHoverfly() throws IOException {


        // Given
        ResteasyWebTarget target = client.target(UriBuilder.fromPath("http://www.my-test.com/api/bookings/1"));

        // When
        String body = target.request().accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);

        // Then
        assertThatJson(body).isEqualTo("{" +
                "\"bookingId\":\"1\"," +
                "\"origin\":\"London\"," +
                "\"destination\":\"Singapore\"," +
                "\"time\":\"2011-09-01T12:30\"," +
                "\"_links\":{\"self\":{\"href\":\"http://localhost/api/bookings/1\"}}" +
                "}");

    }
}
