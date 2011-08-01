package com.thoughtworks.inproctester.jersey.tests;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.WebAppDescriptor;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

public class InProcessJerseyTest extends JerseyTest {


    public static ClientConfig loadClientConfig() {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses().add(JacksonJsonProvider.class);
        return clientConfig;
    }


    private ObjectMapper objectMapper = new ObjectMapper();

    public InProcessJerseyTest() throws Exception {
        super(new WebAppDescriptor.Builder("com.thoughtworks.inproctester.jersey.testapp")
                .clientConfig(loadClientConfig())
//                .initParam("com.sun.jersey.config.feature.Redirect", "true")
                .initParam("com.sun.jersey.api.json.POJOMappingFeature", "true")
                .contextPath("/jersey").build());
    }


    @Test
    public void shouldGetResource() throws Exception {
        WebResource webResource = resource();
        JsonNode resource = webResource.path("/").accept(MediaType.APPLICATION_JSON_TYPE).get(JsonNode.class);
        Assert.assertEquals(objectMapper.readValue("{\"id\":1, \"name\":\"test\"}", JsonNode.class), resource);
    }


}

