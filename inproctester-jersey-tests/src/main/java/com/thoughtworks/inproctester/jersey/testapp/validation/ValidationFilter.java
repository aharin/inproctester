package com.thoughtworks.inproctester.jersey.testapp.validation;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class ValidationFilter implements javax.servlet.Filter {

	@Override
	public void destroy() {
		// Customise this
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		chain.doFilter(new ValidatingHttpRequest((HttpServletRequest) request), response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// Customize
	}
}
