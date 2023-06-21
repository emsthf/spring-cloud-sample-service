package com.example.userservice.service;

public interface JWTService {
	String createToken(long expirationTime, String secret, String subject);
	String checkToken(String accessToken, String secret);
}
