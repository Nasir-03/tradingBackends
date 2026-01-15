package com.trade.service;

import com.trade.modal.Bitcoin;
import com.trade.modal.User;
import com.trade.modal.WatchList;

public interface WatchListService {

	WatchList findUserWatchList(Long userId);
	
	WatchList createWatchList(User user);
	
	WatchList findById(Long id);
	
	Bitcoin addItemToWatchList(Bitcoin coin,User user);
}
