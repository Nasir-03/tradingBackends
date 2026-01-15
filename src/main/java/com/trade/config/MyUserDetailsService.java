//package com.trade.config;
//
//import java.util.ArrayList;
//import java.util.Optional;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import com.trade.modal.User;
//import com.trade.service.UserService;
//
//@Service
//public class MyUserDetailsService implements UserDetailsService{
//
//	private final UserService userService;
//
//	public MyUserDetailsService(UserService userService) {
//		this.userService = userService;
//	}
//
//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		Optional<User> user = userService.getUser(username);
//		
//		if (user.isEmpty()) {
//            throw new UsernameNotFoundException("User not found with email: " + username);
//        }
//		
//		return new CustomUserDetails(
//				user.get().getFullName(),
//				user.get().getEmail(),
//				user.get().getPassword(),
//				user.get().getRole()
//				);
//	}
//	
//	
//}









package com.trade.config;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.trade.modal.User;
import com.trade.service.UserService;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userService.getUser(username);

        if (user == null) {
            throw new RuntimeException("User not found with email: " + username);
        }

//        User u = user.get();
        return new CustomUserDetails(
                user.getFullName(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}

