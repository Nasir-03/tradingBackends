package com.trade.service;
//import java.lang.reflect.Field;
//import java.util.List;
//import java.util.Map;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import com.trade.extra.ApiResponse;
//import com.trade.extra.CoinDTO;
//import com.trade.extra.FunctionResponse;
//
//
//@Service
//public class ChatbotServiceImpl implements ChatbotService{
//	
//  private final RestTemplate restTemplate = new RestTemplate();
//  
//  @Value("${GROQ_API_KEY}")
//  String GROQ_API_KEY;
//  
//  @Value("${CRYPTOCOMPARE_KEY}")
//  private String cryptoCompareKey;
//
//    private static final Map<String,String> FIELD_MAP = Map.of(
//    	    "current_price","currentPrice",
//    	    "market_cap","marketCap",
//    	    "rank","marketCapRank",
//    	    "volume","totalVolume",
//    	    "high_24h","high24h",
//    	    "low_24h","low24h",
//    	    "price_change_24h","priceChange24h",
//    	    "price_change_percentage_24h","priceChangePercentage24h",
//    	    "circulating_supply","circulatingSupply",
//    	    "total_supply","totalSupply"
//    	);
//
//    
//    private double toDouble(Object value){
//        if(value == null) return 0;
//        if(value instanceof Integer) return ((Integer)value).doubleValue();
//        if(value instanceof Long) return ((Long)value).doubleValue();
//        if(value instanceof Double) return (Double)value;
//        return Double.parseDouble(value.toString());
//    }
//
//    private CoinDTO fetchFromCoinGecko(String coinId){
//
//        if(coinId == null || coinId.isBlank())
//            coinId = "bitcoin";
//
//        coinId = coinId.toLowerCase().trim();
//
//        String url="https://api.coingecko.com/api/v3/coins/" + coinId;
//
//        Map<String,Object> res = restTemplate.getForObject(url, Map.class);
//
//        Map<String,Object> market = (Map<String,Object>)res.get("market_data");
//
//        if(market == null)
//            throw new RuntimeException("CoinGecko returned no market data for " + coinId);
//
//        Map<String,Object> image = (Map<String,Object>)res.get("image");
//
//        CoinDTO dto = new CoinDTO();
//
//        dto.setId((String)res.get("id"));
//        dto.setName((String)res.get("name"));
//        dto.setSymbol((String)res.get("symbol"));
//
//        dto.setImage(
//            image != null && image.get("large") != null
//                ? image.get("large").toString()
//                : ""
//        );
//
//        dto.setCurrentPrice(toDouble(((Map<?,?>)market.get("current_price")).get("usd")));
//        dto.setMarketCap(toDouble(((Map<?,?>)market.get("market_cap")).get("usd")));
//        dto.setMarketCapRank(toDouble(res.get("market_cap_rank")));
//        dto.setTotalVolume(toDouble(((Map<?,?>)market.get("total_volume")).get("usd")));
//        dto.setHigh24h(toDouble(((Map<?,?>)market.get("high_24h")).get("usd")));
//        dto.setLow24h(toDouble(((Map<?,?>)market.get("low_24h")).get("usd")));
//
//        dto.setPriceChange24h(toDouble(market.get("price_change_24h")));
//        dto.setPriceChangePercentage24h(toDouble(market.get("price_change_percentage_24h")));
//
//        dto.setCirculatingSupply(toDouble(market.get("circulating_supply")));
//        dto.setTotalSupply(toDouble(market.get("total_supply")));
//
//        return dto;
//    }
//
//    
//    private CoinDTO fetchFromCryptoCompare(String coinId){
//
//        try {
//
//            String symbol = coinId.toUpperCase();
//
//            String url =
//            "https://min-api.cryptocompare.com/data/pricemultifull?fsyms="
//            + symbol + "&tsyms=USD&api_key=" + cryptoCompareKey;
//
//            Map<String,Object> res =
//                restTemplate.getForObject(url, Map.class);
//
//            // 🔐 SAFE CHECKS
//            if(res == null || res.get("RAW") == null){
//                throw new RuntimeException("No RAW data from CryptoCompare");
//            }
//
//            Map raw = (Map) res.get("RAW");
//
//            if(raw.get(symbol) == null){
//                throw new RuntimeException("Symbol not found: " + symbol);
//            }
//
//            Map usd = (Map)((Map)raw.get(symbol)).get("USD");
//
//            if(usd == null){
//                throw new RuntimeException("USD data missing for: " + symbol);
//            }
//
//            CoinDTO dto = new CoinDTO();
//
//            dto.setId(symbol);
//            dto.setName(symbol);
//            dto.setSymbol(symbol);
//
//            dto.setCurrentPrice(toDouble(usd.get("PRICE")));
//            dto.setMarketCap(toDouble(usd.get("MKTCAP")));
//            dto.setTotalVolume(toDouble(usd.get("VOLUME24HOUR")));
//            dto.setPriceChange24h(toDouble(usd.get("CHANGE24HOUR")));
//            dto.setPriceChangePercentage24h(
//                toDouble(usd.get("CHANGEPCT24HOUR"))
//            );
//
//            return dto;
//
//        } catch(Exception e){
//
//            // 🔥 HARD FALLBACK – NEVER NULL
//            CoinDTO dto = new CoinDTO();
//            dto.setId("bitcoin");
//            dto.setName("Bitcoin");
//            dto.setSymbol("BTC");
//            dto.setCurrentPrice(0);
//
//            return dto;
//        }
//    }
//
//
//       
////    @Cacheable(value = "coingecko", key = "#coinId", unless = "#result == null")
////    public CoinDTO makeApiRequest(String coinId){
////
////        try {
////            return fetchFromCoinGecko(coinId);
////        } 
////        catch(HttpClientErrorException.TooManyRequests e) {
////            // 🔥 RATE LIMIT → FALLBACK
////            return fetchFromCryptoCompare(coinId);
////        }
////        catch(Exception e) {
////            return fetchFromCryptoCompare(coinId);
////        }
////    }
//    
//    @Cacheable(value = "coingecko", key = "#coinId", unless = "#result == null")
//    public CoinDTO makeApiRequest(String coinId){
//
//        try {
//            return fetchFromCoinGecko(coinId);
//        } 
//        catch(Exception e) {
//
//            try {
//                return fetchFromCryptoCompare(coinId);
//            } catch(Exception ex) {
//
//                // FINAL SAFE OBJECT
//                CoinDTO dto = new CoinDTO();
//                dto.setId("bitcoin");
//                dto.setName("Bitcoin");
//                dto.setSymbol("BTC");
//                dto.setCurrentPrice(0);
//                return dto;
//            }
//        }
//    }
//
//
//   
////    @Override
////    public ApiResponse getCoinDetails(String prompt){
////
////      try {
////
////         FunctionResponse fr = getFunctionResponse(prompt);
////
////         CoinDTO dto = makeApiRequest(fr.getCurrencyName());
////
////         Object val = getFieldValue(dto, fr.getCurrenctData());
////
////         ApiResponse res = new ApiResponse();
////
////         // ❗ NO GROQ HERE
////         res.setData(
////           fr.getCurrencyName() + " " +
////           fr.getCurrenctData() + " is " +
////           val
////         );
////
////         return res;
////
////      } catch(Exception e){
////
////         e.printStackTrace();   // IMPORTANT
////
////         ApiResponse res = new ApiResponse();
////
////         res.setMessage("Service busy. Using backup data.");
////         res.setData("Try again in few seconds");
////
////         return res;
////      }
////    }
//    
//    
//    @Override
//    public ApiResponse getCoinDetails(String prompt){
//
//      try {
//
//         FunctionResponse fr = getFunctionResponse(prompt);
//
//         CoinDTO dto = makeApiRequest(fr.getCurrencyName());
//
//         Object val = getFieldValue(dto, fr.getCurrenctData());
//
//         ApiResponse res = new ApiResponse();
//
//         res.setData(
//           fr.getCurrencyName() + " " +
//           fr.getCurrenctData() + " is " +
//           val
//         );
//
//         return res;
//
//      } catch(Exception e){
//
//         e.printStackTrace();
//
//         ApiResponse res = new ApiResponse();
//
//         res.setMessage("Service busy. Using backup data.");
//         res.setData("Try again in few seconds");
//
//         return res;
//      }
//    }
//
//
//    
//    private Object getFieldValue(CoinDTO dto,String field){
//        try{
//            field=FIELD_MAP.getOrDefault(field,field);
//            Field f=CoinDTO.class.getDeclaredField(field);
//            f.setAccessible(true);
//            return f.get(dto);
//        }catch(Exception e){
//            return "Unsupported data field";
//        }
//    }
//
//    @Override
//    public String simpleChat(String prompt) {
//
//            String url = "https://api.groq.com/openai/v1/chat/completions";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + GROQ_API_KEY);
//
//        JSONObject userMsg = new JSONObject();
//        userMsg.put("role", "user");
//        userMsg.put("content", prompt);
//
//        JSONArray messages = new JSONArray();
//        messages.put(userMsg);
//
//        JSONObject body = new JSONObject();
//        body.put("model", "llama-3.1-8b-instant");   // ✅ stable model
//        body.put("messages", messages);
//        body.put("temperature", 0.7);
//
//        HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response =
//                restTemplate.postForEntity(url, entity, String.class);
//
//        return response.getBody();
//    } 
//
//
//
//    private JSONObject getGroqToolDefinition() {
//        return new JSONObject()
//            .put("type","function")
//            .put("function", new JSONObject()
//                .put("name","getCoinDetails")
//                .put("description","Fetch crypto market metric")
//                .put("parameters", new JSONObject()
//                    .put("type","object")
//                    .put("properties", new JSONObject()
//                        .put("currencyName", new JSONObject()
//                            .put("type","string")
//                            .put("description","Valid CoinGecko coin id. Example: bitcoin, ethereum, solana"))
//                        .put("currencyData", new JSONObject()
//                            .put("type","string")
//                            .put("enum", new JSONArray()
//                                .put("current_price")
//                                .put("market_cap")
//                                .put("rank")
//                                .put("volume")
//                                .put("high_24h")
//                                .put("low_24h")
//                                .put("price_change_24h")
//                                .put("price_change_percentage_24h")
//                                .put("circulating_supply")
//                                .put("total_supply")
//                            )
//                        )
//                    )
//                    .put("required", new JSONArray().put("currencyName").put("currencyData"))
//                )
//            );
//    }
//
//    
//    public FunctionResponse getFunctionResponse(String prompt){
//
//        // ✅ 1. FAST LOCAL PARSE – NO GROQ NEEDED
//        String p = prompt.toLowerCase();
//
//        if(p.contains("price") ||
//           p.contains("market cap") ||
//           p.contains("marketcap") ||
//           p.contains("volume") ||
//           p.contains("rank")) {
//
//            FunctionResponse fr = new FunctionResponse();
//            fr.setCurrencyName(extractCoinName(prompt));
//            fr.setCurrenctData(fixAmbiguousField(prompt, "current_price"));
//            return fr;
//        }
//
//        // ✅ 2. Only if complex question → call GROQ
//        try {
//
//            String url="https://api.groq.com/openai/v1/chat/completions";
//
//            HttpHeaders headers=new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setBearerAuth(GROQ_API_KEY);
//
//            JSONArray messages=new JSONArray();
//
//            messages.put(new JSONObject().put("role","system")
//                .put("content",
//                "You are crypto assistant. " +
//                "Use getCoinDetails ONLY when user asks about a coin metric."));
//
//            messages.put(new JSONObject().put("role","user").put("content",prompt));
//
//            JSONObject body=new JSONObject();
//            body.put("model","llama-3.1-8b-instant");
//            body.put("messages",messages);
//            body.put("tools", new JSONArray().put(getGroqToolDefinition()));
//            body.put("tool_choice","auto");
//
//            String res = restTemplate.postForObject(
//                url,
//                new HttpEntity<>(body.toString(),headers),
//                String.class
//            );
//
//            return safeParseGroqResponse(res, prompt);
//
//        } catch(Exception e){
//
//            // ✅ 3. HARD FALLBACK – NEVER FAIL
//            FunctionResponse fr = new FunctionResponse();
//            fr.setCurrencyName(extractCoinName(prompt));
//            fr.setCurrenctData("current_price");
//            return fr;
//        }
//    }
//
//    
//    private FunctionResponse safeParseGroqResponse(String res, String prompt){
//
//        JSONObject msg = new JSONObject(res)
//                .getJSONArray("choices")
//                .getJSONObject(0)
//                .getJSONObject("message");
//
//        // ✅ If no tool call → fallback using keyword extraction
//        if(!msg.has("tool_calls")){
//
//            FunctionResponse fr = new FunctionResponse();
//
//            // Basic heuristic
//            fr.setCurrencyName(extractCoinName(prompt));
//            fr.setCurrenctData(fixAmbiguousField(prompt, "current_price"));
//
//            return fr;
//        }
//
//        JSONObject call = msg.getJSONArray("tool_calls")
//                            .getJSONObject(0)
//                            .getJSONObject("function");
//
//        JSONObject args = new JSONObject(call.getString("arguments"));
//
//        FunctionResponse fr = new FunctionResponse();
//        fr.setFunctionName(call.getString("name"));
//        fr.setCurrencyName(args.getString("currencyName"));
//        fr.setCurrenctData(args.getString("currencyData"));
//
//        return fr;
//    }
//
////    private String extractCoinName(String prompt){
////
////        prompt = prompt.toLowerCase();
////
////        if(prompt.contains("bitcoin")) return "bitcoin";
////        if(prompt.contains("ethereum")) return "ethereum";
////        if(prompt.contains("solana")) return "solana";
////        if(prompt.contains("dogecoin")) return "dogecoin";
////
////        return "bitcoin"; // default safe
////    }
//    
//    
//    private String extractCoinName(String prompt){
//
//        prompt = prompt.toLowerCase();
//
//        if(prompt.contains("btc")) return "bitcoin";
//        if(prompt.contains("bitcoin")) return "bitcoin";
//
//        if(prompt.contains("eth")) return "ethereum";
//        if(prompt.contains("ethereum")) return "ethereum";
//
//        if(prompt.contains("sol")) return "solana";
//        if(prompt.contains("solana")) return "solana";
//
//        if(prompt.contains("doge")) return "dogecoin";
//        if(prompt.contains("dogecoin")) return "dogecoin";
//
//        return "bitcoin"; // default safe
//    }
//
//
//    
//    public ApiResponse smartAsk(String prompt) {
//        try{
//            return getCoinDetails(prompt);
//        }catch(Exception e){
//            ApiResponse r = new ApiResponse();
//            r.setData("Service temporarily unavailable");
//            return r;
//        }
//    }
//
//
//    private String detectIntent(String prompt) {
//
//        String url = "https://api.groq.com/openai/v1/chat/completions";
//
//        HttpHeaders h = new HttpHeaders();
//        h.setContentType(MediaType.APPLICATION_JSON);
//        h.setBearerAuth(GROQ_API_KEY);
//
//        JSONObject body = new JSONObject()
//            .put("model","llama-3.1-8b-instant")
//            .put("temperature",0)
//            .put("messages", new JSONArray()
//                .put(new JSONObject().put("role","system")
//                    .put("content",
//                    "You are a classifier. Respond ONLY in JSON: " +
//                    "{\"intent\":\"CRYPTO\"} or {\"intent\":\"GENERAL\"}"))
//                .put(new JSONObject().put("role","user").put("content",prompt))
//            );
//
//        try {
//            String res = restTemplate.postForObject(
//                url,new HttpEntity<>(body.toString(),h),String.class);
//
//            return new JSONObject(res)
//                .getJSONArray("choices")
//                .getJSONObject(0)
//                .getJSONObject("message")
//                .getString("content");
//
//        } catch(Exception e) {
//            return "{\"intent\":\"GENERAL\"}";
//        }
//    }
//
//
//    private String extractLLMText(String res){
//        return new JSONObject(res)
//            .getJSONArray("choices").getJSONObject(0)
//            .getJSONObject("message").getString("content");
//    }
//
//    private String fixAmbiguousField(String prompt, String field) {
//
//        String p = prompt.toLowerCase();
//
//        // If user clearly wants rank — override model mistake
//        if (p.contains("rank") || p.contains("position")) {
//            return "rank";
//        }
//
//        // If user wants price
//        if (p.contains("price")) return "current_price";
//
//        // If user wants market cap
//        if (p.contains("market cap") || p.contains("marketcap")) return "market_cap";
//
//        // If user wants volume
//        if (p.contains("volume")) return "volume";
//
//        return field; // fallback to model
//    }
//
//
//    private JSONObject getTopCoinsTool() {
//        return new JSONObject()
//          .put("type","function")
//          .put("function", new JSONObject()
//            .put("name","getTopCoins")
//            .put("description","Return top N cryptocurrencies by market cap")
//            .put("parameters", new JSONObject()
//              .put("type","object")
//              .put("properties", new JSONObject()
//                .put("limit", new JSONObject().put("type","integer"))
//              )
//              .put("required", new JSONArray().put("limit"))
//            )
//          );
//    }
//
//	@Override
//	public ApiResponse ask(String q) {
//		return smartAsk(q);
//	}
//  
//}

