package com.trade.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trade.domain.WithdrawlStatus;
import com.trade.modal.User;
import com.trade.modal.Withdrawl;
import com.trade.repositoy.AssetRepository;
import com.trade.repositoy.WithdrawRepository;

@Service
public class WithdrawalServiceImpl implements WithdrawlService{

    private final AssetRepository assetRepository;

	private WithdrawRepository withdrawRepository;
	
	@Autowired
	public WithdrawalServiceImpl(WithdrawRepository withdrawRepository, AssetRepository assetRepository) {
		this.withdrawRepository = withdrawRepository;
		this.assetRepository = assetRepository;
	}

	@Override
	public Withdrawl requestWithdrawl(Long amount, User user) {
      Withdrawl withdrawl = new Withdrawl();
		
		withdrawl.setAmount(amount);
		withdrawl.setWithdrawlStatus(WithdrawlStatus.PENDING);
		withdrawl.setUser(user);
		return withdrawRepository.save(withdrawl);
	}

	@Override
	public Withdrawl proceedWithWithdrawl(Long withdrawalId, boolean accept) {
		Optional<Withdrawl> withdrawal = withdrawRepository.findById(withdrawalId);
		
		if (withdrawal.isEmpty()) {
			throw new RuntimeException("withdrawal not found");
		}
		
		Withdrawl withdrawl1 = withdrawal.get();
		
		withdrawl1.setDate(LocalDateTime.now());

		if (accept) {
			withdrawl1.setWithdrawlStatus(WithdrawlStatus.SUCCESS);
		}else {
			withdrawl1.setWithdrawlStatus(WithdrawlStatus.PENDING);
		}
		return withdrawRepository.save(withdrawl1);
	}

	@Override
	public List<Withdrawl> getUserWithdrawalHistory(User user) {
		return withdrawRepository.findByUserId(user.getId());
	}

	@Override
	public List<Withdrawl> getAllWithdrawlsRequest() {
		return withdrawRepository.findAll();
	}

}
