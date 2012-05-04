/*  Copyright 2012 ThoughtWorks Ltd
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

import java.net.URI;
import java.util.Set;

public interface InProcRequest {
    String getHttpMethod();

    URI getUri();

    String getContent();

    Set<String> getHeaderNames();

    String getHeader(String headerName);

    void addHeader(String headerName, String header);
}
