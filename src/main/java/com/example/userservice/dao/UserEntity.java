package com.example.userservice.dao;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;


@NoArgsConstructor
@Data
@Entity
@Table(name="users")
public class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String email;
	@Column(length = 50)
	private String name;
	@Column(name="user_id", nullable = false, unique = true)
	private String userId;
	@Column(name="encrypted_password", nullable = false, unique = true)
	private String encryptedPassword;
	@Column(name="create_at", nullable=false, updatable = false, insertable = false)
	@ColumnDefault(value="CURRENT_TIMESTAMP")
	private Date createAt;
	
	@Builder
	public UserEntity(String email, String name, String userId, String encryptedPassword) {
		this.email = email;
		this.name = name;
		this.userId = userId;
		this.encryptedPassword = encryptedPassword;
	}
	
}
