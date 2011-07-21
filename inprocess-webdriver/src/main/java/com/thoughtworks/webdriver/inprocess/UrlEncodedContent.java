package com.thoughtworks.webdriver.inprocess;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class UrlEncodedContent {

    private List<NameValuePair> requestParameters;

    public UrlEncodedContent(List<NameValuePair> requestParameters) {
        this.requestParameters = requestParameters;
    }

    public String generateFormDataAsString() {
        StringBuilder s = new StringBuilder();

        for (NameValuePair requestParameter : requestParameters) {

            String key = requestParameter.getName();

            if (s.length() > 0) {
                s.append('&');
            }
            s.append(UrlHelper.urlEncode(key)).append("=");
            String value = requestParameter.getValue();

            if (StringUtils.isNotEmpty(value)) {
                s.append(UrlHelper.urlEncode(value));
            }

        }

        return s.toString();
    }
}
