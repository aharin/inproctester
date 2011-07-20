package com.thoughtworks.webdriver.inprocess;

import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.Servlet;

public class HttpAppTester {

    private Server server;
    private LocalConnector connector;
    private ServletContextHandler context;

    public HttpAppTester(String webApp, String contextPath) {
        server = new Server();
        connector = new LocalConnector();
        context = createWebAppContext(webApp, contextPath);

        server.addBean(new ErrorHandler());
        server.setSendServerVersion(false);
        server.addConnector(connector);
        server.setHandler(context);

    }

    private WebAppContext createWebAppContext(String webApp, String contextPath) {
        WebAppContext context = new WebAppContext();
        context.setWar(webApp);
        context.setContextPath(contextPath);


        context.setParentLoaderPriority(true);
        context.setThrowUnavailableOnStartupException(true);

        return context;
    }

    public HttpAppTester() {
        server = new Server();
        connector = new LocalConnector();
        context = createServletContextHandler();

        server.addBean(new ErrorHandler());
        server.setSendServerVersion(false);
        server.addConnector(connector);
        server.setHandler(context);

    }

    private ServletContextHandler createServletContextHandler() {
        return new ServletContextHandler(ServletContextHandler.SESSIONS);
    }


    public void addServlet(Class<? extends Servlet> servletClass, String pathSpec) {
        context.addServlet(servletClass, pathSpec);
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    public String getResponses(String rawRequests) throws Exception {
        return connector.getResponses(rawRequests);
    }
}
