package com.thoughtworks.inproctester.resteasy.exceptions;

public class RequestHostException extends RuntimeException {

	private static final long serialVersionUID = -1365627301588525745L;
	
	public RequestHostException(Exception ex) {
		super(ex);
	}

}
