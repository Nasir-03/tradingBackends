package com.trade.service;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

    @Value("${CRYPTOCOMPARE_KEY}")
    private String cryptoKey;

    // ==========================
    // ENTRY POINT
    // ==========================
    @Override
    public ApiResponse ask(String question) {

        try {            
            ParsedQuery pq = parseQuestion(question);

            if (pq.coin == null) {
                return response("Sorry, I couldn’t recognize the coin in your question.");
            }


            CoinDTO dto = fetchCoin(pq.coin);

            if (dto == null) {
                return response("Sorry, I couldn’t find data for " + pq.coin.toUpperCase());
            }

            Object value = extractValue(dto, pq.field);

            String humanAnswer = toHumanAnswer(pq.coin, pq.field, value);

            return response(humanAnswer);

        } catch (Exception e) {
            return response("Service temporarily unavailable. Please try again.");
        }
    }

    // ==========================
    // QUESTION PARSER (NO AI)
    // ==========================
    
    private ParsedQuery parseQuestion(String q) {

        String p = q.toLowerCase().replaceAll("[^a-z0-9 ]", " ").trim();

        String coin = null;

        // Prefer "of <coin>"
        if (p.contains(" of ")) {
            coin = p.substring(p.lastIndexOf(" of ") + 4).trim().split(" ")[0];
        } else {
            // fallback: last word
            String[] parts = p.split("\\s+");
            coin = parts[parts.length - 1];
        }

        coin = resolveCoinGeckoId(normalizeCoin(coin));

        String field = "current_price";
        if (p.contains("market cap") || p.contains("marketcap")) field = "market_cap";
        else if (p.contains("volume")) field = "volume";
        else if (p.contains("rank")) field = "rank";
        else if (p.contains("high")) field = "high_24h";
        else if (p.contains("low")) field = "low_24h";

        return new ParsedQuery(coin, field);
    }

    

    private String normalizeCoin(String coin) {

        if (coin == null) return null;

        return switch (coin.toLowerCase()) {
            case "btc", "bitcoin" -> "bitcoin";
            case "eth", "ethereum" -> "ethereum";
            case "sol", "solana" -> "solana";
            case "doge", "dogecoin" -> "dogecoin";
            case "ena", "ethena" -> "ethena";
            case "tao", "bittensor" -> "bittensor";
//            default -> null; // ❗ VERY IMPORTANT
            default -> coin.toLowerCase();
        };
    }

    private String resolveCoinGeckoId(String input) {

        try {
            // 1. Call CoinGecko search API
            String url = "https://api.coingecko.com/api/v3/search?query=" + input;

            Map res = rest.getForObject(url, Map.class);

            if (res == null || res.get("coins") == null) return null;

            var coins = (java.util.List<Map>) res.get("coins");

            if (coins.isEmpty()) return null;

            // 2. Pick the TOP result
            return coins.get(0).get("id").toString();

        } catch (Exception e) {
            return null;
        }
    }


    // ==========================
    // FETCH DATA
    // ==========================
    private CoinDTO fetchCoin(String coin) {

        if (coin == null) return null;

        try {
            return fromCoinGecko(coin);
        } catch (Exception e) {

            // ❗ Only fallback for major coins
            if (coin.equals("bitcoin")
             || coin.equals("ethereum")
             || coin.equals("solana")
             || coin.equals("dogecoin")) {

                return fromCryptoCompare(coin);
            }

            return null; // do NOT guess
        }
    }



    // ==========================
    // COINGECKO
    // ==========================
    private CoinDTO fromCoinGecko(String coin) {

        String url = "https://api.coingecko.com/api/v3/coins/" + coin;

        Map res = rest.getForObject(url, Map.class);

        if (res == null || !res.containsKey("market_data")) {
            throw new RuntimeException("Invalid CoinGecko response");
        }

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
    // CRYPTOCOMPARE FALLBACK
    // ==========================
    private CoinDTO fromCryptoCompare(String coin) {

        String symbol = toSymbol(coin);

        String url =
            "https://min-api.cryptocompare.com/data/pricemultifull?fsyms="
            + symbol + "&tsyms=USD";

        HttpHeaders h = new HttpHeaders();
        h.set("authorization", "Apikey " + cryptoKey);

        var response =
            rest.exchange(url, HttpMethod.GET, new HttpEntity<>(h), Map.class);

        Map body = response.getBody();
        if (body == null || body.get("RAW") == null) return null;

        Map raw = (Map) body.get("RAW");
        Map usd = (Map) ((Map) raw.get(symbol)).get("USD");

        CoinDTO d = new CoinDTO();
        d.setCurrentPrice(toDouble(usd.get("PRICE")));
        d.setMarketCap(toDouble(usd.get("MKTCAP")));
        d.setTotalVolume(toDouble(usd.get("VOLUME24HOUR")));
        d.setMarketCapRank(toDouble(usd.get("MKTCAP_RANK")));

        return d;
    }

    // ==========================
    // HUMAN RESPONSE (AI)
    // ==========================
    private String toHumanAnswer(String coin, String field, Object value) {

        try {
            return callGroq("""
            Convert this into a natural one-line answer:

            Coin: %s
            Field: %s
            Value: %s
            """.formatted(coin, field, value));
        } catch (Exception e) {
        	return "The " + field.replace("_", " ")
            + " of " + coin.toUpperCase()
            + " is " + value;

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

    private String toSymbol(String coin) {
        return switch (coin) {
            case "bitcoin" -> "BTC";
            case "ethereum" -> "ETH";
            case "solana" -> "SOL";
            case "dogecoin" -> "DOGE";
            default -> coin.toUpperCase();
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
        String coin;
        String field;
        ParsedQuery(String c, String f) {
            this.coin = c;
            this.field = f;
        }
    }
}
