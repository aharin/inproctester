package com.thoughtworks.inproctester.core;

import java.net.URI;
import java.util.Set;

public interface InProcRequest {
    String getHttpMethod();

    URI getUri();

    String getFormData();

    String getHeader(String headerName);

    Set<String> getHeaderNames();

    void addHeader(String headerName, String header);
}
