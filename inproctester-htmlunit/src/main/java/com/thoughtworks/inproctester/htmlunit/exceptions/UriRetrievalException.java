package com.thoughtworks.inproctester.htmlunit.exceptions;

public class UriRetrievalException extends RuntimeException {
	
	private static final long serialVersionUID = 6880840561805978802L;

	public UriRetrievalException(Exception e) {
		super(e);
	}
}
