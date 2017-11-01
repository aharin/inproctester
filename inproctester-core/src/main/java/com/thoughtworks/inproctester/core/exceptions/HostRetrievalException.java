package com.thoughtworks.inproctester.core.exceptions;

public class HostRetrievalException extends RuntimeException {
	
	private static final long serialVersionUID = -2798433739196715958L;

	public HostRetrievalException(Exception e) {
		super(e);
	}
}
