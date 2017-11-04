package com.thoughtworks.inproctester.resteasy;

import com.thoughtworks.inproctester.core.InProcRequest;
import com.thoughtworks.inproctester.core.UrlHelper;
import com.thoughtworks.inproctester.resteasy.exceptions.RequestEntityWriteException;
import com.thoughtworks.inproctester.resteasy.exceptions.RequestHostException;
import com.thoughtworks.inproctester.resteasy.exceptions.UriRetrievalException;

import org.jboss.resteasy.client.ClientRequest;

import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestEasyClientInProcRequest implements InProcRequest {
	
	private static final Logger LOGGER = Logger.getLogger(RestEasyClientInProcRequest.class.getName());
	private static final String REQUEST_ENTITY_WRITE_EXCEPTION_MESSAGE = "Unable to write from request entity";
	
    private ClientRequest clientRequest;
    private Map<String, String> headers = new HashMap<String, String>();

    public RestEasyClientInProcRequest(ClientRequest clientRequest) {
        this.clientRequest = clientRequest;
        try {
            headers.put("Host", UrlHelper.getRequestHost(new URI(clientRequest.getUri())));
        } catch (Exception e) {
            throw new RequestHostException(e);
        }
        if (clientRequest.getBodyContentType() != null) {
            headers.put("Content-type", clientRequest.getBodyContentType().toString());
        }
        headers.putAll(asMap(clientRequest.getHeaders()));
    }

    @Override
    public String getHttpMethod() {
        return clientRequest.getHttpMethod();
    }

    @Override
    public URI getUri() {
        try {
            return new URI(clientRequest.getUri());
        } catch (Exception e) {
            throw new UriRetrievalException(e);
        }
    }

    @Override
    public String getContent() {
        try {
            return new String(writeRequestEntity(clientRequest), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RequestEntityWriteException(e);
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

    private Map<String, String> asMap(MultivaluedMap<String, String> headers) {
        HashMap<String, String> map = new HashMap<String, String>();
        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            for (String v : header.getValue()) {
                map.put(header.getKey(), v);
            }
        }
        return map;
    }


    private byte[] writeRequestEntity(ClientRequest clientRequest) {

        if (clientRequest.getBody() != null && !clientRequest.getFormParameters().isEmpty())
            throw new RequestEntityWriteException("You cannot send both form parameters and an entity body");

        if (!clientRequest.getFormParameters().isEmpty()) {
            throw new UnsupportedOperationException("InProcessClientExecutpr doesn't support form parameters yet");
        }

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (clientRequest.getBody() != null) {
            if ("GET".equals(clientRequest.getHttpMethod()))
                throw new RequestEntityWriteException("A GET request cannot have a body.");

            try {
                clientRequest.writeRequestBody(clientRequest.getHeadersAsObjects(), baos);
            } catch (IOException e) {
            	LOGGER.log(Level.FINE, REQUEST_ENTITY_WRITE_EXCEPTION_MESSAGE, e);
                throw new RequestEntityWriteException(e);
            }
        }

        return baos.toByteArray();
    }

}
