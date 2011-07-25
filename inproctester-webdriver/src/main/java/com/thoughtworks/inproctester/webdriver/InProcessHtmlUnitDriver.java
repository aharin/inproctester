package com.thoughtworks.inproctester.webdriver;

import com.thoughtworks.inproctester.jetty.HttpAppTester;
import com.thoughtworks.inproctester.htmlunit.InProcessWebConnection;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class InProcessHtmlUnitDriver extends HtmlUnitDriver {


    public InProcessHtmlUnitDriver(HttpAppTester httpAppTester) {
        getWebClient().setWebConnection(new InProcessWebConnection(getWebClient(), httpAppTester));
    }

}
