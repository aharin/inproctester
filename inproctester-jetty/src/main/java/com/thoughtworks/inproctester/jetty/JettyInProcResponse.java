package com.thoughtworks.inproctester.jetty;

import com.thoughtworks.inproctester.core.InProcResponse;
import org.eclipse.jetty.http.HttpTester;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class JettyInProcResponse implements InProcResponse {
    private HttpTester.Response testerResponse;

    public JettyInProcResponse(HttpTester.Response testerResponse) {
        this.testerResponse = testerResponse;
    }

    @Override
    public int getStatus() {
        return testerResponse.getStatus();
    }

    @Override
    public byte[] getContentBytes() {
        return testerResponse.getContentBytes();
    }

    @Override
    public Set<String> getHeaderNames() {
        Set<String> headerNames = new HashSet<>();
        Enumeration enumeration = testerResponse.getFieldNames();
        while (enumeration.hasMoreElements()) {
            headerNames.add(enumeration.nextElement().toString());
        }
        return headerNames;
    }

    @Override
    public String getHeader(String headerName) {
        return testerResponse.get(headerName);
    }

    @Override
    public String getReason() {
        return testerResponse.getReason();
    }
}
