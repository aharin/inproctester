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
package com.thoughtworks.inproctester.htmlunit;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.thoughtworks.inproctester.core.UrlHelper;
import org.apache.commons.lang3.StringUtils;

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
