package com.thoughtworks.inproctester.jetty;

import com.thoughtworks.inproctester.core.InProcResponse;
import org.eclipse.jetty.testing.HttpTester;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class JettyInProcResponse implements InProcResponse {
    private HttpTester httpTester;

    public JettyInProcResponse(HttpTester httpTester) {
        this.httpTester = httpTester;
    }

    @Override
    public int getStatus() {
        return httpTester.getStatus();
    }

    @Override
    public String getContent() {
        return httpTester.getContent();
    }

    @Override
    public Set<String> getHeaderNames() {
        Set<String> headerNames = new HashSet<String>();
        Enumeration enumeration = httpTester.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            headerNames.add(enumeration.nextElement().toString());
        }
        return headerNames;
    }

    @Override
    public String getHeader(String headerName) {
        return httpTester.getHeader(headerName);
    }

    @Override
    public String getCharacterEncoding() {
        return httpTester.getCharacterEncoding();
    }

    @Override
    public String getReason() {
        return httpTester.getReason();
    }
}
