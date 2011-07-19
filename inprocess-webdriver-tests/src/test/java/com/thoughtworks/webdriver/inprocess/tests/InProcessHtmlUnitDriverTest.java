package com.thoughtworks.webdriver.inprocess.tests;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.thoughtworks.webdriver.inprocess.testapp.TestServlet;
import com.thoughworks.webdriver.inprocess.HttpAppTester;
import com.thoughworks.webdriver.inprocess.InProcessHtmlUnitDriver;
import com.thoughworks.webdriver.inprocess.InProcessWebConnection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;



import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.junit.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class InProcessHtmlUnitDriverTest {


    private static HttpAppTester httpAppTester;

    @BeforeClass
    public static void setUp() throws Exception {
//        httpAppTester = new HttpAppTester("./src/main/webapp", "/");
        httpAppTester = new HttpAppTester();
        httpAppTester.addServlet(TestServlet.class, "/test/*");
        httpAppTester.start();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        httpAppTester.stop();
    }

    @Test
    public void shouldTestWithWebClient() throws Exception {
        WebClient webClient = new WebClient();
        webClient.setWebConnection(new InProcessWebConnection(webClient, httpAppTester));

        final HtmlPage page = webClient.getPage("http://localhost/test/a");
        System.out.println("page = " + page.getWebResponse().getContentAsString());

    }

    @Test
    public void shouldTestWithWebDriverClient() throws Exception {

        HtmlUnitDriver htmlUnitDriver = new InProcessHtmlUnitDriver(httpAppTester);

        htmlUnitDriver.get("http://localhost/test/a");
        System.out.println("htmlUnitDriver.getTitle() = " + htmlUnitDriver.getTitle());

        System.out.println("body.getText() = " + htmlUnitDriver.getPageSource());

    }

}
