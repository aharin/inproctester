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

import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;

public class InProcServerAdapter implements InProcServer {

  private LocalConnector connector;

  private Server server;

  public InProcServerAdapter(Server server) {
    this.server = server;
    this.connector = new LocalConnector();
    this.server.addConnector(connector);
  }

  public void start() {
    try {
      server.start();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void stop() {
    try {
      server.stop();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String getResponses(String rawRequests) {
    try {
      ByteArrayBuffer result = connector.getResponses(new ByteArrayBuffer(rawRequests, StringUtil.__UTF8), false);
      return result == null ? null : result.toString(StringUtil.__UTF8);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
