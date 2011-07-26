/*
 * (c) 2011 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the Apache License, Version 2.0
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 *
 */
package com.thoughtworks.inproctester.htmlunit;

import com.gargoylesoftware.htmlunit.util.Cookie;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CookieParser {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zzz");


    public Cookie parseCookie(String host, String rawCookie) {
        String[] cookieParts = rawCookie.split(";");
        String valuePart = cookieParts[0];
        int idx = valuePart.indexOf("=");
        String cookieName = valuePart.substring(0, idx);
        String cookieValue =  valuePart.substring(idx + 1, valuePart.length());


        Date expiresDate = null;
        for (String cookiePart : Arrays.copyOfRange(cookieParts, 1, cookieParts.length)) {
            if (cookiePart.trim().toLowerCase().startsWith("expires=")) {
                String expiresDateString = cookiePart.split("=")[1];
                try {
                    expiresDate = dateFormat.parse(expiresDateString);
                } catch (ParseException ignored) {
                }
            }
        }

        return new Cookie(host, cookieName, cookieValue, "/", expiresDate, false);
    }
}
