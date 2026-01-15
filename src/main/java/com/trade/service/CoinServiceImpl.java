package com.trade.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.modal.Bitcoin;
import com.trade.repositoy.CoinRepository;


@Service
public class CoinServiceImpl implements CoinService {

    private final CoinRepository coinRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public CoinServiceImpl(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    private static final String BASE_IMAGE = "https://www.cryptocompare.com";

    // ---------------- TOP 10 COINS ----------------
    @Cacheable(value = "coinList", key = "#page")
    @Override
    public List<Bitcoin> getCoinList(int page) {

        String url = "https://min-api.cryptocompare.com/data/top/mktcapfull?limit=10&page=" + page + "&tsym=USD";

        try {
            JsonNode data = mapper.readTree(restTemplate.getForObject(url, String.class)).path("Data");

            List<Bitcoin> list = new ArrayList<>();

            for (JsonNode node : data) {

                JsonNode info = node.path("CoinInfo");
                JsonNode usd  = node.path("RAW").path("USD");

                Bitcoin b = new Bitcoin();

                b.setId(info.path("Name").asText());
                b.setSymbol(info.path("Name").asText());
                b.setName(info.path("FullName").asText());
                b.setImage(BASE_IMAGE + info.path("ImageUrl").asText());

                b.setCurrentPrice(usd.path("PRICE").asDouble());
                b.setMarketCap(usd.path("MKTCAP").asLong());
                b.setTotalVolume(usd.path("VOLUME24HOUR").asLong());
                b.setMarketCapRank(usd.path("MKTCAP_RANK").asInt());

                b.setMarketCapChange24h(usd.path("MKTCAPCHANGE24HOUR").asDouble());
                b.setMarketCapChangePercentage24h(usd.path("MKTCAPCHANGEPCT24HOUR").asDouble());

                b.setPriceChange24h(usd.path("CHANGE24HOUR").asDouble());
                b.setPriceChangePercentage24h(usd.path("CHANGEPCT24HOUR").asDouble());

                list.add(b);
            }

            return list;

        } catch (Exception e) {
            throw new RuntimeException("CryptoCompare error", e);
        }
    }

    // ---------------- MARKET CHART ----------------
    @Cacheable(value="marketChart", key="#coin + '-' + #days")
    @Override
    public String getMarketChart(String coin, int days) {
        return restTemplate.getForObject(
                "https://min-api.cryptocompare.com/data/v2/histoday?fsym=" +
                        coin.toUpperCase() + "&tsym=USD&limit=" + days, String.class);
    }

    // ---------------- COIN DETAILS ---------------- 
    @Override
    public Bitcoin getcoinDetails(String coin) {

        String symbol = coin.toUpperCase();

        // 1. First check DB cache
        Optional<Bitcoin> dbCoin = coinRepository.findById(symbol);
        if (dbCoin.isPresent()) {
            return dbCoin.get();
        }

        try {
            // 2. Fetch from CryptoCompare (supports ANY coin)
            String url = "https://min-api.cryptocompare.com/data/pricemultifull?fsyms=" + symbol + "&tsyms=USD";
            JsonNode root = mapper.readTree(restTemplate.getForObject(url, String.class));

            JsonNode coinInfo = root.path("RAW").path(symbol).path("USD");

            if (coinInfo.isMissingNode()) {
                throw new RuntimeException("Coin not found in CryptoCompare");
            }

            Bitcoin b = new Bitcoin();
            b.setId(symbol);
            b.setSymbol(symbol);
            b.setName(symbol);
            b.setImage(null);

            b.setCurrentPrice(coinInfo.path("PRICE").asDouble());
            b.setMarketCap(coinInfo.path("MKTCAP").asLong());
            b.setTotalVolume(coinInfo.path("VOLUME24HOUR").asLong());

            b.setMarketCapChange24h(coinInfo.path("MKTCAPCHANGE24HOUR").asDouble());
            b.setMarketCapChangePercentage24h(coinInfo.path("MKTCAPCHANGEPCT24HOUR").asDouble());

            b.setPriceChange24h(coinInfo.path("CHANGE24HOUR").asDouble());
            b.setPriceChangePercentage24h(coinInfo.path("CHANGEPCT24HOUR").asDouble());

            return coinRepository.save(b);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch coin details", e);
        }
    }
    
    @Cacheable("top50")
    @Override
    public String getTop50CoinsByMarketCapRank() {
        return restTemplate.getForObject(
                "https://min-api.cryptocompare.com/data/top/mktcapfull?limit=50&tsym=USD", String.class);
    }

    @Cacheable("trending")
    @Override
    public String getTradingCoins() {
        return restTemplate.getForObject(
                "https://min-api.cryptocompare.com/data/top/totalvolfull?limit=10&tsym=USD", String.class);
    }

    @Cacheable(value="search", key="#keyword")
    @Override
    public String searchCoin(String keyword) {
        return restTemplate.getForObject(
                "https://min-api.cryptocompare.com/data/search?q=" + keyword, String.class);
    }

    @Override
    public Bitcoin findById(String coinId) throws Exception {
        return coinRepository.findById(coinId)
                .orElseThrow(() -> new Exception("Coin not found"));
    }
    
//    @Transactional
//    public Bitcoin ensureCoinExists(Bitcoin apiCoin) {
//        return coinRepository.findById(apiCoin.getId())
//            .orElseThrow(() -> coinRepository.saveAndFlush(apiCoin)); // 🔥 flush now
//    }

    public Bitcoin ensureCoinExists(Bitcoin apiCoin) {
        return coinRepository.findById(apiCoin.getId())
            .orElseGet(() -> coinRepository.save(apiCoin));
    }

	@Override
	public Bitcoin getCoinFromDB(String coinId) {
		 return coinRepository.findById(coinId.toUpperCase())
	                .orElseThrow(() -> new RuntimeException("Coin not supported for trading"));
	}
    
//    public Bitcoin getCoinFromDB(String coinId) {
//        return coinRepository.findById(coinId.toUpperCase())
//                .orElseThrow(() -> new RuntimeException("Coin not supported for trading"));
//    }


}

