package com.trade.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

//
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//
//@Entity
//public class Asset {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//	private double quantity;
//	private double buyPrice;
//	
////	  @ManyToOne
////	    @JsonIgnoreProperties({"assets"})
////	    private Bitcoin coin;
//	
//	@ManyToOne(optional = false)
//	@JoinColumn(name = "coin_id", referencedColumnName = "id")
//	private Bitcoin coin;
//
//
//	    @ManyToOne
//	    @JsonIgnoreProperties({"assets","password","wallet"})
//	    private User user;
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public double getQuantity() {
//		return quantity;
//	}
//
//	public void setQuantity(double quantity) {
//		this.quantity = quantity;
//	}
//
//	public double getBuyPrice() {
//		return buyPrice;
//	}
//
//	public void setBuyPrice(double buyPrice) {
//		this.buyPrice = buyPrice;
//	}
//
//	public Bitcoin getCoin() {
//		return coin;
//	}
//
//	public void setCoin(Bitcoin coin) {
//		this.coin = coin;
//	}
//
//	public User getUser() {
//		return user;
//	}
//
//	public void setUser(User user) {
//		this.user = user;
//	}
//	
//	
//}









@Document("assets")
public class Asset {

    @Id
    private Long id;

    private double quantity;
    private double buyPrice;
    
    private Long userId;
    
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getCoinId() {
		return coinId;
	}

	public void setCoinId(String coinId) {
		this.coinId = coinId;
	}

	private String coinId; 

    @DBRef
    private Bitcoin coin;

    @DBRef
    private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public Bitcoin getCoin() {
		return coin;
	}

	public void setCoin(Bitcoin coin) {
		this.coin = coin;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
    
    
}
