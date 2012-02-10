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
package com.thoughtworks.inproctester.jetty;

import org.eclipse.jetty.testing.HttpTester;

import java.io.IOException;

public class InProcServerExtensions {
    public static HttpTester processRequest(InProcServer inProcServer, HttpTester testerRequest) throws IOException {
        String rawResponse = inProcServer.getResponses(testerRequest.generate());
        HttpTester testerResponse = new HttpTester();
        testerResponse.parse(rawResponse);
        return testerResponse;
    }
}
