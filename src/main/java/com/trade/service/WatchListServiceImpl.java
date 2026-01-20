package com.trade.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.trade.extra.SequenceGeneratorService;
import com.trade.modal.Bitcoin;
import com.trade.modal.Order;
import com.trade.modal.User;
import com.trade.modal.Wallet;
import com.trade.modal.WatchList;
import com.trade.repositoy.WatchListRepository;

@Service
public class WatchListServiceImpl implements WatchListService{

	private WatchListRepository watchListRepository;
	private SequenceGeneratorService sequenceGeneratorService;
	
	@Autowired
	public WatchListServiceImpl(WatchListRepository watchListRepository,
			SequenceGeneratorService sequenceGeneratorService) {
		this.watchListRepository = watchListRepository;
		this.sequenceGeneratorService = sequenceGeneratorService;
	}

	
	@Override
	public WatchList findUserWatchList(Long userId) {

	    WatchList watchList = watchListRepository.findByUserId(userId);

	    if (watchList == null) {
	        // create empty watchlist WITHOUT userService
	        WatchList newList = new WatchList();
	        newList.setId(sequenceGeneratorService.generateSequence("watchlist_sequence"));

	        User u = new User();
	        u.setId(userId);

	        newList.setUser(u);

	        return watchListRepository.save(newList);
	    }

	    return watchList;
	}



	@Override
	public WatchList createWatchList(User user) {
		WatchList watchList = new WatchList();
		watchList.setId(sequenceGeneratorService.generateSequence("watchlist_sequence"));
		
		watchList.setUser(user);
		return watchListRepository.save(watchList);
	}

	@Override
	public WatchList findById(Long id) {
		Optional<WatchList> waOptional = watchListRepository.findById(id);
		
		if (waOptional.isEmpty()) {
			throw new RuntimeException("watchlist not found");
		}
		return waOptional.get();
	}

	@Override
	public Bitcoin addItemToWatchList(Bitcoin coin, User user) {

	    WatchList watchList = watchListRepository.findByUserId(user.getId());

	    if (watchList == null) {
	        watchList = createWatchList(user);
	    }

	    if (watchList.getCoins().contains(coin)) {
	        watchList.getCoins().remove(coin);
	    } else {
	        watchList.getCoins().add(coin);
	    }

	    watchListRepository.save(watchList);

	    return coin;
	}
}
