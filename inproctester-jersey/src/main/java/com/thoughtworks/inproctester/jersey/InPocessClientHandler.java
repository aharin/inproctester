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

import com.sun.jersey.api.client.*;
import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.header.InBoundHeaders;
import com.thoughtworks.inproctester.core.InProcRequest;
import com.thoughtworks.inproctester.jetty.HttpAppTester;
import com.thoughtworks.inproctester.jetty.HttpAppTesterExtensions;
import com.thoughtworks.inproctester.jetty.UrlHelper;
import org.eclipse.jetty.testing.HttpTester;

import javax.ws.rs.core.MultivaluedMap;
import java.io.*;
import java.net.URI;
import java.util.*;

public class InPocessClientHandler extends TerminatingClientHandler {
    private final HttpAppTester w;

    public InPocessClientHandler(HttpAppTester appTester) {
        this.w = appTester;
    }

    public ClientResponse handle(ClientRequest clientRequest) {
        final InProcRequest cRequest = new JerseyClientInprocRequest(clientRequest);

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

    class JerseyClientInprocRequest implements InProcRequest {
        private Map<String, String> headers = new HashMap<String, String>();
        private ClientRequest clientRequest;

        public JerseyClientInprocRequest(ClientRequest clientRequest) {
            this.clientRequest = clientRequest;
            headers.put("Host", UrlHelper.getRequestHost(clientRequest.getURI()));
            headers.putAll(asMap(clientRequest.getHeaders()));
        }


        @Override
        public String getHttpMethod() {
            return clientRequest.getMethod();
        }

        @Override
        public URI getUri() {
            return clientRequest.getURI();
        }

        @Override
        public String getFormData() {
            byte[] requestEntity = writeRequestEntity(clientRequest);

            try {
                return new String(requestEntity, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new ContainerException(e);
            }
        }

        @Override
        public String getHeader(String headerName) {
            return headers.get(headerName);
        }

        @Override
        public Set<String> getHeaderNames() {
            return headers.keySet();
        }

        @Override
        public void addHeader(String headerName, String header) {
            headers.put(headerName, header);
        }

        private Map<String, String> asMap(MultivaluedMap<String, Object> headers) {
            HashMap<String, String> map = new HashMap<String, String>();
            for (Map.Entry<String, List<Object>> e : headers.entrySet()) {
                for (Object v : e.getValue()) {
                    map.put(e.getKey(), ClientRequest.getHeaderValue(v));
                }
            }
            return map;
        }

        private byte[] writeRequestEntity(ClientRequest ro) {
            try {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InPocessClientHandler.this.writeRequestEntity(ro, new RequestWriter.RequestEntityWriterListener() {

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
}
