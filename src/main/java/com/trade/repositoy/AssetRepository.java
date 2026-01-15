package com.trade.repositoy;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.trade.modal.Asset;

@Repository
public interface AssetRepository extends MongoRepository<Asset, Long>{

	List<Asset> findByUserId(Long userId);
	
	Asset findByUserIdAndCoinId(Long userId,String coinId);

	 Asset findByUser_IdAndCoin_Id(Long userId, String coinId);
}
