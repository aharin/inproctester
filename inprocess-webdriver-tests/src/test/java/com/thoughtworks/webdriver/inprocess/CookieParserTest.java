package com.thoughtworks.webdriver.inprocess;

import com.gargoylesoftware.htmlunit.util.Cookie;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CookieParserTest {

    public Date getTestDate(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        Calendar testDate = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        testDate.set(year, month -1, dayOfMonth, hourOfDay, minute, second);
        testDate.set(Calendar.MILLISECOND, 0);
        return testDate.getTime();
    }

    @Test
    public void ShouldparseCookieWithExpiryButNoValueCorrectly() {
        Date testDate = getTestDate(2010, 8, 3, 9, 25, 3);
        final String rawCookie = "USER=; expires=Tue, 03-Aug-2010 09:25:03 GMT; path=/";

        CookieParser cookieParser = new CookieParser();
        Cookie cookie = cookieParser.parseCookie("localhost", rawCookie);

        assertThat(cookie.getName(), is("USER"));
        assertThat(cookie.getValue(), is(""));
        assertThat(cookie.getExpires(), is(testDate));
    }

    @Test
    public void ShouldparseCookieWithExpiryAndAValueCorrectly() {
        Date testDate = getTestDate(2010, 8, 3, 9, 25, 3);
        final String rawCookie = "USER=bob; expires=Tue, 03-Aug-2010 09:25:03 GMT; path=/";

        CookieParser cookieParser = new CookieParser();
        Cookie cookie = cookieParser.parseCookie("localhost", rawCookie);

        assertThat(cookie.getName(), is("USER"));
        assertThat(cookie.getValue(), is("bob"));
        assertThat(cookie.getExpires(), is(testDate));
    }

    @Test
    public void ShouldparseCookieWithoutExpiryAndNoValueCorrectly() {
        final String rawCookie = "USER=; path=/";

        CookieParser cookieParser = new CookieParser();
        Cookie cookie = cookieParser.parseCookie("localhost", rawCookie);

        assertThat(cookie.getName(), is("USER"));
        assertThat(cookie.getValue(), is(""));
        assertThat(cookie.getExpires(), is(nullValue()));
    }

    @Test
    public void ShouldparseCookieWithEqualsInTheValue() {
        final String rawCookie = "DATA=USER=bob&FullName=Bob The Builder; path=/";

        CookieParser cookieParser = new CookieParser();
        Cookie cookie = cookieParser.parseCookie("localhost", rawCookie);

        assertThat(cookie.getName(), is("DATA"));
        assertThat(cookie.getValue(), is("USER=bob&FullName=Bob The Builder"));
        assertThat(cookie.getExpires(), is(nullValue()));
    }
}
