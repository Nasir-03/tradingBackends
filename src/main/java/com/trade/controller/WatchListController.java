package com.trade.controller;

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

import com.trade.modal.Bitcoin;
import com.trade.modal.User;
import com.trade.modal.WatchList;
import com.trade.service.CoinService;
import com.trade.service.UserService;
import com.trade.service.WatchListService;

@RestController
@RequestMapping("/api/watchList")
public class WatchListController {

	private WatchListService watchListService;
	private UserService userService;
	private CoinService coinService;
	
	@Autowired
	public WatchListController(WatchListService watchListService, UserService userService,CoinService coinService) {
		this.watchListService = watchListService;
		this.userService = userService;
		this.coinService = coinService;
		
	}


	@GetMapping("/user")
	public ResponseEntity<WatchList> getUserWatchList(
	        @RequestHeader("Authorization") String jwt
	) throws Exception {

	    String token = jwt.startsWith("Bearer ")
	            ? jwt.substring(7)
	            : jwt;

	    User user = userService.getUserByJwt(token);

	    WatchList watchList =
	            watchListService.findUserWatchList(user.getId());   // ✅ CORRECT

	    return new ResponseEntity<>(watchList, HttpStatus.OK);
	}

	
	@PostMapping("/create/{watchlistId}")
	public ResponseEntity<WatchList> createWatchList(
			@PathVariable Long watchlistId
			){
		
           WatchList watchList = watchListService.findById(watchlistId);
           
           return ResponseEntity.ok(watchList);
	}
	
	@PatchMapping("/add/coin/{coinId}")
	public ResponseEntity<Bitcoin> addItemToWatchlist(
	        @RequestHeader("Authorization") String jwt,
	        @PathVariable String coinId
	) throws Exception {

	    // ✅ STRIP BEARER PREFIX
	    String token = jwt.startsWith("Bearer ")
	            ? jwt.substring(7)
	            : jwt;

	    User user = userService.getUserByJwt(token);

	    Bitcoin coin = coinService.findById(coinId);

	    Bitcoin addCoin =
	            watchListService.addItemToWatchList(coin, user);

	   return ResponseEntity.ok(addCoin);
	}
}