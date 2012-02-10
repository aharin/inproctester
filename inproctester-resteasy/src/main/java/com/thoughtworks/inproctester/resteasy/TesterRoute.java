package com.thoughtworks.inproctester.resteasy;

import com.thoughtworks.inproctester.jetty.InProcServer;

import java.net.URI;

public class TesterRoute {
    private RouteMatcher routeMatcher;
    private InProcServer inProcServer;

    public TesterRoute(RouteMatcher routeMatcher, InProcServer inProcServer) {
        this.routeMatcher = routeMatcher;
        this.inProcServer = inProcServer;
    }

    public InProcServer getInProcServer() {
        return inProcServer;
    }

    public boolean matches(URI requestUri) {
        return routeMatcher.routeMatches(requestUri);
    }
}
