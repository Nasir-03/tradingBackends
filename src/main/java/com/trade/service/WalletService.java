package com.trade.service;

import com.trade.modal.Order;
import com.trade.modal.User;
import com.trade.modal.Wallet;


public interface WalletService {

	Wallet createWallet(User user);
	
	Wallet getUserWallet(User user);
	
	Wallet addBalance(Wallet wallet, long money)throws Exception;
	
	Wallet findWalletById(long id)throws Exception;
	
	Wallet walletToWalletTransfer(User sender,Wallet receiverWallet,long amount,String purpose)throws Exception;
	
	Wallet payOrderPayment(Order order,User user)throws Exception;
}
