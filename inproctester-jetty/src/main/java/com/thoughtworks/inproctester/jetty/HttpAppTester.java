/*  Copyright 2011 ThoughtWorks Ltd
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.thoughtworks.inproctester.jetty;

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

    public void start() {
        try {
            server.start();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getResponses(String rawRequests) {
        try {
            return connector.getResponses(rawRequests);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
