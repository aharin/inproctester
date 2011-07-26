/*
 * (c) 2011 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the Apache License, Version 2.0
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 *
 */
package com.thoughtworks.inproctester.htmlunit;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class UrlEncodedContent {

    private List<NameValuePair> requestParameters;

    public UrlEncodedContent(List<NameValuePair> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public String generateFormDataAsString() {
        StringBuilder s = new StringBuilder();

        for (NameValuePair requestParameter : requestParameters) {

            String key = requestParameter.getName();

            if (s.length() > 0) {
                s.append('&');
            }
            s.append(UrlHelper.urlEncode(key)).append("=");
            String value = requestParameter.getValue();

            if (StringUtils.isNotEmpty(value)) {
                s.append(UrlHelper.urlEncode(value));
            }

        }

        return s.toString();
    }
}
