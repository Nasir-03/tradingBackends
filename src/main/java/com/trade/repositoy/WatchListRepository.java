package com.trade.repositoy;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.trade.modal.WatchList;

@Repository
public interface WatchListRepository extends MongoRepository<WatchList, Long>{

	WatchList findByUserId(Long userId);
}
