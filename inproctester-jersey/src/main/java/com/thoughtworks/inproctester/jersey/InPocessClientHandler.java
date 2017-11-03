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
import com.thoughtworks.inproctester.core.InProcResponse;
import com.thoughtworks.inproctester.core.UrlHelper;
import com.thoughtworks.inproctester.jetty.HttpAppTester;

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

        InProcResponse cResponse = w.getResponses(cRequest);

        return new ClientResponse(
                cResponse.getStatus(),
                getInBoundHeaders(cResponse),
                new ByteArrayInputStream(cResponse.getContentBytes()),
                getMessageBodyWorkers());
    }

    private InBoundHeaders getInBoundHeaders(InProcResponse inProcResponse) {
        InBoundHeaders headers = new InBoundHeaders();
        Collection<String> headerNames = inProcResponse.getHeaderNames();

        for (String headerName : headerNames) {
            String headerValue = inProcResponse.getHeader(headerName);
            headers.add(headerName, headerValue);
        }
        return headers;
    }

    class JerseyClientInprocRequest implements InProcRequest {
        private Map<String, String> headers = new HashMap<>();
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
        public String getContent() {
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
            HashMap<String, String> map = new HashMap<>();
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
                    	// Not yet implemented
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
