package com.trade.repositoy;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.trade.modal.Order;

public interface OrderRepository extends MongoRepository<Order, Long>{

	List<Order> findByUserId(Long userId);
	
	List<Order> findByUser_Id(Long userId);
}
