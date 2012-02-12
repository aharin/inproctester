package com.thoughtworks.inproctester.resteasy;

import com.thoughtworks.inproctester.jetty.HttpAppTester;

import java.net.URI;

public class TesterRoute {
    private RouteMatcher routeMatcher;
    private HttpAppTester httpAppTester;

    public TesterRoute(RouteMatcher routeMatcher, HttpAppTester httpAppTester) {
        this.routeMatcher = routeMatcher;
        this.httpAppTester = httpAppTester;
    }

    public HttpAppTester getHttpAppTester() {
        return httpAppTester;
    }

    public boolean matches(URI requestUri) {
        return routeMatcher.routeMatches(requestUri);
    }
}
