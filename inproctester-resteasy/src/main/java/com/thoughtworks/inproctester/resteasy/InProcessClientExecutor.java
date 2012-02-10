package com.thoughtworks.inproctester.resteasy;

import com.thoughtworks.inproctester.jetty.InProcServerExtensions;
import com.thoughtworks.inproctester.jetty.InProcServer;
import com.thoughtworks.inproctester.jetty.UrlHelper;
import org.eclipse.jetty.http.HttpException;
import org.eclipse.jetty.testing.HttpTester;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.SelfExpandingBufferredInputStream;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class InProcessClientExecutor implements ClientExecutor {

    private List<TesterRoute> testerRoutes = new ArrayList<TesterRoute>();

    public InProcessClientExecutor() {
    }

    public InProcessClientExecutor(final InProcServer inProcServer) {
        addTesterRoute(new AnyRouteMatcher(), inProcServer);
    }

    public InProcessClientExecutor addTesterRoute(RouteMatcher routeMatcher, InProcServer inProcServer) {
        testerRoutes.add(new TesterRoute(routeMatcher, inProcServer));
        return this;
    }

    public ClientRequest createRequest(String uriTemplate) {
        return new ClientRequest(uriTemplate, this);
    }

    public ClientRequest createRequest(UriBuilder uriBuilder) {
        return new ClientRequest(uriBuilder, this);
    }

    public ClientResponse execute(ClientRequest clientRequest) throws Exception {

        final HttpTester testerRequest = new HttpTester();
        testerRequest.setMethod(clientRequest.getHttpMethod());
        URI requestUri = new URI(clientRequest.getUri());
        testerRequest.setURI(UrlHelper.getRequestPath(requestUri));
        testerRequest.addHeader("Host", UrlHelper.getRequestHost(requestUri));
        loadContent(clientRequest, testerRequest);
        writeOutBoundHeaders(clientRequest.getHeaders(), testerRequest);

        final HttpTester testerResponse = InProcServerExtensions.processRequest(routeToTesterApplication(requestUri), testerRequest);

        BaseClientResponse<?> clientResponse = new BaseClientResponse(new BaseClientResponse.BaseClientResponseStreamFactory() {
            InputStream stream;

            public InputStream getInputStream() throws IOException {
                if (stream == null) {
                    stream = new SelfExpandingBufferredInputStream(new ByteArrayInputStream(getContent(testerResponse)));
                }
                return stream;
            }

            public void performReleaseConnection() {
                try {
                    stream.close();
                } catch (Exception ignored) {
                }
            }
        }, this);
        clientResponse.setStatus(testerResponse.getStatus());
        clientResponse.setHeaders(extractHeaders(testerResponse));
        clientResponse.setProviderFactory(clientRequest.getProviderFactory());

        return clientResponse;
    }

    private InProcServer routeToTesterApplication(URI requestUri) throws HttpException {
        for (TesterRoute route : testerRoutes) {
            if (route.matches(requestUri)) {
                return route.getInProcServer();
            }
        }
        throw new HttpException(404, "Unknown Route: " + requestUri);
    }

    private void loadContent(ClientRequest clientRequest, HttpTester testerRequest) throws IOException {

        if (clientRequest.getBody() != null && !clientRequest.getFormParameters().isEmpty())
            throw new RuntimeException("You cannot send both form parameters and an entity body");

        if (!clientRequest.getFormParameters().isEmpty()) {
            throw new UnsupportedOperationException("InProcessClientExecutpr doesn't support form parameters yet");
        } else if (clientRequest.getBody() != null) {
            if ("GET".equals(clientRequest.getHttpMethod()))
                throw new RuntimeException("A GET request cannot have a body.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            clientRequest.writeRequestBody(clientRequest.getHeadersAsObjects(), baos);
            byte[] requestEntity = writeRequestEntity(clientRequest);
            testerRequest.setHeader("Content-type", clientRequest.getBodyContentType().toString());
            testerRequest.setContent(new String(requestEntity, "UTF-8"));
        }
    }

    private byte[] getContent(HttpTester cResponse) throws UnsupportedEncodingException {
        String contentString = cResponse.getContent();
        if (contentString == null) contentString = "";
        return contentString.getBytes(cResponse.getCharacterEncoding());
    }


    private MultivaluedMap<String, String> extractHeaders(HttpTester httpTester) {
        final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<String>();
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

    private void writeOutBoundHeaders(MultivaluedMap<String, String> headers, HttpTester uc) {
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            for (String v : header.getValue()) {
                uc.addHeader(header.getKey(), v);
            }
        }
    }


    private byte[] writeRequestEntity(ClientRequest ro) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ro.writeRequestBody(ro.getHeadersAsObjects(), baos);
        return baos.toByteArray();
    }

}
