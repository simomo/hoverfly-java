package io.specto.hoverfly.junit.core.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class HoverflyInfoTest {

    @Test
    public void shouldNotSerializeNullField() throws Exception {

        HoverflyInfo hoverflyInfo = new HoverflyInfo("www.test.com", null, null, null);

        ObjectMapper objectMapper = new ObjectMapper();

        String json = objectMapper.writeValueAsString(hoverflyInfo);

        JSONAssert.assertEquals("{\"destination\": \"www.test.com\"}", json, true);

    }
}