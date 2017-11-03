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

import com.thoughtworks.inproctester.core.InProcConnection;
import com.thoughtworks.inproctester.core.InProcRequest;
import com.thoughtworks.inproctester.core.InProcResponse;
import com.thoughtworks.inproctester.jetty.exceptions.HttpAppStartException;
import com.thoughtworks.inproctester.jetty.exceptions.HttpAppStopException;

import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import java.util.EnumSet;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

public class HttpAppTester implements InProcConnection {

    private Server server;
    private LocalConnection localConnection;
    private ServletContextHandler context;

    public HttpAppTester(String contextPath) {
        this(createServletContextHandler(contextPath));
    }

    public HttpAppTester(String webApp, String contextPath) {
        this(createWebAppContext(webApp, contextPath));
    }

    private HttpAppTester(ServletContextHandler context) {
        server = new Server();
        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setServer(server);
        server.addBean(errorHandler);

        LocalConnector connector = createLocalConnector(server);
        server.addConnector(connector);

        this.context = context;
        server.setHandler(context);

        localConnection = new LocalConnection(connector);
    }

    private LocalConnector createLocalConnector(Server server) {
        HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory();
        httpConnectionFactory.getHttpConfiguration().setSendServerVersion(false);
        return new LocalConnector(server, null, null, null, -1, httpConnectionFactory);
    }


    private static ServletContextHandler createWebAppContext(String webApp, String contextPath) {
        WebAppContext context = new WebAppContext();
        context.setWar(webApp);
        context.setContextPath(contextPath);


        context.setParentLoaderPriority(true);
        context.setThrowUnavailableOnStartupException(true);

        return context;
    }

    private static ServletContextHandler createServletContextHandler(String contextPath) {
        return new ServletContextHandler(null, contextPath, ServletContextHandler.SESSIONS);
    }


    public void addServlet(Class<? extends Servlet> servletClass, String pathSpec, Map<String, String> initParameters) {
        ServletHolder servletHolder = context.addServlet(servletClass, pathSpec);
        servletHolder.setInitParameters(initParameters);
    }

    public void addServlet(Class<? extends Servlet> servletClass, String pathSpec) {
        context.addServlet(servletClass, pathSpec);
    }


    public void addFilter(Class<? extends Filter> filterClass, String pathSpec, Set<DispatcherType> dispatches, Map<String, String> initParameters) {
        FilterHolder servletHolder = context.addFilter(filterClass, pathSpec, EnumSet.copyOf( dispatches));
        servletHolder.setInitParameters(initParameters);
    }

    public void addFilter(Class<? extends Filter> filterClass, String pathSpec, Set<DispatcherType> dispatches) {
        context.addFilter(filterClass, pathSpec, EnumSet.copyOf(dispatches));
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
        try {
            server.start();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpAppStartException(e);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new HttpAppStopException(e);
        }
    }

    @Override
    public InProcResponse getResponses(InProcRequest inProcRequest) {
        return localConnection.getResponses(inProcRequest);
    }
}
