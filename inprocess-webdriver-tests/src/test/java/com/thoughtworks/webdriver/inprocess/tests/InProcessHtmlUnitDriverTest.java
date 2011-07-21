package com.thoughtworks.webdriver.inprocess.tests;

import com.thoughtworks.webdriver.inprocess.HttpAppTester;
import com.thoughtworks.webdriver.inprocess.InProcessHtmlUnitDriver;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;

public class InProcessHtmlUnitDriverTest {


    private static HttpAppTester httpAppTester;

    @BeforeClass
    public static void setUp() throws Exception {
        httpAppTester = new HttpAppTester("./src/main/webapp", "/");
        httpAppTester.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        httpAppTester.stop();
    }


    @Test
    public void shouldSupportGetAndPostRequests() throws Exception {

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
    public void shouldSupportCookies() throws Exception {
        WebDriver htmlUnitDriver = new InProcessHtmlUnitDriver(httpAppTester);
        htmlUnitDriver.manage().deleteAllCookies();

        htmlUnitDriver.get("http://localhost/contacts/add");
        htmlUnitDriver.findElement(By.name("contactName")).sendKeys("My Contact");
        htmlUnitDriver.findElement(By.tagName("form")).submit();

        Cookie flashMessageCookie = htmlUnitDriver.manage().getCookieNamed("FLASH_MESSAGE");
        assertThat(flashMessageCookie.getValue(), is("Success"));

         assertThat(htmlUnitDriver.findElement(By.className("message")).getText(), is("Success"));

    }

}
