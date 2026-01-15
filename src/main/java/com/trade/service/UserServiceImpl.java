package com.trade.service;

import java.util.Optional;

import javax.management.RuntimeErrorException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.trade.DTO.RegisterRequest;
import com.trade.config.CustomUserDetails;
import com.trade.config.JwtConfig;
import com.trade.extra.LoginRequest;
import com.trade.extra.LoginResponse;
import com.trade.extra.SequenceGeneratorService;
import com.trade.modal.User;
import com.trade.repositoy.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final WatchListService watchListService;
    private SequenceGeneratorService sequenceGeneratorService;
    private WalletService walletService;

    public UserServiceImpl(PasswordEncoder passwordEncoder,
                           UserRepository userRepository,
                           JwtConfig jwtConfig,
                           WatchListService watchListService,
                           SequenceGeneratorService sequenceGeneratorService,
                           WalletService walletService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtConfig = jwtConfig;
        this.watchListService = watchListService;
        this.sequenceGeneratorService = sequenceGeneratorService;
        this.walletService = walletService;
    }

    @Override
    public User registerUser(RegisterRequest user) {
    	Optional<User> checkUser = userRepository.findByEmail(user.getEmail());
    	
    	if (checkUser.isPresent()) {
    	    throw new ResponseStatusException(
    	        HttpStatus.BAD_REQUEST,
    	        "User already register"
    	    );
    	}
    	
        User newUser = new User();
        newUser.setId(sequenceGeneratorService.generateSequence("user_sequence"));
        newUser.setEmail(user.getEmail());
        newUser.setFullName(user.getFullName());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRole(user.getRole());
        
        User savedUser = userRepository.save(newUser);
        
        // Add user watchlist
        watchListService.createWatchList(newUser);
        walletService.createWallet(savedUser);

        return savedUser;
    }
    


    @Override
    public LoginResponse login(LoginRequest request) throws Exception {
        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty()) {
        	throw new Exception("user not registered");
        }

        // validate password
        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        User u = user.get();
        // create UserDetails object
        CustomUserDetails userDetails = new CustomUserDetails(
                u.getFullName(),
                u.getEmail(),
                u.getPassword(),
                u.getRole()
        );

        // generate JWT token
        String token = jwtConfig.generateToken(userDetails);

        // return login response
        return new LoginResponse(token);
    }

	@Override
	public User getUser(String email) {
		Optional<User> user = userRepository.findByEmail(email);
		
		if(user.isEmpty()) {
			throw new RuntimeException("user not found");
		}
		return user.get();
	}

	@Override
	public User getUserByJwt(String jwt) {

	    if (jwt.startsWith("Bearer ")) {
	        jwt = jwt.substring(7);
	    }

	    String email = jwtConfig.getUserNameFromToken(jwt);
	    return getUser(email);
	}

    
}
