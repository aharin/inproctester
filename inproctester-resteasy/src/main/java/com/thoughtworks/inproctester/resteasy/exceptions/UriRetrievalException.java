package com.thoughtworks.inproctester.resteasy.exceptions;

public class UriRetrievalException extends RuntimeException {

	private static final long serialVersionUID = -506077171726232405L;
	
	public UriRetrievalException(Exception ex) {
		super(ex);
	}

}
