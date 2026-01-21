package com.trade.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.trade.extra.ApiResponse;
import com.trade.extra.CoinDTO;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final RestTemplate rest = new RestTemplate();

    @Value("${GROQ_API_KEY}")
    private String groqKey;

    // ==========================
    // ENTRY POINT
    // ==========================
    @Override
    public ApiResponse ask(String question) {

        try {
            ParsedQuery pq = parseQuestion(question);

            if (pq.coinId == null) {
                return response("Sorry, I couldn’t recognize the coin in your question.");
            }

            CoinDTO dto = fetchFromCoinGecko(pq.coinId);
            if (dto == null) {
                return response("Sorry, I couldn’t find data for " + pq.coinId.toUpperCase());
            }

            Object value = extractValue(dto, pq.field);
            String answer = toHumanAnswer(pq.coinId, pq.field, value);

            return response(answer);

        } catch (Exception e) {
            return response("Service temporarily unavailable. Please try again.");
        }
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
        if (p.contains("market cap") || p.contains("marketcap")) field = "market_cap";
        else if (p.contains("volume")) field = "volume";
        else if (p.contains("rank")) field = "rank";
        else if (p.contains("high")) field = "high_24h";
        else if (p.contains("low")) field = "low_24h";

        return new ParsedQuery(coinId, field);
    }

    // ==========================
    // COIN ID RESOLUTION (SAFE)
    // ==========================
    private String resolveCoinId(String input) {

        if (input == null || input.isBlank()) return null;

        // 🔒 Hard-map major coins (NEVER SEARCH)
        switch (input.toLowerCase()) {
            case "btc":
            case "bitcoin": return "bitcoin";
            case "eth":
            case "ethereum": return "ethereum";
            case "bnb": return "binancecoin";
            case "xmr":
            case "monero": return "monero";
            case "sol":
            case "solana": return "solana";
            case "doge":
            case "dogecoin": return "dogecoin";
        }

        // 🔍 CoinGecko search for everything else
        try {
            String url =
                "https://api.coingecko.com/api/v3/search?query=" +
                URLEncoder.encode(input, StandardCharsets.UTF_8);

            Map res = rest.getForObject(url, Map.class);
            if (res == null || res.get("coins") == null) return null;

            List<Map> coins = (List<Map>) res.get("coins");
            if (coins.isEmpty()) return null;

            // 1️⃣ Exact symbol match
            for (Map c : coins) {
                if (input.equalsIgnoreCase(c.get("symbol").toString())) {
                    return c.get("id").toString();
                }
            }

            // 2️⃣ Exact name match
            for (Map c : coins) {
                if (input.equalsIgnoreCase(c.get("name").toString())) {
                    return c.get("id").toString();
                }
            }

            // 3️⃣ CoinGecko relevance (first result)
            return coins.get(0).get("id").toString();

        } catch (Exception e) {
            return null;
        }
    }

    // ==========================
    // COINGECKO FETCH (SOURCE OF TRUTH)
    // ==========================
    private CoinDTO fetchFromCoinGecko(String coinId) {

        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;

        Map res = rest.getForObject(url, Map.class);
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
    }

    // ==========================
    // HUMAN RESPONSE (SAFE AI)
    // ==========================
    private String toHumanAnswer(String coin, String field, Object value) {

        String factual =
            "The " + field.replace("_", " ") +
            " of " + coin.toUpperCase() +
            " is " + value;

        try {
            String ai = callGroq("""
            Rewrite this fact in ONE short sentence.
            Do NOT add assumptions, dates, or opinions.

            %s
            """.formatted(factual));

            if (ai == null || ai.isBlank()) return factual;
            return ai;

        } catch (Exception e) {
            return factual;
        }
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
            case "current_price" -> d.getCurrentPrice();
            case "market_cap" -> d.getMarketCap();
            case "volume" -> d.getTotalVolume();
            case "rank" -> d.getMarketCapRank();
            case "high_24h" -> d.getHigh24h();
            case "low_24h" -> d.getLow24h();
            default -> d.getCurrentPrice();
        };
    }

    private double toDouble(Object v) {
        if (v == null) return 0;
        return Double.parseDouble(v.toString());
    }

    private ApiResponse response(String msg) {
        ApiResponse r = new ApiResponse();
        r.setData(msg);
        return r;
    }

    // ==========================
    // INTERNAL CLASS
    // ==========================
    private static class ParsedQuery {
        String coinId;
        String field;
        ParsedQuery(String c, String f) {
            this.coinId = c;
            this.field = f;
        }
    }
}
