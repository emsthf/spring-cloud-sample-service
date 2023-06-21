package com.example.userservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Controller에서 Request data를 Service 호출 시 전달되는  Transfer Object
 * Service에서 Entity data Controller에 응답시 전달되는 Transfer Object
 * @author springrestful
 *
 */
@NoArgsConstructor
@Getter
@Setter
public class UserDto implements java.io.Serializable {
	private String email;
	private String name;
	private String userId;
	private String password;
	private String encryptedPassword;
	
	@Builder
	public UserDto(String email, String name, String userId, String password, String encryptedPassword) {
		super();
		this.email = email;
		this.name = name;
		this.userId = userId;
		this.password = password;
		this.encryptedPassword = encryptedPassword;
	}
}
