package com.trade.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//@Entity
//@Table(name="coins")
//public class Bitcoin {
//
//    @Id
//    private String id;
//
//    private String symbol;
//    private String name;
//    private String image;
//
//    private Double currentPrice;
//    private Long marketCap;
//    private Long totalVolume;
//    private Integer marketCapRank;
//
//    private Double marketCapChange24h;
//    private Double marketCapChangePercentage24h;
//
//    private Double priceChange24h;
//    private Double priceChangePercentage24h;
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getSymbol() {
//		return symbol;
//	}
//	public void setSymbol(String symbol) {
//		this.symbol = symbol;
//	}
//	public String getName() {
//		return name;
//	}
//	public void setName(String name) {
//		this.name = name;
//	}
//	public String getImage() {
//		return image;
//	}
//	public void setImage(String image) {
//		this.image = image;
//	}
//	public Double getCurrentPrice() {
//		return currentPrice;
//	}
//	public void setCurrentPrice(Double currentPrice) {
//		this.currentPrice = currentPrice;
//	}
//	public Long getMarketCap() {
//		return marketCap;
//	}
//	public void setMarketCap(Long marketCap) {
//		this.marketCap = marketCap;
//	}
//	public Long getTotalVolume() {
//		return totalVolume;
//	}
//	public void setTotalVolume(Long totalVolume) {
//		this.totalVolume = totalVolume;
//	}
//	public Integer getMarketCapRank() {
//		return marketCapRank;
//	}
//	public void setMarketCapRank(Integer marketCapRank) {
//		this.marketCapRank = marketCapRank;
//	}
//	public Double getMarketCapChange24h() {
//		return marketCapChange24h;
//	}
//	public void setMarketCapChange24h(Double marketCapChange24h) {
//		this.marketCapChange24h = marketCapChange24h;
//	}
//	public Double getMarketCapChangePercentage24h() {
//		return marketCapChangePercentage24h;
//	}
//	public void setMarketCapChangePercentage24h(Double marketCapChangePercentage24h) {
//		this.marketCapChangePercentage24h = marketCapChangePercentage24h;
//	}
//	public Double getPriceChange24h() {
//		return priceChange24h;
//	}
//	public void setPriceChange24h(Double priceChange24h) {
//		this.priceChange24h = priceChange24h;
//	}
//	public Double getPriceChangePercentage24h() {
//		return priceChangePercentage24h;
//	}
//	public void setPriceChangePercentage24h(Double priceChangePercentage24h) {
//		this.priceChangePercentage24h = priceChangePercentage24h;
//	}
//    
//    
//}
//









@Document("coins")
public class Bitcoin {

    @Id
    private String id;

    private String symbol;
    private String name;
    private String image;

    private Double currentPrice;
    private Long marketCap;
    private Long totalVolume;
    private Integer marketCapRank;

    private Double marketCapChange24h;
    private Double marketCapChangePercentage24h;
    private Double priceChange24h;
    private Double priceChangePercentage24h;
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Double getCurrentPrice() {
		return currentPrice;
	}
	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}
	public Long getMarketCap() {
		return marketCap;
	}
	public void setMarketCap(Long marketCap) {
		this.marketCap = marketCap;
	}
	public Long getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(Long totalVolume) {
		this.totalVolume = totalVolume;
	}
	public Integer getMarketCapRank() {
		return marketCapRank;
	}
	public void setMarketCapRank(Integer marketCapRank) {
		this.marketCapRank = marketCapRank;
	}
	public Double getMarketCapChange24h() {
		return marketCapChange24h;
	}
	public void setMarketCapChange24h(Double marketCapChange24h) {
		this.marketCapChange24h = marketCapChange24h;
	}
	public Double getMarketCapChangePercentage24h() {
		return marketCapChangePercentage24h;
	}
	public void setMarketCapChangePercentage24h(Double marketCapChangePercentage24h) {
		this.marketCapChangePercentage24h = marketCapChangePercentage24h;
	}
	public Double getPriceChange24h() {
		return priceChange24h;
	}
	public void setPriceChange24h(Double priceChange24h) {
		this.priceChange24h = priceChange24h;
	}
	public Double getPriceChangePercentage24h() {
		return priceChangePercentage24h;
	}
	public void setPriceChangePercentage24h(Double priceChangePercentage24h) {
		this.priceChangePercentage24h = priceChangePercentage24h;
	}
	public Bitcoin(String id, String symbol, String name, String image, Double currentPrice, Long marketCap,
			Long totalVolume, Integer marketCapRank, Double marketCapChange24h, Double marketCapChangePercentage24h,
			Double priceChange24h, Double priceChangePercentage24h) {
		super();
		this.id = id;
		this.symbol = symbol;
		this.name = name;
		this.image = image;
		this.currentPrice = currentPrice;
		this.marketCap = marketCap;
		this.totalVolume = totalVolume;
		this.marketCapRank = marketCapRank;
		this.marketCapChange24h = marketCapChange24h;
		this.marketCapChangePercentage24h = marketCapChangePercentage24h;
		this.priceChange24h = priceChange24h;
		this.priceChangePercentage24h = priceChangePercentage24h;
	}
	public Bitcoin() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
