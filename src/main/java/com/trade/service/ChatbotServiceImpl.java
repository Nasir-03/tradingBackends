package com.trade.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.trade.extra.ApiResponse;
import com.trade.extra.CoinDTO;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final RestTemplate rest = new RestTemplate();

    @Value("${GROQ_API_KEY:}")
    private String groqKey;

    // ==========================
    // ENTRY POINT
    // ==========================
    @Override
    public ApiResponse ask(String question) {

        ParsedQuery pq = parseQuestion(question);

        if (pq.coinId == null) {
            return response("Sorry, I couldn’t recognize the coin in your question.");
        }

        CoinDTO dto = fetchFromCoinGeckoSafe(pq.coinId);

        if (dto == null) {
            return response("Sorry, I couldn’t find live data for " + pq.coinId.toUpperCase());
        }

        Object value = extractValue(dto, pq.field);
        String answer = toHumanAnswerSafe(pq.coinId, pq.field, value);

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

        String coinId = resolveCoinId(coinInput);

        String field = "current_price";
        if (p.contains("market cap")) field = "market_cap";
        else if (p.contains("volume")) field = "volume";
        else if (p.contains("rank")) field = "rank";
        else if (p.contains("high")) field = "high_24h";
        else if (p.contains("low")) field = "low_24h";

        return new ParsedQuery(coinId, field);
    }

    // ==========================
    // COIN ID RESOLUTION
    // ==========================
    private String resolveCoinId(String input) {

        if (input == null || input.isBlank()) return null;

        switch (input.toLowerCase()) {
            case "btc": case "bitcoin": return "bitcoin";
            case "eth": case "ethereum": return "ethereum";
            case "bnb": return "binancecoin";
            case "xmr": case "monero": return "monero";
            case "sol": case "solana": return "solana";
            case "doge": case "dogecoin": return "dogecoin";
        }

        try {
            String url =
                "https://api.coingecko.com/api/v3/search?query=" +
                URLEncoder.encode(input, StandardCharsets.UTF_8);

            Map res = rest.getForObject(url, Map.class);
            if (res == null || res.get("coins") == null) return null;

            List<Map> coins = (List<Map>) res.get("coins");
            if (coins.isEmpty()) return null;

            return coins.get(0).get("id").toString();

        } catch (Exception e) {
            return null;
        }
    }

    // ==========================
    // COINGECKO FETCH (CACHED)
    // ==========================
    @Cacheable(value = "coin-prices", key = "#coinId")
    public CoinDTO fetchFromCoinGeckoSafe(String coinId) {

        try {
            String url = "https://api.coingecko.com/api/v3/coins/" + coinId;

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-cg-demo-api-key", System.getenv("COINGECKO_API_KEY"));

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            Map res = rest.exchange(
                    url,
                    org.springframework.http.HttpMethod.GET,
                    entity,
                    Map.class
            ).getBody();

            if (res == null || !res.containsKey("market_data")) return null;

            Map market = (Map) res.get("market_data");

            CoinDTO d = new CoinDTO();
            d.setCurrentPrice(toDouble(((Map) market.get("current_price")).get("usd")));
            d.setMarketCap(toDouble(((Map) market.get("market_cap")).get("usd")));
            d.setTotalVolume(toDouble(((Map) market.get("total_volume")).get("usd")));
            d.setHigh24h(toDouble(((Map) market.get("high_24h")).get("usd")));
            d.setLow24h(toDouble(((Map) market.get("low_24h")).get("usd")));
            d.setMarketCapRank(toDouble(res.get("market_cap_rank")));

            return d;

        } catch (Exception e) {
            System.err.println("CoinGecko API failed for coin: " + coinId);
            e.printStackTrace();
            return null;
        }
    }

    // ==========================
    // AI (OPTIONAL)
    // ==========================
    private String toHumanAnswerSafe(String coin, String field, Object value) {

        return "The " + field.replace("_", " ")
            + " of " + coin.toUpperCase()
            + " is " + value + " USD.";
    }


    private String callGroq(String content) {

        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqKey);

        JSONObject body = new JSONObject()
            .put("model", "llama-3.1-8b-instant")
            .put("messages", new JSONArray()
                .put(new JSONObject()
                    .put("role", "user")
                    .put("content", content)
                )
            );

        String response = rest.postForObject(
            url,
            new HttpEntity<>(body.toString(), headers),
            String.class
        );

        return new JSONObject(response)
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content");
    }

    // ==========================
    // HELPERS
    // ==========================
    private Object extractValue(CoinDTO d, String f) {
        return switch (f) {
            case "market_cap" -> d.getMarketCap();
            case "volume" -> d.getTotalVolume();
            case "rank" -> d.getMarketCapRank();
            case "high_24h" -> d.getHigh24h();
            case "low_24h" -> d.getLow24h();
            default -> d.getCurrentPrice();
        };
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
        String coinId;
        String field;
        ParsedQuery(String c, String f) {
            this.coinId = c;
            this.field = f;
        }
    }
}
