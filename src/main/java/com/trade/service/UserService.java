package com.trade.service;

import com.trade.DTO.RegisterRequest;
import com.trade.extra.LoginRequest;
import com.trade.extra.LoginResponse;
import com.trade.modal.User;

public interface UserService {

	public User registerUser(RegisterRequest user);
	
	public LoginResponse login(LoginRequest request)throws Exception;
	
	User getUser(String email);
	
	User getUserByJwt(String jwt)throws Exception;
}
