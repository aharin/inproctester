package com.thoughtworks.inproctester.resteasy;

import java.net.URI;

public class HostAndPortRouteMatcher implements RouteMatcher {
    private String hostname;
    private int port;

    public HostAndPortRouteMatcher(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public boolean routeMatches(URI requestUri) {
        return hostname.equalsIgnoreCase(requestUri.getHost()) && port == requestUri.getPort();
    }
}
