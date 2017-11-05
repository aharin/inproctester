package com.thoughtworks.inproctester.resteasy;

import com.thoughtworks.inproctester.core.InProcConnection;
import com.thoughtworks.inproctester.core.InProcRequest;
import com.thoughtworks.inproctester.core.InProcResponse;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.SelfExpandingBufferredInputStream;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InProcessClientExecutor implements ClientExecutor {
	
	private static final Logger LOGGER = Logger.getLogger(InProcessClientExecutor.class.getName());
	private static final String CONNECTION_RELEASE_FAILURE_MSG = "An exception occured while trying to release the connection";
	
    private List<TesterRoute> testerRoutes = new ArrayList<>();

    public InProcessClientExecutor() {
    }

    public InProcessClientExecutor(InProcConnection httpAppTester) {
        addTesterRoute(new AnyRouteMatcher(), httpAppTester);
    }

    public InProcessClientExecutor addTesterRoute(RouteMatcher routeMatcher, InProcConnection tester) {
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

        final InProcResponse testerResponse = routeToTesterApplication(testerRequest.getUri()).getResponses(testerRequest);

        BaseClientResponse<?> clientResponse = new BaseClientResponse(new BaseClientResponse.BaseClientResponseStreamFactory() {
            InputStream stream;

            public InputStream getInputStream() {
                if (stream == null) {
                    stream = new SelfExpandingBufferredInputStream(new ByteArrayInputStream(testerResponse.getContentBytes()));
                }
                return stream;
            }

            public void performReleaseConnection() {
                try {
                    stream.close();
                } catch (Exception ex) {
                	LOGGER.log(Level.FINE,CONNECTION_RELEASE_FAILURE_MSG , ex);
                }
            }
        }, this);
        clientResponse.setStatus(testerResponse.getStatus());
        clientResponse.setHeaders(extractHeaders(testerResponse));
        clientResponse.setProviderFactory(clientRequest.getProviderFactory());

        return clientResponse;
    }

    public void close() throws Exception {
    	// Not implemented yet
    }

    private InProcConnection routeToTesterApplication(URI requestUri) {
        for (TesterRoute route : testerRoutes) {
            if (route.matches(requestUri)) {
                return route.getHttpAppTester();
            }
        }
        throw new NotFoundException("Unknown Route: " + requestUri);
    }

    private MultivaluedMap<String, String> extractHeaders(InProcResponse inProcResponse) {
        final CaseInsensitiveMap<String> headers = new CaseInsensitiveMap<>();
        Collection<String> headerNames = inProcResponse.getHeaderNames();
        for (String headerName : headerNames) {
            String headerValue = inProcResponse.getHeader(headerName);
            headers.add(headerName, headerValue);
        }
        return headers;
    }


}
