package com.trade.repositoy;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.trade.modal.PaymentOrder;

public interface PaymentOrderRepository extends MongoRepository<PaymentOrder, Long>{

}
