package com.example.userservice.service;

import com.example.userservice.dto.UserDto;

import java.util.List;

public interface UserService {
	UserDto regist(UserDto user);
	List<UserDto> getUsers();
	UserDto getUser(String userId);
	UserDto modifyUser(UserDto user);
	String removeUser(String userId);
	UserDto loginCheck(UserDto user);
}
