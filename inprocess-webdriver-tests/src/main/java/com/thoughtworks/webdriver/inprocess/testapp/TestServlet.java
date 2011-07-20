package com.thoughtworks.webdriver.inprocess.testapp;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TestServlet extends HttpServlet {

    public static Contact contact = new Contact();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");


        req.setAttribute("contact", contact);
        getServletContext().getRequestDispatcher("/test.ftl").forward(req, resp);
    }
}

