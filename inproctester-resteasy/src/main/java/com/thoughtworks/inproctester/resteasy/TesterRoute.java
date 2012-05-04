package com.thoughtworks.inproctester.resteasy;

import com.thoughtworks.inproctester.core.InProcConnection;

import java.net.URI;

public class TesterRoute {
    private RouteMatcher routeMatcher;
    private InProcConnection httpAppTester;

    public TesterRoute(RouteMatcher routeMatcher, InProcConnection httpAppTester) {
        this.routeMatcher = routeMatcher;
        this.httpAppTester = httpAppTester;
    }

    public InProcConnection getHttpAppTester() {
        return httpAppTester;
    }

    public boolean matches(URI requestUri) {
        return routeMatcher.routeMatches(requestUri);
    }
}
