package com.example.userservice.vo;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 요청 데이터 (Cotext Object) :입력값 검증
 * @author spring restful
 *
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class RequestUser {
	@NotNull(message = "Email cannot null.")
	@Email
	private String email;
	private String name;
	@NotNull(message = "Password cannot null.")
	@Size(min = 8, message = "Password must be equals or grater than 8 characters")
	private String password;
	
	@Builder
	public RequestUser(String email, String name, String password) {
		this.email = email;
		this.name = name;
		this.password = password;
	}
	
}
