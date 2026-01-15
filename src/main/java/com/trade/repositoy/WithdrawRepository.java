package com.trade.repositoy;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.trade.modal.Withdrawl;

@Repository
public interface WithdrawRepository extends MongoRepository<Withdrawl, Long>{

	List<Withdrawl> findByUserId(Long userId);
}
