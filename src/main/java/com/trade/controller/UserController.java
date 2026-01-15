package com.trade.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trade.DTO.RegisterRequest;
import com.trade.extra.LoginRequest;
import com.trade.extra.LoginResponse;
import com.trade.modal.User;
import com.trade.service.UserService;
import com.trade.service.WatchListService;

@RestController
@RequestMapping("/auth")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody RegisterRequest request) {
		User users = userService.registerUser(request);
		
		return new ResponseEntity<>(users,HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws Exception {
	    LoginResponse response = userService.login(request);
	    return ResponseEntity.ok(response);
	}

	@GetMapping("/getUser")
	public ResponseEntity<User> getUserByJwt(
			@RequestHeader("Authorization") String jwt
			) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		
		return new ResponseEntity<User>(user,HttpStatus.OK);
	}
	
}
