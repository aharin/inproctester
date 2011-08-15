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

import com.gargoylesoftware.htmlunit.FormEncodingType;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.thoughtworks.inproctester.jetty.UrlHelper;
import org.eclipse.jetty.testing.HttpTester;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class HttpTesterAdaptor {
    static WebResponseData adaptResponse(HttpTester httpTester) throws IOException {
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

    static HttpTester adaptRequest(WebRequest request) {
        HttpTester httpTester = new HttpTester();
        httpTester.setMethod(request.getHttpMethod().name());
        httpTester.setURI(UrlHelper.getRequestPath(request.getUrl()));
        httpTester.addHeader("Host", UrlHelper.getRequestHost(request.getUrl()));

        if (request.getHttpMethod() == HttpMethod.POST) {
            httpTester.setHeader("Content-Type", request.getEncodingType().getName());
            if (request.getEncodingType() == FormEncodingType.URL_ENCODED) {
                httpTester.setContent(new UrlEncodedContent(request.getRequestParameters()).generateFormDataAsString());
            }
        }
        return httpTester;
    }
}
