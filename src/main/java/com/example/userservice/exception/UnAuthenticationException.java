package com.example.userservice.exception;

public class UnAuthenticationException extends RuntimeException {

	public UnAuthenticationException() {
		super("아이디 비밀번호 확인 하세요.");
		
	}

	public UnAuthenticationException(String message) {
		super(message);
	}
	

}
