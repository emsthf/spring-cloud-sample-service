package com.example.userservice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JWTServiceImpl implements JWTService {

	/**
	 * 토큰 발행
	 * @param expirationTime:long 유효기간
	 * @param secret : String key정보
	 * @param subject:String userId
	 * @return accessToken : String 생성된 JWT 
	 */
	@Override
	public String createToken(long expirationTime, String secret, String subject) {
		return Jwts.builder() 
        .setSubject(subject)
        .signWith(SignatureAlgorithm.HS512, secret)  
        .setExpiration(
      		  new Date(
      				  System.currentTimeMillis()
      				  	+ expirationTime
      				  )
      		  )
        .compact(); 
	}

	/**
	 * token 검증
	 * @param : accessToken:String 검증할 토큰
	 * @param : secret:String key정보
	 * @return : subject:String 검증할 userId 
	 */
	@Override
	public String checkToken(String accessToken, String secret) {
		return Jwts.parser().setSigningKey(secret)
					 .parseClaimsJws(accessToken)
					 .getBody()
					 .getSubject();
	}
	
}
