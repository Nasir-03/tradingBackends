package com.trade.repositoy;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.trade.modal.User;

public interface UserRepository extends MongoRepository<User, Long>{

	Optional<User> findByEmail(String email);
}
