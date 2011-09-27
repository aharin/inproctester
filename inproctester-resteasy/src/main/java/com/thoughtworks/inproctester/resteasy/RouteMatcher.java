package com.thoughtworks.inproctester.resteasy;

import java.net.URI;

public interface RouteMatcher {
    public boolean routeMatches(URI requestUri);
}
