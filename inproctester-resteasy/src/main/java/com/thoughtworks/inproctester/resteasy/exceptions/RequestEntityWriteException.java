package com.thoughtworks.inproctester.resteasy.exceptions;

public class RequestEntityWriteException extends RuntimeException {

	private static final long serialVersionUID = -3024331508265244769L;
	
	public RequestEntityWriteException(Exception e) {
		super(e);
	}
	
	public RequestEntityWriteException(String message) {
		super(message);
	}

}
