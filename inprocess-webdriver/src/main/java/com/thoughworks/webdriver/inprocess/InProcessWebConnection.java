package com.thoughworks.webdriver.inprocess;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.eclipse.jetty.testing.HttpTester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class InProcessWebConnection implements WebConnection {
    private HttpAppTester appTester;

    public InProcessWebConnection(WebClient webClient, HttpAppTester appTester) {
        this.appTester = appTester;
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {

        String rawRequests = generateRawRequest(request);

        String rawResponse;
        try {
            rawResponse = appTester.getResponses(rawRequests);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        final WebResponseData responseData = parseRawResponse(rawResponse);
        return new WebResponse(responseData, request.getUrl(), request.getHttpMethod(), 0);
    }

    private String generateRawRequest(WebRequest request) throws IOException {
        HttpTester httpTester = new HttpTester();
        httpTester.setMethod(request.getHttpMethod().name());
        httpTester.setVersion("HTTP/1.0");
        httpTester.setURI(request.getUrl().toString());
        return httpTester.generate();
    }

    private WebResponseData parseRawResponse(String rawResponse) throws IOException {
        HttpTester httpTester = new HttpTester();
        httpTester.parse(rawResponse);
        final List<NameValuePair> headers = new ArrayList<NameValuePair>();
        Enumeration headerNames = httpTester.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toString();
            Enumeration headerValues = httpTester.getHeaderValues(headerName);
            while (headerValues.hasMoreElements()) {
                headers.add(new NameValuePair(headerName, headerValues.nextElement().toString()));
            }
        }
        return new WebResponseData(httpTester.getContent().getBytes(httpTester.getCharacterEncoding()), httpTester.getStatus(), httpTester.getReason(), headers);
    }
}

