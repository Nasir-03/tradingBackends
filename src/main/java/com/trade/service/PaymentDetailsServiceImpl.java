package com.trade.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trade.modal.PaymentDetails;
import com.trade.modal.User;
import com.trade.repositoy.PaymentDetailsRepository;

@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService{

	private PaymentDetailsRepository paymentDetailsRepository;
	
	@Autowired
	public PaymentDetailsServiceImpl(PaymentDetailsRepository paymentDetailsRepository) {
		this.paymentDetailsRepository = paymentDetailsRepository;
	}

	@Override
	public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifsc,
			String bankName, User user) {
		
		PaymentDetails paymentDetails = new PaymentDetails();
		
		paymentDetails.setAccountHolderName(accountHolderName);
		paymentDetails.setAccountNumber(accountNumber);
		paymentDetails.setIfsc(ifsc);
		paymentDetails.setBankName(bankName);
		paymentDetails.setUser(user);
		
		return paymentDetailsRepository.save(paymentDetails);
	}

	@Override
	public PaymentDetails getUserPaymentDetails(User user) {
		PaymentDetails paymentDetails = paymentDetailsRepository.findByUserId(user.getId());
	
	    if (paymentDetails == null) {
	    	throw new RuntimeException("payment details not found");
	    }
	    
	    return paymentDetails;
	}

}
