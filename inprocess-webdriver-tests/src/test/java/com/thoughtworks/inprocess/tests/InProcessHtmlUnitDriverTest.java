package com.thoughtworks.inprocess.tests;

import com.thoughtworks.inprocess.jetty.HttpAppTester;
import com.thoughtworks.inprocess.webdriver.InProcessHtmlUnitDriver;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;

public class InProcessHtmlUnitDriverTest {


    private static HttpAppTester httpAppTester;

    @BeforeClass
    public static void setUp() {
        httpAppTester = new HttpAppTester("./src/main/webapp", "/");
        httpAppTester.start();
    }

    @AfterClass
    public static void tearDown() {
        httpAppTester.stop();
    }


    @Test
    public void shouldSupportGetAndPostRequests() {

        WebDriver htmlUnitDriver = new InProcessHtmlUnitDriver(httpAppTester);

        htmlUnitDriver.get("http://localhost/contacts/add");

        assertThat(htmlUnitDriver.getTitle(), is("Test Application"));
        assertThat(htmlUnitDriver.findElement(By.tagName("h3")).getText(), is("Contact Details"));
        WebElement contactNameElement = htmlUnitDriver.findElement(By.name("contactName"));

        assertThat(contactNameElement.getAttribute("value"), isEmptyString());
        contactNameElement.sendKeys("My Contact");

        htmlUnitDriver.findElement(By.tagName("form")).submit();

        assertThat(htmlUnitDriver.getCurrentUrl(), is("http://localhost/contacts/1"));
        assertThat(htmlUnitDriver.findElement(By.name("contactName")).getAttribute("value"), is("My Contact"));

    }

    @Test
    public void shouldSupportCookies() {
        WebDriver htmlUnitDriver = new InProcessHtmlUnitDriver(httpAppTester);
        htmlUnitDriver.manage().deleteAllCookies();

        htmlUnitDriver.get("http://localhost/contacts/add");
        htmlUnitDriver.findElement(By.name("contactName")).sendKeys("My Contact");
        htmlUnitDriver.findElement(By.tagName("form")).submit();

        assertThat(htmlUnitDriver.findElement(By.className("message")).getText(), is("Success"));

        htmlUnitDriver.get("http://localhost/contacts/1");

        Cookie flashMessageCookie = htmlUnitDriver.manage().getCookieNamed("FLASH_MESSAGE");
        assertThat(flashMessageCookie, is(nullValue()));


         assertThat(htmlUnitDriver.findElements(By.className("message")), is(Matchers.<WebElement>empty()));
    }

}
