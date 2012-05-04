package com.thoughtworks.inproctester.core;

import java.util.Set;

public interface InProcResponse {
    int getStatus();

    String getContent();

    Set<String> getHeaderNames();

    String getHeader(String headerName);

    String getCharacterEncoding();

    String getReason();
}
