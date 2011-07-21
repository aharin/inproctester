package com.thoughtworks.webdriver.inprocess;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.testing.HttpTester;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

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

        String rawResponse;
        try {
            rawResponse = appTester.getResponses(rawRequests);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        WebResponse webResponse = new WebResponse(parseRawResponse(rawResponse), request.getUrl(), request.getHttpMethod(), 0);
        processCookies(webResponse);
        return webResponse;
    }

    private void processCookies(WebResponse webResponse) {
        List<NameValuePair> responseHeaders = webResponse.getResponseHeaders();
        for (NameValuePair responseHeader : responseHeaders) {
            if ("Set-Cookie".equalsIgnoreCase(responseHeader.getName())) {
                Cookie cookie = cookieParser.parseCookie(webResponse.getWebRequest().getUrl().getHost(), responseHeader.getValue());
                Date now = new Date();
                CookieManager cookieManager = webClient.getCookieManager();
                if (cookie.getExpires() != null && now.after(cookie.getExpires())) {
                    Cookie existingCookie = cookieManager.getCookie(cookie.getName());
                    if (existingCookie != null){
                        cookieManager.removeCookie(existingCookie);
                    }
                } else {
                    cookieManager.addCookie(cookie);
                }
            }
        }
    }

    private String generateRawRequest(WebRequest request) throws IOException {
        HttpTester httpTester = new HttpTester();
        httpTester.setMethod(request.getHttpMethod().name());
        httpTester.setURI(getRequestPath(request.getUrl()));
        httpTester.addHeader("Host", getRequestHost(request.getUrl()));
        addCookies(httpTester);
        if (request.getHttpMethod() == HttpMethod.POST) {
            if (request.getEncodingType() == FormEncodingType.URL_ENCODED) {
                httpTester.setHeader("Content-Type", "application/x-www-form-urlencoded");
                httpTester.setContent(generateFormDataAsString(request));
            }
        }
        return httpTester.generate();
    }

    private void addCookies(HttpTester httpTester) {
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

    private String getRequestPath(URL absoluteUrl) {
        try {
            URI uri = absoluteUrl.toURI();
            String path = uri.getPath();
            String query = uri.getQuery();
            String fragment = uri.getFragment();

            StringBuilder sb = new StringBuilder(path);
            if (StringUtils.isNotEmpty(query)) {
                sb.append("?").append(query);
            }
            if (StringUtils.isNotEmpty(fragment)) {
                sb.append("#").append(fragment);
            }
            return sb.toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRequestHost(URL absoluteUrl) {
        try {
            URI uri = absoluteUrl.toURI();
            String host = uri.getHost();
            int port = uri.getPort();
            StringBuilder sb = new StringBuilder(host);
            if (port != -1) {
                sb.append(":").append(port);
            }
            return sb.toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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
        String content = httpTester.getContent();
        if (content == null) content = "";
        return new WebResponseData(content.getBytes(httpTester.getCharacterEncoding()), httpTester.getStatus(), httpTester.getReason(), headers);
    }

    private String generateFormDataAsString(WebRequest request) {
        List<NameValuePair> requestParameters = request.getRequestParameters();

        StringBuilder s = new StringBuilder();

        for (NameValuePair requestParameter : requestParameters) {

            String key = requestParameter.getName();

            if (s.length() > 0) {
                s.append('&');
            }
            s.append(urlEncode(key)).append("=");
            String value = requestParameter.getValue();

            if (StringUtils.isNotEmpty(value)) {
                s.append(urlEncode(value));
            }

        }

        return s.toString();
    }

    private String urlEncode(String key) {
        try {
            return URLEncoder.encode(key, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

