package com.example.userservice.exception;

public class UnAuthorizationException extends RuntimeException {

	public UnAuthorizationException() {
		super("접근 권한이 없습니다.");
		
	}

	public UnAuthorizationException(String message) {
		super(message);
	}
	

}
