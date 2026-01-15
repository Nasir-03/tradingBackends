package com.trade.service;

import java.util.List;

import com.trade.modal.User;
import com.trade.modal.Withdrawl;

public interface WithdrawlService {

	Withdrawl requestWithdrawl(Long amount,User user);
	
	Withdrawl proceedWithWithdrawl(Long withdrawalId,boolean accept);
	
	List<Withdrawl> getUserWithdrawalHistory(User user);
	
	List<Withdrawl> getAllWithdrawlsRequest();
}
