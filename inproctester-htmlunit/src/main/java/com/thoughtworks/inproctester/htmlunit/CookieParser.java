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
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CookieParser {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss Z", Locale.US);
    private static final Logger LOGGER = LogManager.getLogger(CookieParser.class.getName());

    public Cookie parseCookie(String hostName, String rawCookie) {
        String[] cookieParts = rawCookie.split(";");
        String valuePart = cookieParts[0];
        int idx = valuePart.indexOf('=');
        String cookieName = valuePart.substring(0, idx);
        String cookieValue =  valuePart.substring(idx + 1, valuePart.length());


        Date expiresDate = null;
        for (String cookiePart : Arrays.copyOfRange(cookieParts, 1, cookieParts.length)) {
            if (cookiePart.trim().toLowerCase().startsWith("expires=")) {
                String expiresDateString = cookiePart.split("=")[1];
                try {
                    expiresDate = dateFormat.parse(expiresDateString);
                } catch (ParseException ignored) {
                	if (LOGGER.isDebugEnabled()) {
                		LOGGER.debug("A problem occured while parsing some cookie: ", ignored);
                	} else {
                		LOGGER.error("A technical problem occured while parsing some cookies", ignored);
                	}
                }
            }
        }

        return new Cookie(hostName, cookieName, cookieValue, "/", expiresDate, false);
    }
}
