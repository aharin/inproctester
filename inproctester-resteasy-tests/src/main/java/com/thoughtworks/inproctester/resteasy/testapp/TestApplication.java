/*  Copyright 2011 ThoughtWorks Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.thoughtworks.inproctester.resteasy.testapp;


import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;

@Path("/")
public class TestApplication {

    private static Map<Integer, TestResource> resources = new HashMap<>();

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public TestResource getResource(@PathParam("id") int id) {
        return resources.get(id);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addResource(TestResource testResource) {
        int nextId = resources.size();
        resources.put(nextId, testResource);
        return Response
                .created(uriInfo.getBaseUriBuilder().path(TestApplication.class, "getResource").build(nextId))
                .entity(testResource)
                .build();
    }
}
