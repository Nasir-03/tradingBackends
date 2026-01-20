package com.trade.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.modal.Bitcoin;
import com.trade.service.CoinService;

@RestController
@RequestMapping("/coins")
public class CoinController {
	
	private CoinService coinService;
	private ObjectMapper objectMapper;
	
	@Autowired
	public CoinController(CoinService coinService, ObjectMapper objectMapper) {
	    this.coinService = coinService;
	    this.objectMapper = objectMapper;
	}

	@GetMapping("/coinList")
	ResponseEntity<List<Bitcoin>> getCoinList(
			@RequestParam(required = false, name =  "page") int page
			){
		List<Bitcoin> coins = coinService.getCoinList(page);
		return new ResponseEntity<List<Bitcoin>>(coins,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/{coinId}/chart")
	ResponseEntity<JsonNode> getMarketChart(
			@PathVariable String coinId,
			@RequestParam("days") int days) throws JsonMappingException, JsonProcessingException{
		
		String response = coinService.getMarketChart(coinId, days);
		JsonNode jsonNode = objectMapper.readTree(response);
		
		return new ResponseEntity<>(jsonNode,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/search")
	ResponseEntity<JsonNode> search(@RequestParam("q") String keyword) throws JsonMappingException, JsonProcessingException{
		String response = coinService.searchCoin(keyword);
		JsonNode jsonNode = objectMapper.readTree(response);
		
		return new ResponseEntity<>(jsonNode,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/top50")
	ResponseEntity<JsonNode> getTop50CoinByMarketCapRank() throws JsonMappingException, JsonProcessingException{
		String response = coinService.getTop50CoinsByMarketCapRank();
		JsonNode jsonNode = objectMapper.readTree(response);
		
		return new ResponseEntity<>(jsonNode,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/trending")
	ResponseEntity<JsonNode> getTrendingCoins() throws JsonMappingException, JsonProcessingException{
		String response = coinService.getTradingCoins();
		JsonNode jsonNode = objectMapper.readTree(response);
		
		return new ResponseEntity<>(jsonNode,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/details/{coinId}")
	public ResponseEntity<Bitcoin> getCoinDetails(@PathVariable String coinId) {
	    return ResponseEntity.ok(coinService.getcoinDetails(coinId));
	}

}
