package com.thoughtworks.inproctester.jetty;

import com.thoughtworks.inproctester.core.InProcResponse;
import org.eclipse.jetty.http.HttpTester;

import java.util.Collection;

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
    public Collection<String> getHeaderNames() {
        return testerResponse.getFieldNamesCollection();
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
