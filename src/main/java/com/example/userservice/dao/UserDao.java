package com.example.userservice.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDao 
				extends CrudRepository<UserEntity, Long>{
    UserEntity findByEmail(String email);
    List<UserEntity> findByName(String name);
    UserEntity findByUserId(String userId);
}
