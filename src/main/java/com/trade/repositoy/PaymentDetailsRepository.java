package com.trade.repositoy;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.trade.modal.PaymentDetails;

public interface PaymentDetailsRepository extends MongoRepository<PaymentDetails, Long>{

	PaymentDetails findByUserId(Long userId);
}
