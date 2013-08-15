package com.msi.tough.core;

public class BaseException extends RuntimeException {

	private static final long serialVersionUID = -2809614811252465025L;
	private String code;

	public BaseException(String message) {
		super(message);
	}

	public BaseException(String message, String code) {
		super(message);
		this.code = code;
	}

	public BaseException(Throwable e) {
		super(e);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
