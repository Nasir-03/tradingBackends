package com.trade.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.trade.extra.ApiResponse;
import com.trade.extra.CoinDTO;

import jakarta.annotation.PostConstruct;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final RestTemplate rest;
	
    @Autowired
	public ChatbotServiceImpl(RestTemplate rest) {
		this.rest = rest;
	}

    @Override
    public ApiResponse ask(String question) {

        ParsedQuery pq = parseQuestion(question);

        if (pq.symbol == null) {
            return response(
                "Sorry, I couldn’t recognize the coin in your question."
            );
        }

        CoinDTO dto = fetchFromCryptoCompare(pq.symbol);

        if (dto == null) {
            return response("Sorry, I couldn’t find live data for " + pq.symbol);
        }

        Object value = extractValue(dto, pq.field);
        String answer = toHumanAnswerSafe(pq.symbol, pq.field, value);

        return response(answer);
    }

    // ==========================
    // QUESTION PARSER
    // ==========================
    private ParsedQuery parseQuestion(String q) {

        String p = q.toLowerCase().replaceAll("[^a-z0-9 ]", " ").trim();

        String coinInput;
        if (p.contains(" of ")) {
            coinInput = p.substring(p.lastIndexOf(" of ") + 4).trim().split(" ")[0];
        } else {
            String[] parts = p.split("\\s+");
            coinInput = parts[parts.length - 1];
        }

        String symbol = resolveSymbol(coinInput);

        String field = "current_price";
        if (p.contains("market cap")) field = "market_cap";
        else if (p.contains("volume")) field = "volume";
        else if (p.contains("rank")) field = "rank";
        else if (p.contains("high")) field = "high_24h";
        else if (p.contains("low")) field = "low_24h";

        return new ParsedQuery(symbol, field);
    }

    // ==========================
    // SYMBOL RESOLUTION
    // ==========================
    private String resolveSymbol(String input) {

        if (input == null) return null;

        Map<String, String> coinMap = loadCryptoCompareCoinMap();
        if (coinMap == null) return null;

        String normalized = input
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");

        return coinMap.get(normalized);

    }


    // ==========================
    // CRYPTOCOMPARE FETCH (CACHED)
    // ==========================
    @Cacheable(
        value = "coin-prices",
        key = "#symbol",
        unless = "#result == null"
    )
    public CoinDTO fetchFromCryptoCompare(String symbol) {

        try {
            String url =
                "https://min-api.cryptocompare.com/data/pricemultifull" +
                "?fsyms=" + symbol +
                "&tsyms=USD" +
                "&api_key=" + System.getenv("CRYPTOCOMPARE_KEY");

            Map res = rest.getForObject(url, Map.class);
            if (res == null || res.get("RAW") == null) return null;

            Map raw = (Map) ((Map) res.get("RAW")).get(symbol);
            if (raw == null) return null;

            Map usd = (Map) raw.get("USD");

            CoinDTO d = new CoinDTO();
            d.setCurrentPrice(toDouble(usd.get("PRICE")));
            d.setMarketCap(toDouble(usd.get("MKTCAP")));
            d.setTotalVolume(toDouble(usd.get("TOTALVOLUME24H")));
            d.setHigh24h(toDouble(usd.get("HIGH24HOUR")));
            d.setLow24h(toDouble(usd.get("LOW24HOUR")));
            d.setMarketCapRank(0);

            return d;

        } catch (Exception e) {
            return null;
        }
    }

    // ==========================
    // HELPERS
    // ==========================
    private Object extractValue(CoinDTO d, String f) {
        return switch (f) {
            case "market_cap" -> d.getMarketCap();
            case "volume" -> d.getTotalVolume();
            case "high_24h" -> d.getHigh24h();
            case "low_24h" -> d.getLow24h();
            default -> d.getCurrentPrice();
        };
    }

    private String toHumanAnswerSafe(String coin, String field, Object value) {
        return "The " + field.replace("_", " ") +
               " of " + coin +
               " is " + value + " USD.";
    }

    private double toDouble(Object v) {
        try {
            return v == null ? 0 : Double.parseDouble(v.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private ApiResponse response(String msg) {
        ApiResponse r = new ApiResponse();
        r.setData(msg);
        return r;
    }

    private static class ParsedQuery {
        String symbol;
        String field;
        ParsedQuery(String s, String f) {
            this.symbol = s;
            this.field = f;
        }
    }
    
    @Cacheable(value = "crypto-coinlist", unless = "#result == null")
    public Map<String, String> loadCryptoCompareCoinMap() {

        try {
            String url =
                "https://min-api.cryptocompare.com/data/all/coinlist" +
                "?api_key=" + System.getenv("CRYPTOCOMPARE_KEY");

            Map res = rest.getForObject(url, Map.class);
            if (res == null || res.get("Data") == null) return null;

            Map<String, Map> data = (Map<String, Map>) res.get("Data");

            Map<String, String> nameToSymbol = new java.util.HashMap<>();

            for (Map.Entry<String, Map> e : data.entrySet()) {
                Map meta = e.getValue();

                String symbol = e.getKey(); // BTC
                String name = meta.get("Name").toString().toLowerCase();
                String coinName = meta.get("CoinName").toString().toLowerCase();
                String fullName = meta.get("FullName").toString().toLowerCase();

//                nameToSymbol.put(symbol.toLowerCase(), symbol);
//                nameToSymbol.put(name, symbol);
//                nameToSymbol.put(coinName, symbol);
//                nameToSymbol.put(fullName.replace(" ", ""), symbol);
//
//                nameToSymbol.put(symbol.toLowerCase(), symbol);
//                nameToSymbol.put(name, symbol);
                
                addKey(nameToSymbol, symbol, symbol);
                addKey(nameToSymbol, symbol, name);
                addKey(nameToSymbol, symbol, coinName);
                addKey(nameToSymbol, symbol, fullName);
            }

            return nameToSymbol;

        } catch (Exception e) {
            return null;
        }
    }

    private void addKey(Map<String, String> map, String symbol, String value) {
        if (value == null) return;

        String key = value
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");

        map.put(key, symbol);
    }

    @PostConstruct
    public void preloadCoinList() {
        loadCryptoCompareCoinMap();
    }
    
}
