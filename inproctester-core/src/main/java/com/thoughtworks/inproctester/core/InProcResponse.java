package com.thoughtworks.inproctester.core;

import java.util.Collection;

public interface InProcResponse {
    int getStatus();

    byte[] getContentBytes();

    Collection<String> getHeaderNames();

    String getHeader(String headerName);

    String getReason();
}
