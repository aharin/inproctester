package com.thoughtworks.inproctester.jetty.exceptions;

public class LocalConnectionResponseException extends RuntimeException {

	private static final long serialVersionUID = 4113707830431924809L;
	
	public LocalConnectionResponseException(Exception e) {
		super(e);
	}

}
