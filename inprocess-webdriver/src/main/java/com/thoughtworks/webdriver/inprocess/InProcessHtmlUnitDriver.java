package com.thoughtworks.webdriver.inprocess;

import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class InProcessHtmlUnitDriver extends HtmlUnitDriver {


    public InProcessHtmlUnitDriver(HttpAppTester httpAppTester) {
        getWebClient().setWebConnection(new InProcessWebConnection(getWebClient(), httpAppTester));
    }

}
