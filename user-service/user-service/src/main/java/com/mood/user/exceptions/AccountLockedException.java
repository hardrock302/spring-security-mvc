package com.mood.user.exceptions;

public class AccountLockedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccountLockedException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountLockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public AccountLockedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public AccountLockedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AccountLockedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
