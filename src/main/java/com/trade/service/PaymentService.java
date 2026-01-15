package com.trade.service;

import com.razorpay.RazorpayException;
import com.trade.domain.PaymentMethod;
import com.trade.extra.PaymentResponse;
import com.trade.modal.PaymentOrder;
import com.trade.modal.User;

public interface PaymentService {

	PaymentOrder createOrder(User user,Long amount,PaymentMethod method);
	
	PaymentOrder getPaymentOrderById(Long id);
	
	Boolean proceedPaymentOrder(PaymentOrder paymentOrder,String paymentId)throws RazorpayException;
	
	PaymentResponse createRazorPayPaymentLing(User user,Long amount,Long orderId)throws RazorpayException;

	PaymentResponse createStripePayPaymentLing(User user,Long amount,Long orderId);
}
