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

import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.EventListener;
import java.util.Map;

public class HttpAppTester implements InProcServer {

    private InProcServer inProcServer;

    private ServletContextHandler context;

    public HttpAppTester(String webApp, String contextPath) {
        context = createWebAppContext(webApp, contextPath);

        Server server = new Server();
        server.addBean(new ErrorHandler());
        server.setSendServerVersion(false);
        server.setHandler(context);
        inProcServer = new InProcServerAdapter(server);
    }

    private WebAppContext createWebAppContext(String webApp, String contextPath) {
        WebAppContext context = new WebAppContext();
        context.setWar(webApp);
        context.setContextPath(contextPath);


        context.setParentLoaderPriority(true);
        context.setThrowUnavailableOnStartupException(true);

        return context;
    }

    public HttpAppTester(String contextPath) {
        context = createServletContextHandler(contextPath);

        Server server = new Server();
        server.addBean(new ErrorHandler());
        server.setSendServerVersion(false);
        server.setHandler(context);
        inProcServer = new InProcServerAdapter(server);
    }

    private ServletContextHandler createServletContextHandler(String contextPath) {
        return new ServletContextHandler(null, contextPath, ServletContextHandler.SESSIONS);
    }


    public void addServlet(Class<? extends Servlet> servletClass, String pathSpec, Map<String, String> initParameters) {
        ServletHolder servletHolder = context.addServlet(servletClass, pathSpec);
        servletHolder.setInitParameters(initParameters);
    }

    public void addServlet(Class<? extends Servlet> servletClass, String pathSpec) {
        context.addServlet(servletClass, pathSpec);
    }


    public void addFilter(Class<? extends Filter> filterClass, String pathSpec, int dispatches, Map<String, String> initParameters) {
        FilterHolder servletHolder = context.addFilter(filterClass, pathSpec, dispatches);
        servletHolder.setInitParameters(initParameters);
    }

    public void addFilter(Class<? extends Filter> filterClass, String pathSpec, int dispatches) {
        context.addFilter(filterClass, pathSpec, dispatches);
    }

    public void addEventListener(EventListener listener) {
        context.addEventListener(listener);
    }

    public void setResourceBase(String resourceBase) {
        context.setResourceBase(resourceBase);
    }

    public void setInitParameter(String name, String value) {
        context.setInitParameter(name, value);
    }

    public void start() {
        inProcServer.start();
    }

    public void stop() {
        inProcServer.stop();
    }

    public String getResponses(String rawRequests) {
        return inProcServer.getResponses(rawRequests);
    }
}