import java.util.HashMap;
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

    // ==========================================
    // ENTRY POINT
    // ==========================================
    @Override
    public ApiResponse ask(String prompt){

        try {

            // 1. AI Understand Question
            Map<String,String> intent =
                understandQuestion(prompt);

            String coin  = intent.get("coin");
            String field = intent.get("field");

            // 2. Fetch Market Data
            CoinDTO dto = fetchCoin(coin);

            if(dto == null){
                return error(
                  "Sorry, I couldn’t find data for coin: "
                  + coin.toUpperCase()
                );
            }

            Object value = readValue(dto, field);

            // 3. Human Response via AI
            String human =
              makeHumanResponse(coin, field, value);

            ApiResponse res = new ApiResponse();
            res.setData(human);

            return res;

        } catch(Exception e){

            ApiResponse r = new ApiResponse();
            r.setData("Service temporarily unavailable");
            return r;
        }
    }

    // ==========================================
    // AI INTENT DETECTION
    // ==========================================
    private Map<String,String> understandQuestion(String q){

        String prompt = """
        You are crypto parser.
        Return ONLY JSON:

        {
          "coin": "coin id (like bitcoin, pepe, sui)",
          "field": "current_price | market_cap | volume | rank | high_24h | low_24h"
        }

        Question: %s
        """.formatted(q);

        String ai = callGroq(prompt);

        return parseJson(ai);
    }
    
    private Map<String,String> parseJson(String ai){

        try {

            JSONObject o = new JSONObject(ai);

            Map<String,String> m = new HashMap<>();

            m.put("coin",
                o.has("coin")
                  ? o.getString("coin")
                  : "bitcoin"
            );

            m.put("field",
                o.has("field")
                  ? o.getString("field")
                  : "current_price"
            );

            return m;

        } catch(Exception e){

            // 🔥 HARD FALLBACK
            Map<String,String> m = new HashMap<>();
            m.put("coin","bitcoin");
            m.put("field","current_price");

            return m;
        }
    }


    // ==========================================
    // GROQ CALL
    // ==========================================
    private String callGroq(String content){

        String url =
        "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBearerAuth(groqKey);

        JSONObject body = new JSONObject()
          .put("model","llama-3.1-8b-instant")
          .put("messages", new JSONArray().put(
             new JSONObject()
               .put("role","user")
               .put("content",content)
          ));

        String res =
          rest.postForObject(
            url,
            new HttpEntity<>(body.toString(),h),
            String.class
          );

        return new JSONObject(res)
          .getJSONArray("choices")
          .getJSONObject(0)
          .getJSONObject("message")
          .getString("content");
    }

    // ==========================================
    // FETCH DATA (ANY COIN)
    // ==========================================
    private CoinDTO fetchCoin(String coin){

        try{
            return fromCoinGecko(coin);
        }catch(Exception e){
            return fromCryptoCompare(coin);
        }
    }

    // ==========================================
    // COINGECKO
    // ==========================================
    private CoinDTO fromCoinGecko(String coin){

        String url =
        "https://api.coingecko.com/api/v3/coins/"
        + coin.toLowerCase();

        Map res = rest.getForObject(url, Map.class);

        Map market = (Map) res.get("market_data");

        CoinDTO d = new CoinDTO();

        d.setCurrentPrice(
          toDouble(((Map)market.get("current_price")).get("usd"))
        );

        d.setMarketCap(
          toDouble(((Map)market.get("market_cap")).get("usd"))
        );

        d.setTotalVolume(
          toDouble(((Map)market.get("total_volume")).get("usd"))
        );

        d.setHigh24h(
          toDouble(((Map)market.get("high_24h")).get("usd"))
        );

        d.setLow24h(
          toDouble(((Map)market.get("low_24h")).get("usd"))
        );

        d.setMarketCapRank(
          toDouble(res.get("market_cap_rank"))
        );

        return d;
    }

    // ==========================================
    // CRYPTOCOMPARE FALLBACK
    // ==========================================
    private CoinDTO fromCryptoCompare(String coin){

        String symbol = coin.toUpperCase();

        String url =
        "https://min-api.cryptocompare.com/data/pricemultifull?fsyms="
        + symbol + "&tsyms=USD";

        HttpHeaders h = new HttpHeaders();
        h.set("authorization","Apikey " + cryptoKey);

        var response =
        rest.exchange(url, HttpMethod.GET,
            new HttpEntity<>(h), Map.class);

        Map body = response.getBody();

        if(body == null || body.get("RAW") == null)
            return null;

        Map raw = (Map) body.get("RAW");

        if(raw.get(symbol) == null)
            return null;

        Map usd =
        (Map)((Map)raw.get(symbol)).get("USD");

        CoinDTO d = new CoinDTO();

        d.setCurrentPrice(toDouble(usd.get("PRICE")));
        d.setMarketCap(toDouble(usd.get("MKTCAP")));
        d.setTotalVolume(toDouble(usd.get("VOLUME24HOUR")));
        d.setMarketCapRank(toDouble(usd.get("MKTCAP_RANK")));

        return d;
    }

    // ==========================================
    // HUMAN RESPONSE
    // ==========================================
    private String makeHumanResponse(
        String coin, String field, Object value){

        String p = """
        Convert to natural sentence:

        Coin: %s
        Field: %s
        Value: %s
        """.formatted(coin, field, value);

        return callGroq(p);
    }

    // ==========================================
    private Object readValue(CoinDTO d,String f){

        return switch(f){

            case "current_price" -> d.getCurrentPrice();

            case "market_cap" -> d.getMarketCap();

            case "volume" -> d.getTotalVolume();

            case "rank" -> d.getMarketCapRank();

            case "high_24h" -> d.getHigh24h();

            case "low_24h" -> d.getLow24h();

            default -> d.getCurrentPrice();
        };
    }

    private double toDouble(Object v){
        if(v==null) return 0;
        return Double.parseDouble(v.toString());
    }

    private ApiResponse error(String m){
        ApiResponse r = new ApiResponse();
        r.setData(m);
        return r;
    }
}
