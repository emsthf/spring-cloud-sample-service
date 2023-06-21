package com.example.userservice.vo;

import lombok.*;

/**
 * 응답 데이터 (Context Object)
 * @author spring restful
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ResponseUser {
	
	private String email;
	private String name;
	private String userId;

	@Builder
	public ResponseUser(String email, String name, String userId) {
		super();
		this.email = email;
		this.name = name;
		this.userId = userId;
	}
	
	

}
