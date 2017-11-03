package com.thoughtworks.inproctester.jetty.exceptions;

public class HttpAppStopException extends RuntimeException {

	private static final long serialVersionUID = 3783656435168579286L;
	
	public HttpAppStopException(Exception e) {
		super(e);
	}

}
