package com.thoughtworks.inproctester.jetty.testapp.validation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class ValidatingHttpRequest extends HttpServletRequestWrapper {

	public ValidatingHttpRequest(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public boolean authenticate(HttpServletResponse response) {
		// Customize this
		return true;
	}

}

