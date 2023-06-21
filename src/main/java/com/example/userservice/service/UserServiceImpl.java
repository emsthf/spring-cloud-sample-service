package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserEntity;
import com.example.userservice.dto.UserDto;
import com.example.userservice.exception.UnAuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserDao dao;

	@Override
	public UserDto regist(UserDto user) {
		//modelmapper setting
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		//user : email, name, password
		//newUser : email, name, userId, encryptedPassword
		//userId 생성 UUID.randomUUID(), password 암호화
		//UserDto -> UserEntity로 변환 dao save호출
		//String hashed = BCrypt.hashpw(password, BCrypt.gensalt());
		//BCrypt.checkpw(candidate, hashed)
		user.setUserId(UUID.randomUUID().toString());
		user.setEncryptedPassword(BCrypt.hashpw(user.getPassword(),
								  				BCrypt.gensalt()));

		UserEntity userEntity = mapper.map(user, UserEntity.class);

		dao.save(userEntity);  //JPA로 구현
		log.debug(userEntity.toString());
		return user;
	}

	@Override
	public List<UserDto> getUsers() {
		//modelmapper setting
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		Iterable<UserEntity> userEntities = dao.findAll();
		//UserEntity - > UserDto
		List<UserDto> userDtos = new ArrayList<UserDto>();
		userEntities.forEach( user->
					userDtos.add(mapper.map(user, UserDto.class)) );
		return userDtos;
	}

	@Override
	public UserDto getUser(String userId) {
		//modelmapper setting
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UserEntity userEntity = dao.findByUserId(userId);
		return mapper.map(userEntity, UserDto.class);
	}

	@Override
	public UserDto modifyUser(UserDto user) {
		UserEntity userEntity = dao.findByUserId(user.getUserId());
		userEntity.setEmail(user.getEmail());
		userEntity.setEncryptedPassword(BCrypt.hashpw(user.getPassword(),
								  				BCrypt.gensalt()));
		userEntity.setName(user.getName());

		dao.save(userEntity);

		return user;
	}

	@Override
	public String removeUser(String userId) {
		dao.deleteById(dao.findByUserId(userId).getId());
//	dao.delete(dao.findByUserId(userId));
		return userId;
	}

	@Override
	public UserDto loginCheck(UserDto user) throws UnAuthenticationException{
		//modelmapper setting
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		//email, password - email에 해당되는 UserEntity 구하고
		//UserEntity encryptedPassword와 password 비교
		//같으면 UserEntity->UserDto 리턴  다르면 인증오류 발생
		UserEntity userEntity = dao.findByEmail(user.getEmail());
		if(userEntity != null
			&& BCrypt.checkpw(user.getPassword(),
					userEntity.getEncryptedPassword())) {
			return mapper.map(userEntity, UserDto.class);
		}

		throw new UnAuthenticationException();
	}

}
