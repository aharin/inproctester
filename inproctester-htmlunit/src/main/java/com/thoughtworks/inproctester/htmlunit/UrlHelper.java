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

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlHelper {

    private static URLCodec urlCodec = new URLCodec();

    public static String getRequestPath(URL absoluteUrl) {
        try {
            URI uri = absoluteUrl.toURI();
            return getRequestPath(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRequestPath(URI uri) {
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
    }

    public static String getRequestHost(URL absoluteUrl) {
        try {
            URI uri = absoluteUrl.toURI();
            return getRequestHost(uri);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getRequestHost(URI uri) {
        String host = uri.getHost();
        int port = uri.getPort();
        StringBuilder sb = new StringBuilder(host);
        if (port != -1) {
            sb.append(":").append(port);
        }
        return sb.toString();
    }

    public static String urlEncode(String value) {
        try {
            return urlCodec.encode(value, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
