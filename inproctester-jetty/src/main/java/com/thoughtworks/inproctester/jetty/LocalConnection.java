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
package com.thoughtworks.inproctester.jetty;

import com.thoughtworks.inproctester.core.InProcRequest;
import com.thoughtworks.inproctester.core.InProcResponse;
import com.thoughtworks.inproctester.core.UrlHelper;
import com.thoughtworks.inproctester.jetty.exceptions.LocalConnectionResponseException;

import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.http.HttpTester;

import java.nio.ByteBuffer;

public class LocalConnection  {

    private LocalConnector connector;

    public LocalConnection(LocalConnector connector) {
        this.connector = connector;
    }


    private ByteBuffer getResponses(ByteBuffer rawRequests) {
        try {
            return connector.getResponses(rawRequests);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new LocalConnectionResponseException(e);
        }
    }

    public InProcResponse getResponses(InProcRequest request) {
        HttpTester.Request testerRequest = HttpTester.newRequest();
        testerRequest.setMethod(request.getHttpMethod());
        testerRequest.setURI(UrlHelper.getRequestPath(request.getUri()));
        String content = request.getContent();
        if (content != null) {
            testerRequest.setContent(content);
        }

        for (String headerName : request.getHeaderNames()) {
            testerRequest.add(headerName, request.getHeader(headerName));
        }
        ByteBuffer rawResponse = getResponses(testerRequest.generate());
        HttpTester.Response testerResponse = HttpTester.parseResponse(rawResponse);
        return new JettyInProcResponse(testerResponse);
    }
}
