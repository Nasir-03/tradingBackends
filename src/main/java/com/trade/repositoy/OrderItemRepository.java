package com.trade.repositoy;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.trade.modal.OrderItem;

@Repository
public interface OrderItemRepository extends MongoRepository<OrderItem, Long>{

}
