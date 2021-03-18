package br.com.jc.bluefood.application.service;

public class PagamentoException extends Exception {
	private static final long serialVersionUID = 1L;

	public PagamentoException() {
		
	}

	public PagamentoException(String message) {
		super(message);
	}

	public PagamentoException(Throwable cause) {
		super(cause);
	}

	public PagamentoException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
