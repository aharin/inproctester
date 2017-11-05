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
package com.thoughtworks.inproctester.testapp;


import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(TestServlet.class.getName());
	private static final String FORWARD_FAILURE_MESSAGE = "Unable to forward the request ";
	private static final String REDIRECT_FAILURE_MESSAGE = "Failed to redirect from the request ";
	
    public static final Contact contact = new Contact();
    public static final String FLASH_MESSAGE_COOKIE_NAME = "FLASH_MESSAGE";
    

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cookie flashMessageCookie = getCookie(req, FLASH_MESSAGE_COOKIE_NAME);
        if (flashMessageCookie != null) {
            req.setAttribute("message", flashMessageCookie.getValue());
            Cookie cookie = new Cookie(FLASH_MESSAGE_COOKIE_NAME, "");
            cookie.setMaxAge(0);
            resp.addCookie(cookie);
        }
        req.setAttribute("contact", contact);
        try {
        	getServletContext().getRequestDispatcher("/test.ftl").forward(req, resp);
        } catch (ServletException | IOException ex) {
        	LOGGER.log(Level.FINE, FORWARD_FAILURE_MESSAGE, ex);
        }
    }

    private Cookie getCookie(HttpServletRequest req, String cookieName) {

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        contact.setName(req.getParameter("contactName"));
        resp.addCookie(new Cookie(FLASH_MESSAGE_COOKIE_NAME, "Success"));
        try {
        	resp.sendRedirect(req.getContextPath() + "/contacts/1");
        } catch (IOException ex) {
        	LOGGER.log(Level.FINE, REDIRECT_FAILURE_MESSAGE, ex);
        }
    }
}

