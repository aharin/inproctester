package com.thoughtworks.inproctester.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.thoughtworks.inproctester.jetty.HttpAppTester;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.testing.HttpTester;

import java.io.IOException;
import java.util.*;

public class InProcessWebConnection implements WebConnection {
    private WebClient webClient;
    private HttpAppTester appTester;
    private CookieParser cookieParser = new CookieParser();

    public InProcessWebConnection(WebClient webClient, HttpAppTester appTester) {
        this.webClient = webClient;
        this.appTester = appTester;
    }

    @Override
    public WebResponse getResponse(WebRequest request) throws IOException {

        String rawRequests = generateRawRequest(request);

        String rawResponse = appTester.getResponses(rawRequests);

        WebResponse webResponse = new WebResponse(parseRawResponse(rawResponse), request.getUrl(), request.getHttpMethod(), 0);
        storeCookies(webResponse);
        return webResponse;
    }

    private void storeCookies(WebResponse webResponse) {
        List<NameValuePair> responseHeaders = webResponse.getResponseHeaders();
        for (NameValuePair responseHeader : responseHeaders) {
            if ("Set-Cookie".equalsIgnoreCase(responseHeader.getName())) {
                Cookie cookie = cookieParser.parseCookie(webResponse.getWebRequest().getUrl().getHost(), responseHeader.getValue());
                Date now = new Date();
                CookieManager cookieManager = webClient.getCookieManager();
                if (cookie.getExpires() != null && now.after(cookie.getExpires())) {
                    removeCookie(cookieManager, cookie.getName());
                } else {
                    cookieManager.addCookie(cookie);
                }
            }
        }
    }

    private void removeCookie(CookieManager cookieManager, String cookieName) {
        Cookie existingCookie = cookieManager.getCookie(cookieName);
        if (existingCookie != null) {
            cookieManager.removeCookie(existingCookie);
        }
    }

    private void sendCookies(HttpTester httpTester) {
        Set<Cookie> cookies = webClient.getCookieManager().getCookies();
        if (!cookies.isEmpty()) {
            List<String> cookieStrings = new ArrayList<String>();
            for (Cookie cookie : cookies) {
                cookieStrings.add(cookie.getName() + "=" + cookie.getValue());
            }
            String cookieHeaderValue = StringUtils.join(cookieStrings, "; ");
            httpTester.addHeader("Cookie", cookieHeaderValue);
        }
    }

    private String generateRawRequest(WebRequest request) throws IOException {
        HttpTester httpTester = new HttpTester();
        httpTester.setMethod(request.getHttpMethod().name());
        httpTester.setURI(UrlHelper.getRequestPath(request.getUrl()));
        httpTester.addHeader("Host", UrlHelper.getRequestHost(request.getUrl()));
        sendCookies(httpTester);
        if (request.getHttpMethod() == HttpMethod.POST) {
            httpTester.setHeader("Content-Type", request.getEncodingType().getName());
            if (request.getEncodingType() == FormEncodingType.URL_ENCODED) {
                httpTester.setContent(new UrlEncodedContent(request.getRequestParameters()).generateFormDataAsString());
            }
        }
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
                String headerValue = headerValues.nextElement().toString();
                headers.add(new NameValuePair(headerName, headerValue));
            }
        }
        String content = httpTester.getContent();
        if (content == null) content = "";
        return new WebResponseData(content.getBytes(httpTester.getCharacterEncoding()), httpTester.getStatus(), httpTester.getReason(), headers);
    }

}

