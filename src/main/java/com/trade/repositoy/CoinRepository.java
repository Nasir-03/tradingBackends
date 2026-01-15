package com.trade.repositoy;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.trade.modal.Bitcoin;

@Repository
public interface CoinRepository extends MongoRepository<Bitcoin, String>{

	Optional<Bitcoin> findBySymbolIgnoreCase(String symbol);
}
