package com.thoughtworks.inproctester.jersey.exceptions;

public class WebServerEventException extends RuntimeException {
	
	private static final long serialVersionUID = -6999259613578549965L;

	public WebServerEventException(Exception e) {
		super(e);
	}
}
