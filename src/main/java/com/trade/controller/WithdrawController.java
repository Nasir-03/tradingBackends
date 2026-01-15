package com.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.trade.modal.User;
import com.trade.modal.Wallet;
import com.trade.modal.WalletTransaction;
import com.trade.modal.Withdrawl;
import com.trade.service.UserService;
import com.trade.service.WalletService;
import com.trade.service.WithdrawlService;

@RestController
@RequestMapping("/withdrawl")
public class WithdrawController {

	private WithdrawlService withdrawlService;
	private WalletService walletService;
	private UserService userService;
	
	@Autowired
	public WithdrawController(WithdrawlService withdrawlService, WalletService walletService, UserService userService) {
		this.withdrawlService = withdrawlService;
		this.walletService = walletService;
		this.userService = userService;
	}
	
	@PostMapping("/request/{amount}")
	public ResponseEntity<?> withdrawRequest(
			@PathVariable Long amount,
			@RequestHeader("Authorization")String jwt
			) throws Exception{
		User user = userService.getUserByJwt(jwt);
		Wallet userWallet = walletService.getUserWallet(user);
		
		Withdrawl withdrawl = withdrawlService.requestWithdrawl(amount, user);
	    walletService.addBalance(userWallet, -withdrawl.getAmount());
	    
//	    WalletTransaction walletTransaction = wa
	    
	    return new ResponseEntity<>(withdrawl,HttpStatus.OK);
	}
	
	@PatchMapping("/api/admin/withdrawl/{id}/proceed/{accept}")
	public ResponseEntity<?> proceedWithdrawl(
			@PathVariable Long id,
			@PathVariable boolean accept,
			@RequestHeader("Authorization")String jwt
			) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		Withdrawl withdrawl = withdrawlService.proceedWithWithdrawl(id, accept);
		
		Wallet userWallet = walletService.getUserWallet(user);
		
		if(!accept) {
			walletService.addBalance(userWallet, withdrawl.getAmount());
		}
		return new ResponseEntity<>(withdrawl,HttpStatus.OK);
	}
	
	@GetMapping("/api/withdrawl")
	public ResponseEntity<List<Withdrawl>> getWithdrawlHistory(
			@RequestHeader("Authorization")String jwt
			) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		
		List<Withdrawl> withdrawl = withdrawlService.getUserWithdrawalHistory(user);
		
		return new ResponseEntity<List<Withdrawl>>(withdrawl,HttpStatus.OK);
	}
	
	@GetMapping("/api/admin/withdrawl")
	public ResponseEntity<List<Withdrawl>> getAllWithdrawlRequest(
			@RequestHeader("Authorization")String jwt
			) throws Exception{
		
		User user = userService.getUserByJwt(jwt);
		
		List<Withdrawl> withdrawl = withdrawlService.getAllWithdrawlsRequest();
		
		return new ResponseEntity<List<Withdrawl>>(withdrawl,HttpStatus.OK);
	}
}
