package com.thoughtworks.inproctester.htmlunit;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.thoughtworks.inproctester.core.InProcRequest;
import com.thoughtworks.inproctester.core.UrlHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class HtmlUnitInProcRequest implements InProcRequest {
    private WebRequest request;
    private Map<String, String> headers = new HashMap<String, String>();

    public HtmlUnitInProcRequest(WebRequest request) {
        this.request = request;
        headers.put("Host", UrlHelper.getRequestHost(request.getUrl()));
        headers.put("Content-Type", request.getEncodingType().getName() + ";" + request.getCharset());
        headers.putAll(request.getAdditionalHeaders());
    }

    @Override
    public String getHttpMethod() {
        return request.getHttpMethod().name();
    }

    @Override
    public URI getUri() {
        try {
            return request.getUrl().toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContent() {
        return new UrlEncodedContent(request.getRequestParameters()).generateFormDataAsString();
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
}
