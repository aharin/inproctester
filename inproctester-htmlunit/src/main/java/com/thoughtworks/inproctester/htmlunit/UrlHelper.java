/*
 * (c) 2011 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the Apache License, Version 2.0
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 *
 */
package com.thoughtworks.inproctester.htmlunit;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class UrlHelper {

    public static String getRequestPath(URL absoluteUrl) {
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

    public static String getRequestHost(URL absoluteUrl) {
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

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
