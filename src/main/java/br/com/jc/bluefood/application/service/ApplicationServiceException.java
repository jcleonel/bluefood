package br.com.jc.bluefood.application.service;

public class ApplicationServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ApplicationServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationServiceException(String message) {
		super(message);
	}

	public ApplicationServiceException(Throwable cause) {
		super(cause);
	}
	
	

}
