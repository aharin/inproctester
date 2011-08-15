package com.thoughtworks.inproctester.resteasy.tests;

import com.thoughtworks.inproctester.jetty.HttpAppTester;
import com.thoughtworks.inproctester.resteasy.InProcessClientExecutor;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyBootstrap;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

public class InProcessResteasyTest {


    private ObjectMapper objectMapper = new ObjectMapper();

    private static HttpAppTester httpAppTester;

    @BeforeClass
    public static void setUp() {
        httpAppTester = new HttpAppTester("/");
        httpAppTester.setInitParameter("resteasy.resources", "com.thoughtworks.inproctester.resteasy.testapp.TestApplication");
        httpAppTester.setInitParameter("resteasy.providers", "org.jboss.resteasy.plugins.providers.jackson.ResteasyJacksonProvider");
        httpAppTester.addEventListener(new ResteasyBootstrap());
        httpAppTester.addServlet(HttpServletDispatcher.class, "/*");

        httpAppTester.start();
    }

    @AfterClass
    public static void tearDown() {
        httpAppTester.stop();
    }

    @Test
    public void shouldAddResource() throws Exception {
        ClientRequest request = new ClientRequest("http://localhost/", new InProcessClientExecutor(httpAppTester));
        JsonNode testResource = objectMapper.readValue("{\"name\":\"test\"}", JsonNode.class);
        ClientResponse<JsonNode> createResponse = request.body(MediaType.APPLICATION_JSON, testResource).post(JsonNode.class);

        Assert.assertEquals(201, createResponse.getStatus());
        JsonNode entity = createResponse.getEntity();
        Assert.assertEquals(testResource, entity);

//        JsonNode testResourceFromServer = webResource.uri(createResponse.getLocation()).accept(MediaType.APPLICATION_JSON).get(JsonNode.class);
//        Assert.assertEquals(testResource, testResourceFromServer);
    }


}

