package com.thoughtworks.inproctester.jetty.exceptions;

public class HttpAppStartException extends RuntimeException {

	private static final long serialVersionUID = 844677459825042151L;
	
	public HttpAppStartException(Exception e) {
		super(e);
	}

}
