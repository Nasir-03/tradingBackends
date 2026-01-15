package com.trade.repositoy;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.trade.modal.Wallet;

@Repository
public interface WalletRepository extends MongoRepository<Wallet, Long>{

	Wallet findByUserId(Long userId);
	
	Wallet findByUser_Id(Long id);

}
