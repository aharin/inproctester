package com.thoughtworks.inproctester.core.exceptions;

public class UrlEncodingException extends RuntimeException {
	
	private static final long serialVersionUID = -784039882607132947L;

	public UrlEncodingException(Exception e) {
		super(e);
	}
}
