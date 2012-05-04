package com.thoughtworks.inproctester.resteasy;

import com.thoughtworks.inproctester.jetty.HttpAppTester;
import com.thoughtworks.inproctester.jetty.HttpAppTesterExtensions;
import com.thoughtworks.inproctester.jetty.InProcRequest;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class InProcessClientExecutor implements ClientExecutor {

    private List<TesterRoute> testerRoutes = new ArrayList<TesterRoute>();

    public InProcessClientExecutor() {
    }

    public InProcessClientExecutor(final HttpAppTester httpAppTester) {
        addTesterRoute(new AnyRouteMatcher(), httpAppTester);
    }

    public InProcessClientExecutor addTesterRoute(RouteMatcher routeMatcher, HttpAppTester tester) {
        testerRoutes.add(new TesterRoute(routeMatcher, tester));
        return this;
    }

    public ClientRequest createRequest(String uriTemplate) {
        return new ClientRequest(uriTemplate, this);
    }

    public ClientRequest createRequest(UriBuilder uriBuilder) {
        return new ClientRequest(uriBuilder, this);
    }

    public ClientResponse execute(ClientRequest clientRequest) throws Exception {

        final InProcRequest testerRequest = new RestEasyClientInProcRequest(clientRequest);

        final HttpTester testerResponse = HttpAppTesterExtensions.processRequest(routeToTesterApplication(testerRequest.getUri()), testerRequest);

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

    private HttpAppTester routeToTesterApplication(URI requestUri) throws HttpException {
        for (TesterRoute route : testerRoutes) {
            if (route.matches(requestUri)) {
                return route.getHttpAppTester();
            }
        }
        throw new HttpException(404, "Unknown Route: " + requestUri);
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


}
