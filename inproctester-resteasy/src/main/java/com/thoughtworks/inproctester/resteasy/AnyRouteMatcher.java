package com.thoughtworks.inproctester.resteasy;

import java.net.URI;

public class AnyRouteMatcher implements RouteMatcher {
    @Override
    public boolean routeMatches(URI requestUri) {
        return true;
    }
}
