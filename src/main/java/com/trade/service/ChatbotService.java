package com.trade.service;

import com.trade.extra.ApiResponse;

public interface ChatbotService {

	ApiResponse getCoinDetails(String prompts);
	
	String simpleChat(String prompts);
	
	
	ApiResponse ask(String q);
	
}
