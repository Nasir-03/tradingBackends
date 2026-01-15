package com.trade.service;

import java.util.List;

import com.trade.modal.Bitcoin;

public interface CoinService {

	List<Bitcoin> getCoinList(int page);
	
	String getMarketChart(String coinId, int days);
	
	Bitcoin getcoinDetails(String coinId);
	
	Bitcoin findById(String coinId) throws Exception;
	
	String searchCoin(String keyword);
	
	String getTop50CoinsByMarketCapRank();
	
	String getTradingCoins();
	
	Bitcoin getCoinFromDB(String coinId);
}
