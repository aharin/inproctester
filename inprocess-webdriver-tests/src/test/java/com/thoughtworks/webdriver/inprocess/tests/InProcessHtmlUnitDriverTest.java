package com.thoughtworks.webdriver.inprocess.tests;

import com.thoughtworks.webdriver.inprocess.HttpAppTester;
import com.thoughtworks.webdriver.inprocess.InProcessHtmlUnitDriver;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

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
    public void shouldTestGetRequest() throws Exception {

        HtmlUnitDriver htmlUnitDriver = new InProcessHtmlUnitDriver(httpAppTester);

        htmlUnitDriver.get("http://localhost/test/a");

        assertThat(htmlUnitDriver.getTitle(), is("Test Application"));
        assertThat(htmlUnitDriver.findElement(By.tagName("h3")).getText(), is("Contact Details"));
        assertThat(htmlUnitDriver.findElement(By.name("contactName")).getText(), isEmptyString());

    }

}
