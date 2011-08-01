package com.thoughtworks.inproctester.jersey.testapp;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class TestApplication {


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestResource getResource() {
        return new TestResource(1, "test");
    }
}
