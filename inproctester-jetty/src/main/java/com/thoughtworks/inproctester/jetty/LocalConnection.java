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

import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.testing.HttpTester;
import org.eclipse.jetty.util.StringUtil;

import java.io.IOException;

public class LocalConnection implements InProcConnection {

    private LocalConnector connector;

    public LocalConnection(LocalConnector connector) {
        this.connector = connector;
    }


    private String getResponses(String rawRequests) {
        try {
            ByteArrayBuffer result = connector.getResponses(new ByteArrayBuffer(rawRequests, StringUtil.__UTF8), false);
            return result == null ? null : result.toString(StringUtil.__UTF8);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getResponses(InProcRequest request) {
        HttpTester httpTester = new HttpTester();
        httpTester.setMethod(request.getHttpMethod());
        httpTester.setURI(UrlHelper.getRequestPath(request.getUri()));
        for (String headerName : request.getHeaderNames()) {
            httpTester.addHeader(headerName, request.getHeader(headerName));
        }

        if ("POST".equals(request.getHttpMethod())) {
            httpTester.setContent(request.getFormData());
        }

        try {
            return getResponses(httpTester.generate());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
