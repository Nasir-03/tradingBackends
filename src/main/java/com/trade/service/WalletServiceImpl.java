package com.trade.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.trade.domain.OrderType;
import com.trade.extra.SequenceGeneratorService;
import com.trade.modal.Order;
import com.trade.modal.User;
import com.trade.modal.Wallet;
import com.trade.repositoy.WalletRepository;

@Service
public class WalletServiceImpl implements WalletService{

	private final WalletRepository walletRepository;
	private final SequenceGeneratorService sequenceGeneratorService;
	
	@Autowired
	public WalletServiceImpl(WalletRepository walletRepository,
			SequenceGeneratorService sequenceGeneratorService) {
		this.walletRepository = walletRepository;
		this.sequenceGeneratorService = sequenceGeneratorService;
	}
	
	@Override
	public Wallet createWallet(User user) {
		Wallet wallet = new Wallet();
        wallet.setId(sequenceGeneratorService.generateSequence("wallet_sequence"));  // 🔥 REQUIRED
        wallet.setUser(user);
//        wallet.setBalance(0);
        wallet.setBalance(BigDecimal.ZERO);
        wallet = walletRepository.save(wallet);
		return wallet;
	}
	
	@Override
	public Wallet getUserWallet(User user) {
		Wallet wallet = walletRepository.findByUser_Id(user.getId());

	    if (wallet == null) {
	    	createWallet(user);
	    }
	    return wallet;
	}


	@Override
	@Transactional
	public Wallet addBalance(Wallet wallet, long money) {

	    BigDecimal balance = wallet.getBalance();
	    BigDecimal newBalance = balance.add(BigDecimal.valueOf(money));

	    wallet.setBalance(newBalance);

	    return walletRepository.save(wallet);
	}

	@Override
	public Wallet findWalletById(long id) throws Exception {
		return walletRepository.findById(id)
				.orElseThrow(() -> new Exception("wallet not found with id: "+id));
	}

	@Override
	public Wallet walletToWalletTransfer(User sender, Wallet receiverWallet, long amount, String purpose) throws Exception {
		Wallet senderWallet = getUserWallet(sender);
		Wallet receiverWallets = walletRepository.findById(receiverWallet.getId())
				.orElseThrow(() -> new Exception("receiver wallet not found"));
		
		if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0) {
			throw new Exception("insuffiecient amount");
		}
		
		BigDecimal senderAmount = senderWallet.getBalance().subtract(BigDecimal.valueOf(amount));
		senderWallet.setBalance(senderAmount);
		walletRepository.save(senderWallet);
		
		BigDecimal receiverAmount = receiverWallet.getBalance().add(BigDecimal.valueOf(amount));
		receiverWallets.setBalance(receiverAmount);
		walletRepository.save(receiverWallets);
		
		return senderWallet;
	}

	@Override
	public Wallet payOrderPayment(Order order, User user) throws Exception {
		Wallet wallet = getUserWallet(user);
		
		if (order.getOrderType().equals(OrderType.BUY)) {
			BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
			if (newBalance.compareTo(order.getPrice())<0) {
				throw new Exception("Insuffiecient fund for order");
			}
			wallet.setBalance(newBalance);
		}else {
			BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
			wallet.setBalance(newBalance);
		}
		return walletRepository.save(wallet);
	}

}
