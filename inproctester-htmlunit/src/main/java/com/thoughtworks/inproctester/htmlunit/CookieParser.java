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
