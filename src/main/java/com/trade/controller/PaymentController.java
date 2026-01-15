package com.trade.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trade.domain.PaymentMethod;
import com.trade.extra.PaymentResponse;
import com.trade.modal.PaymentOrder;
import com.trade.modal.User;
import com.trade.service.PaymentService;
import com.trade.service.UserService;

@RestController
@RequestMapping("/api")
public class PaymentController {

	private PaymentService paymentService;
	private UserService userService;

	@Autowired
	public PaymentController(PaymentService paymentService,
			UserService userService) {
		this.paymentService = paymentService;
		this.userService = userService;
	}
	
	@PostMapping("/payment/{paymentMethod}/amount/{amount}")
	public ResponseEntity<PaymentResponse> paymentHandler(
			@PathVariable PaymentMethod paymentMethod,
			@PathVariable Long amount,
			@RequestHeader("Authorization") String jwt
			) throws Exception{
	
		User user = userService.getUserByJwt(jwt);
		
		PaymentResponse paymentResponse;
		
		PaymentOrder order = paymentService.createOrder(user, amount, paymentMethod);
		
		if (paymentMethod.equals(paymentMethod.REZORPAY)) {
			paymentResponse = paymentService.createRazorPayPaymentLing(user, amount,order.getId());
		}else {
			paymentResponse = paymentService.createStripePayPaymentLing(user, amount, order.getId());
		}
		
		return new ResponseEntity<PaymentResponse>(paymentResponse,HttpStatus.CREATED);
	}
	
}
