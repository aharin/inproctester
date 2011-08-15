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
package com.thoughtworks.inproctester.jersey.testapp;

import com.sun.jersey.spi.resource.Singleton;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@Singleton
public class TestApplication {

    private List<TestResource> resources = new ArrayList<TestResource>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestResource getResource() {
        return new TestResource(1, "test");
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addResource(TestResource testResource) {
        resources.add(testResource);
    }
}
