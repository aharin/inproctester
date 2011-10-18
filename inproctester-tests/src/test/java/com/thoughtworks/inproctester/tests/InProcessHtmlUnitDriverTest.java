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
package com.thoughtworks.inproctester.tests;

import com.thoughtworks.inproctester.jetty.HttpAppTester;
import com.thoughtworks.inproctester.testapp.TestServlet;
import com.thoughtworks.inproctester.webdriver.InProcessHtmlUnitDriver;
import freemarker.ext.servlet.FreemarkerServlet;
import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;

public class InProcessHtmlUnitDriverTest {


    private static HttpAppTester httpAppTester;

    @BeforeClass
    public static void setUp() {
        httpAppTester = new HttpAppTester("/");
        httpAppTester.setResourceBase("./src/main/webapp");

        httpAppTester.addServlet(FreemarkerServlet.class, "*.ftl", new HashMap<String, String>() {
            {
                put("TemplatePath", "/WEB-INF/ftl/");
                put("template_exception_handler", "rethrow");

            }
        });
        httpAppTester.addServlet(TestServlet.class, "/contacts/*");

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
    public void shouldSupportUtfEncodedData() {

        WebDriver htmlUnitDriver = new InProcessHtmlUnitDriver(httpAppTester);

        htmlUnitDriver.get("http://localhost/contacts/add");

        htmlUnitDriver.findElement(By.name("contactName")).sendKeys("Имя Контакта");
        htmlUnitDriver.findElement(By.tagName("form")).submit();

        assertThat(htmlUnitDriver.findElement(By.name("contactName")).getAttribute("value"), is("Имя Контакта"));

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
