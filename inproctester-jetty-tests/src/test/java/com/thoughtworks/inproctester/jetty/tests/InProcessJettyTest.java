package com.thoughtworks.inproctester.jetty.tests;

import com.thoughtworks.inproctester.core.InProcRequest;
import com.thoughtworks.inproctester.core.InProcResponse;
import com.thoughtworks.inproctester.jetty.HttpAppTester;
import com.thoughtworks.inproctester.jetty.testapp.TestServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class InProcessJettyTest {
    private static HttpAppTester httpAppTester;

    @BeforeClass
    public static void setUp() {
        httpAppTester = new HttpAppTester("/");
        httpAppTester.addServlet(TestServlet.class, "/*");
        httpAppTester.start();
    }

    @AfterClass
    public static void tearDown() {
        httpAppTester.stop();
    }

    @Test
    public void shouldAcceptBodyForPostRequest() throws Exception {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost");
        headers.put("Content-Type", "text/plain");

        URI uri = new URI("http://localhost/");
        InProcRequest request = new TestRequest("POST", uri, "POST body", headers);

        InProcResponse response = httpAppTester.getResponses(request);

        assertThat(response.getContent(), is("POST body"));
    }

    @Test
    public void shouldAcceptBodyForPutRequest() throws Exception {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost");
        headers.put("Content-Type", "text/plain");

        URI uri = new URI("http://localhost/");
        InProcRequest request = new TestRequest("PUT", uri, "PUT body", headers);

        InProcResponse response = httpAppTester.getResponses(request);

        assertThat(response.getContent(), is("PUT body"));
    }

    private static class TestRequest implements InProcRequest {
        private final String httpMethod;
        private final URI uri;
        private final String content;
        private final Map<String, String> headers;

        public TestRequest(
                String httpMethod,
                URI uri,
                String content,
                Map<String, String> headers) {
            this.httpMethod = httpMethod;
            this.uri = uri;
            this.content = content;
            this.headers = headers;
        }

        @Override public String getHttpMethod() {
            return httpMethod;
        }

        @Override public URI getUri() {
            return uri;
        }

        @Override public String getContent() {
            return content;
        }

        @Override public Set<String> getHeaderNames() {
            return headers.keySet();
        }

        @Override public String getHeader(String headerName) {
            return headers.get(headerName);
        }

        @Override public void addHeader(String headerName, String header) {
            headers.put(headerName, header);
        }
    }
}

