package com.thoughtworks.inproctester.resteasy;

import com.thoughtworks.inproctester.core.InProcRequest;
import com.thoughtworks.inproctester.core.UrlHelper;
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

public class RestEasyClientInProcRequest implements InProcRequest {
    private ClientRequest clientRequest;
    private Map<String, String> headers = new HashMap<String, String>();

    public RestEasyClientInProcRequest(ClientRequest clientRequest) {
        this.clientRequest = clientRequest;
        try {
            headers.put("Host", UrlHelper.getRequestHost(new URI(clientRequest.getUri())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        headers.put("Content-type", clientRequest.getBodyContentType().toString());
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContent() {
        try {
            return new String(writeRequestEntity(clientRequest), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
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
            throw new RuntimeException("You cannot send both form parameters and an entity body");

        if (!clientRequest.getFormParameters().isEmpty()) {
            throw new UnsupportedOperationException("InProcessClientExecutpr doesn't support form parameters yet");
        }

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (clientRequest.getBody() != null) {
            if ("GET".equals(clientRequest.getHttpMethod()))
                throw new RuntimeException("A GET request cannot have a body.");

            try {
                clientRequest.writeRequestBody(clientRequest.getHeadersAsObjects(), baos);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return baos.toByteArray();
    }

}
