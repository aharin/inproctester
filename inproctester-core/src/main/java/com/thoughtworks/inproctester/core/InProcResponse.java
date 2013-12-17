package com.thoughtworks.inproctester.core;

import java.util.Set;

public interface InProcResponse {
    int getStatus();

    byte[] getContentBytes();

    Set<String> getHeaderNames();

    String getHeader(String headerName);

    String getReason();
}
