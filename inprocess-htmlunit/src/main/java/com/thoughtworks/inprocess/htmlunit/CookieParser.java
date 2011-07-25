package com.thoughtworks.inprocess.htmlunit;

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
