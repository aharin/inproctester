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
package com.thoughtworks.inproctester.htmlunit;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.thoughtworks.inproctester.core.InProcConnection;
import com.thoughtworks.inproctester.core.InProcRequest;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jetty.testing.HttpTester;

import java.io.IOException;
import java.util.*;

import static com.thoughtworks.inproctester.htmlunit.HttpTesterAdaptor.adaptRequest;
import static com.thoughtworks.inproctester.htmlunit.HttpTesterAdaptor.adaptResponse;
import static com.thoughtworks.inproctester.jetty.HttpAppTesterExtensions.processRequest;

public class InProcessWebConnection implements WebConnection {
    private CookieManager cookieManager;
    private InProcConnection inProcConnection;
    private CookieParser cookieParser = new CookieParser();

    public InProcessWebConnection(InProcConnection inProcConnection, CookieManager cookieManager) {
        this.inProcConnection = inProcConnection;
        this.cookieManager = cookieManager;
    }

    @Override
    public WebResponse getResponse(WebRequest webRequest) throws IOException {
        return new WebResponse(adaptResponse(processTesterRequest(adaptRequest(webRequest))), webRequest, 0);
    }

    private HttpTester processTesterRequest(InProcRequest inProcRequest) throws IOException {
        addCookiesToRequest(inProcRequest);
        HttpTester testerResponse = processRequest(inProcConnection, inProcRequest);
        storeCookiesFromResponse(inProcRequest, testerResponse);
        return testerResponse;
    }

    private void storeCookiesFromResponse(InProcRequest testerRequest, HttpTester testerResponse) {
        String requestHostName = testerRequest.getHeader("Host").split(":", 1)[0];
        Enumeration headerNames = testerResponse.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement().toString();
            if ("Set-Cookie".equalsIgnoreCase(headerName)) {
                Enumeration headerValues = testerResponse.getHeaderValues(headerName);
                while (headerValues.hasMoreElements()) {
                    storeCookie(requestHostName, headerValues.nextElement().toString());
                }
            }
        }
    }

    private void storeCookie(String hostName, String rawCookie) {
        Cookie cookie = cookieParser.parseCookie(hostName, rawCookie);
        Date now = new Date();
        if (cookie.getExpires() != null && now.after(cookie.getExpires())) {
            removeCookie(this.cookieManager, cookie.getName());
        } else {
            this.cookieManager.addCookie(cookie);
        }
    }

    private void removeCookie(CookieManager cookieManager, String cookieName) {
        Cookie existingCookie = cookieManager.getCookie(cookieName);
        if (existingCookie != null) {
            cookieManager.removeCookie(existingCookie);
        }
    }

    private void addCookiesToRequest(InProcRequest httpTester) {
        Set<Cookie> cookies = cookieManager.getCookies();
        if (!cookies.isEmpty()) {
            List<String> cookieStrings = new ArrayList<String>();
            for (Cookie cookie : cookies) {
                cookieStrings.add(cookie.getName() + "=" + cookie.getValue());
            }
            String cookieHeaderValue = StringUtils.join(cookieStrings, "; ");
            httpTester.addHeader("Cookie", cookieHeaderValue);
        }
    }

}

