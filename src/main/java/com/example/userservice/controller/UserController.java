package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.exception.UnAuthenticationException;
import com.example.userservice.service.JWTService;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

    private final Environment env;
    private final UserService service;
    private final JWTService jwtService;

    @Value("${eureka.instance.instance-id}")  //config property use
    String instance;

    @GetMapping("/healthy-check")
    public String status() {
        return "User Service Connected : port= " + env.getProperty("local.server.port")  // property에 포트는 0으로 랜덤 포트이므로 local
                + ", " + instance + " success!!<br/>"
                + "token secret : " + env.getProperty("token.secret")
                + "<br/>token expiration time : " + env.getProperty("token.expiration_time");
    }

    /**
     * 사용자 등록
     */
    @PostMapping("/auth/signup")
    public ResponseEntity<ResponseUser> signup(@RequestBody RequestUser user) {
        //modelmapper setting
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        //RequestUser -> UserDto
        UserDto userDto = mapper.map(user, UserDto.class);
        UserDto resultDto = service.regist(userDto);

        //UserDto -> ResponseUser
        ResponseUser responseUser = mapper.map(resultDto, ResponseUser.class);
        //응답
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    /**
     * 사용자 목록 조회
     */
    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        //modelmapper setting
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        List<ResponseUser> users = new ArrayList<ResponseUser>();
        List<UserDto> list = service.getUsers();
        //UserDto -> ResponseUser
        list.forEach(user ->
                users.add(mapper.map(user, ResponseUser.class)));

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    /**
     * 사용자 정보 조회
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable String userId) {
        //model mapper setting
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = service.getUser(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.map(userDto, ResponseUser.class));

    }

    /**
     * 사용자 정보 수정
     */
    @PutMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> modify(@PathVariable String userId,
                                               @RequestBody RequestUser user) {
        //model mapper setting
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = service.getUser(userId);
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setPassword(user.getPassword());

        userDto = service.modifyUser(userDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(mapper.map(userDto, ResponseUser.class));
    }

    /**
     * 사용자 정보 삭제
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> remove(@PathVariable String userId) {
        String result = service.removeUser(userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }



    /**
     * singin 로그인 체크, JWT발행
     */
    @PostMapping("/auth/signin")
    public ResponseEntity<ResponseUser> signin(@RequestBody RequestUser user){
//			,HttpServletResponse response){
        //model mapper setting
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        //RequestUser -> UserDto service loginCheck호출
        UserDto userDto=null;
        String accessToken=null;
        try {
            userDto = service.loginCheck(mapper.map(user, UserDto.class));
            //valid  -> JWT발행 -> header에 setting access-token
            //토큰 : header, payload, signature (Environment: expiration_time, secret)
            if(userDto != null) {
                accessToken = jwtService.createToken(
                        Long.parseLong(env.getProperty("token.expiration_time")),
                        env.getProperty("token.secret"),
                        userDto.getUserId()
                );
//				accessToken = Jwts.builder()
//				              .setSubject(userDto.getUserId())
//				              .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
//				              .setExpiration(
//				            		  new Date(
//				            				  System.currentTimeMillis()
//				            				  	+ Long.parseLong(env.getProperty("token.expiration_time"))
//				            				  )
//				            		  )
//					          .compact();
//				response.setHeader("access-token", accessToken);
//				response.setHeader("userId", userDto.getUserId());
            }
            //UserDto->ResponseUser 변환 리턴
        }catch(UnAuthenticationException siginError) {
            exceptionHandler(siginError);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .header("access-token", accessToken)
                .header("userId", userDto.getUserId())
                .body(mapper.map(userDto, ResponseUser.class));
    }

    @GetMapping("/auth/signout")
    public ResponseEntity<String> signout(HttpServletRequest request){
        if(request.getHeader("userId")!=null
                && request.getHeader("access-token") != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("access-token", "")
                    .header("userId", "")
                    .body("success");
        }else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                    .header("access-token", "")
                    .header("userId", "")
                    .body("fail");
        }
    }

    private ResponseEntity<String> exceptionHandler(Exception error) {
        // exception type별로 처리 - ExceptionHandler Class 이용
//		if( error instanceof UnAuthenticationException)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error.getMessage());
    }
}
