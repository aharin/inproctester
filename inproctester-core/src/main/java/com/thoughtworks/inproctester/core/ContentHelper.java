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
package com.thoughtworks.inproctester.core;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class ContentHelper {
    public static byte[] getBytes(InProcResponse inProcResponse) {
        String content = inProcResponse.getContent();
        if (content == null) content = "";
        try {
            return content.getBytes(getCharacterEncoding(inProcResponse));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCharacterEncoding(InProcResponse inProcResponse) {
        String charset = "";

        String contentType = inProcResponse.getHeader("Content-Type");
        if (contentType != null) {
            String[] values = contentType.split(";"); //The values.length must be equal to 2...

            for (String value : values) {
                value = value.trim();

                if (value.toLowerCase().startsWith("charset=")) {
                    charset = value.substring("charset=".length());
                }
            }
        }

        if ("".equals(charset)) {
            charset = StandardCharsets.ISO_8859_1.name();
        }

        return charset;
    }
}
