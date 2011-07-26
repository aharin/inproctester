/*
 * (c) 2011 ThoughtWorks Ltd
 * All rights reserved.
 *
 * The software in this package is published under the terms of the Apache License, Version 2.0
 * a copy of which has been included with this distribution in the LICENSE.txt file.
 *
 */
package com.thoughtworks.inproctester.webdriver;

import com.thoughtworks.inproctester.jetty.HttpAppTester;
import com.thoughtworks.inproctester.htmlunit.InProcessWebConnection;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class InProcessHtmlUnitDriver extends HtmlUnitDriver {


    public InProcessHtmlUnitDriver(HttpAppTester httpAppTester) {
        getWebClient().setWebConnection(new InProcessWebConnection(getWebClient(), httpAppTester));
    }

}
