package com.thoughtworks.inproctester.jersey;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.WebAppDescriptor;
import com.sun.jersey.test.framework.spi.container.TestContainer;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;
import com.thoughtworks.inproctester.jetty.HttpAppTester;

import javax.servlet.Servlet;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class InProcessTestContainerFactory implements TestContainerFactory {

    public Class<WebAppDescriptor> supports() {
        return WebAppDescriptor.class;
    }

    public TestContainer create(URI baseUri, AppDescriptor ad) {
        if (!(ad instanceof WebAppDescriptor))
            throw new IllegalArgumentException(
                    "The application descriptor must be an instance of WebAppDescriptor");

        return new InprocessWebTestContainer(baseUri, (WebAppDescriptor) ad);
    }

    private static class InprocessWebTestContainer implements TestContainer {

        private static final Logger LOGGER =
                Logger.getLogger(InprocessWebTestContainer.class.getName());

        final URI baseUri;

        final String contextPath;

        final String servletPath;

        final Class<? extends Servlet> servletClass;

        List<WebAppDescriptor.FilterDescriptor> filters = null;

        final List<Class<? extends EventListener>> eventListeners;

        final Map<String, String> initParams;

        final Map<String, String> contextParams;

        private HttpAppTester httpServer;
        private ClientConfig clientConfig;


        private InprocessWebTestContainer(URI baseUri, WebAppDescriptor ad) {
            this.baseUri = UriBuilder.fromUri(baseUri)
                    .path(ad.getContextPath())
                    .build();

            LOGGER.info("Creating Inprocess Web Container configured at the base URI " + this.baseUri);
            this.contextPath = getContextPath(ad);
            this.servletPath = getServletPath(ad);


            this.servletClass = ad.getServletClass();
            this.filters = ad.getFilters();
            this.initParams = ad.getInitParams();
            this.contextParams = ad.getContextParams();
            this.eventListeners = ad.getListeners();
            clientConfig = ad.getClientConfig();

            instantiateInprocessWebServer();

        }

        private String getContextPath(WebAppDescriptor ad) {
            String contextPath = ad.getContextPath() == null ? "/" : ad.getContextPath();
            if (!contextPath.startsWith("/")) {
                return ("/" + contextPath);
            } else {
                return contextPath;
            }

        }

        private String getServletPath(WebAppDescriptor ad) {
            String servletPath = ad.getServletPath() == null ? "/" : ad.getServletPath();
            if (!servletPath.startsWith("/")) {
                return ("/" + servletPath);
            } else {
                return servletPath;
            }

        }

        public Client getClient() {
            return new Client(new InPocessClientHandler(baseUri, httpServer), clientConfig);
        }

        public URI getBaseUri() {
            return baseUri;
        }

        public void start() {
            LOGGER.info("Starting the Inprocess Web Container...");
            httpServer.start();

        }

        public void stop() {
            LOGGER.info("Stopping the Inprocess Web Container...");
            httpServer.stop();
        }

        /**
         * Instantiates the Grizzly 2 Web Server
         */
        private void instantiateInprocessWebServer() {
            httpServer = new HttpAppTester(contextPath);

            if (servletClass != null) {
                httpServer.addServlet(servletClass, servletPath, initParams);
            }

            if (contextParams != null) {
                for (Map.Entry<String, String> parameterEntry : contextParams.entrySet()) {
                    httpServer.setInitParameter(parameterEntry.getKey(), parameterEntry.getValue());
                }
            }

            if (eventListeners != null) {
                for (Class<? extends EventListener> eventListener : eventListeners) {
                    try {
                        httpServer.addEventListener(eventListener.newInstance());
                    } catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}


