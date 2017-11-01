package com.thoughtworks.inproctester.core.exceptions;

public class ContentRetrievalException extends RuntimeException {
	
	private static final long serialVersionUID = 8785446378507287253L;

	public ContentRetrievalException(Exception e) {
		super(e);
	}
}
