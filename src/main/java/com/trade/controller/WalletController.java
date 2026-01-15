package com.trade.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.trade.extra.PaymentResponse;
import com.trade.mapper.OrderDto;
import com.trade.mapper.OrderMapper;
import com.trade.modal.Order;
import com.trade.modal.PaymentOrder;
import com.trade.modal.User;
import com.trade.modal.Wallet;
import com.trade.modal.WalletTransaction;
import com.trade.service.OrderService;
import com.trade.service.PaymentService;
import com.trade.service.UserService;
import com.trade.service.WalletService;

@RestController
@RequestMapping("/wallet")
public class WalletController {
	
	private UserService userService;
	private WalletService walletService;
	private OrderService orderService;
	private OrderMapper orderMapper;
	private PaymentService paymentService;

	@Autowired
	public WalletController(UserService userService, WalletService walletService,
			OrderService orderService,OrderMapper orderMapper,PaymentService paymentService) {
		this.userService = userService;
		this.walletService = walletService;
		this.orderService = orderService;
		this.orderMapper = orderMapper;
		this.paymentService = paymentService;
	}

    @GetMapping("/getUserWallet")
	public ResponseEntity<Wallet> getUserWallet(
			@RequestHeader("Authorization") String jwt
			) throws Exception{
		User user = userService.getUserByJwt(jwt);
	
	   Wallet wallet = walletService.getUserWallet(user);
	   
	   return new ResponseEntity<Wallet>(wallet,HttpStatus.ACCEPTED);
	}
    
    @PutMapping("/{walletId}/transfer")
    public ResponseEntity<Wallet> walletToWalletTransfer(
    		@RequestHeader("Authorization") String jwt,
    		@PathVariable Long walletId,
    		@RequestBody WalletTransaction req
    		)throws Exception{
    	User senderUser = userService.getUserByJwt(jwt);
    	Wallet receiverWallet = walletService.findWalletById(walletId);
    	Wallet wallet = walletService.walletToWalletTransfer(senderUser, receiverWallet, req.getAmount(), req.getPurpose());
    	return new ResponseEntity<Wallet>(wallet,HttpStatus.ACCEPTED);
    }
    
    @PutMapping("/order/{orderId}")
    public ResponseEntity<Wallet> payOrderPayment(
    		@RequestHeader("Authorization") String jwt,
    		@PathVariable Long orderId
    		)throws Exception{
    	User user = userService.getUserByJwt(jwt);
    	
    	OrderDto orderDto = orderService.getOrderById(orderId);
    	Order order = orderMapper.toEntity(orderDto);
    	Wallet wallet = walletService.payOrderPayment(order, user);
    	
    	return new ResponseEntity<Wallet>(wallet,HttpStatus.CREATED);
    }
    	
    @PutMapping("/api/wallet/deposit")
    public ResponseEntity<Wallet> addbalanaceToWallet(
    		@RequestHeader("Authorization") String jwt,
    		@RequestParam(name = "order_id")Long orderId,
    		@RequestParam(name = "payment_id")String paymentId
    		
    		)throws Exception{
    	
    	User user = userService.getUserByJwt(jwt);
 
    	Wallet wallet = walletService.getUserWallet(user);
    	
    	if (wallet.getBalance()==null) {
    		wallet.setBalance(BigDecimal.valueOf(0));
    	}
    	
    	PaymentOrder order = paymentService.getPaymentOrderById(orderId);
    	
    	Boolean status = paymentService.proceedPaymentOrder(order, paymentId);
    	
    	PaymentResponse res = new PaymentResponse();
    	
    	if (status) {
    		wallet = walletService.addBalance(wallet, order.getAmount());
    	}
    	
    	return new ResponseEntity<Wallet>(wallet,HttpStatus.ACCEPTED);
    }
}
