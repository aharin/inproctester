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
package com.thoughtworks.inproctester.jersey;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.TerminatingClientHandler;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.header.InBoundHeaders;
import com.thoughtworks.inproctester.htmlunit.UrlHelper;
import com.thoughtworks.inproctester.jetty.HttpAppTester;
import com.thoughtworks.inproctester.jetty.HttpAppTesterExtensions;
import org.eclipse.jetty.testing.HttpTester;

import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class InPocessClientHandler extends TerminatingClientHandler {
    private final HttpAppTester w;

    public InPocessClientHandler(HttpAppTester appTester) {
        this.w = appTester;
    }


    public ClientResponse handle(ClientRequest clientRequest) {
        byte[] requestEntity = writeRequestEntity(clientRequest);

        final HttpTester cRequest = new HttpTester();
        cRequest.setMethod(clientRequest.getMethod());
        cRequest.setURI(UrlHelper.getRequestPath(clientRequest.getURI()));
        cRequest.addHeader("Host", UrlHelper.getRequestHost(clientRequest.getURI()));
        try {
            cRequest.setContent(new String(requestEntity, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ContainerException(e);
        }
        writeOutBoundHeaders(clientRequest.getHeaders(), cRequest);

        HttpTester cResponse;
        try {
            cResponse = HttpAppTesterExtensions.processRequest(w, cRequest);
        } catch (IOException e) {
            throw new ContainerException(e);
        }

        ClientResponse clientResponse;
        try {
            clientResponse = new ClientResponse(
                    cResponse.getStatus(),
                    getInBoundHeaders(cResponse),
                    new ByteArrayInputStream(getContent(cResponse)),
                    getMessageBodyWorkers());
        } catch (UnsupportedEncodingException e) {
            throw new ContainerException(e);
        }

        return clientResponse;
    }

    private byte[] getContent(HttpTester cResponse) throws UnsupportedEncodingException {
        String contentString = cResponse.getContent();
        if (contentString == null) contentString = "";
        return contentString.getBytes(cResponse.getCharacterEncoding());
    }

    private void writeOutBoundHeaders(MultivaluedMap<String, Object> headers, HttpTester uc) {
        for (Map.Entry<String, List<Object>> e : headers.entrySet()) {
            for (Object v : e.getValue()) {
                uc.addHeader(e.getKey(), ClientRequest.getHeaderValue(v));
            }
        }
    }

    private InBoundHeaders getInBoundHeaders(HttpTester httpTester) {
        InBoundHeaders headers = new InBoundHeaders();
        Enumeration headerNames = httpTester.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toString();
            Enumeration headerValues = httpTester.getHeaderValues(headerName);
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement().toString();
                headers.add(headerName, headerValue);
            }
        }
        return headers;
    }

    private byte[] writeRequestEntity(ClientRequest ro) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writeRequestEntity(ro, new RequestEntityWriterListener() {

                public void onRequestEntitySize(long size) throws IOException {
                }

                public OutputStream onGetOutputStream() throws IOException {
                    return baos;
                }
            });
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new ClientHandlerException(ex);
        }
    }

}
