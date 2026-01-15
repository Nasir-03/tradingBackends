package com.trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trade.modal.PaymentDetails;
import com.trade.modal.User;
import com.trade.service.PaymentDetailsService;
import com.trade.service.UserService;

@RestController
@RequestMapping("/api")
public class PaymentDetailsController {

	private PaymentDetailsService paymentDetailsService;
	private UserService userService;
	
	@Autowired
	public PaymentDetailsController(PaymentDetailsService paymentDetailsService, UserService userService) {
		this.paymentDetailsService = paymentDetailsService;
		this.userService = userService;
	}
	
	@PostMapping("/payment-details")
	public ResponseEntity<PaymentDetails> addPaymentDetails(
			@RequestBody PaymentDetails paymentDetails,
			@RequestHeader("Authorization")String jwt
			) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		
		PaymentDetails paymentDetails2 = paymentDetailsService.addPaymentDetails(
				paymentDetails.getAccountNumber(), 
				paymentDetails.getAccountHolderName(),
				paymentDetails.getIfsc(), 
				paymentDetails.getBankName(), 
				user);
		
		return new ResponseEntity<PaymentDetails>(paymentDetails2,HttpStatus.CREATED);
	}
	
	@GetMapping("/payment-details")
	public ResponseEntity<PaymentDetails> getUserPaymentdetails(
			 @RequestHeader("Authorization")String jwt
			) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		PaymentDetails paymentDetails = 
				paymentDetailsService.getUserPaymentDetails(user);
		
		return new ResponseEntity<PaymentDetails>(paymentDetails,HttpStatus.OK);
	}
}
