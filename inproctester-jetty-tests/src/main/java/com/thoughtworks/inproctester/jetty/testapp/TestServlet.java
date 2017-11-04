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
package com.thoughtworks.inproctester.jetty.testapp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestServlet extends HttpServlet {
	
	private static final long serialVersionUID = 8775172147016982644L;
	private static final Logger LOGGER = Logger.getLogger(TestServlet.class.getName());
	
	private static final String REQUEST_RESPONSE_FAILURE_MSG_BASE = "The request / response interaction generated an exception";
	
    @Override protected void doPut(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        try {
        	response.getWriter().append(request.getReader().readLine());
        } catch (IOException ex) {
        	LOGGER.log(Level.FINE, REQUEST_RESPONSE_FAILURE_MSG_BASE, ex);
        } 
    }

    @Override protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        try {
        	response.getWriter().append(request.getReader().readLine());
        } catch (IOException ex) {
        	LOGGER.log(Level.FINE, REQUEST_RESPONSE_FAILURE_MSG_BASE, ex);
        }
        
    }
}
