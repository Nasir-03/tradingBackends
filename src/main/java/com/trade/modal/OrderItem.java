package com.trade.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonBackReference;

//
//import java.security.PrivateKey;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToOne;
//
//@Entity
//public class OrderItem {
//
//	public OrderItem() {
//		
//	}
//	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//	
//	private double quantity;
//	
////	@ManyToOne
////	private Bitcoin coin;
//	@ManyToOne
//	@JoinColumn(name = "coin_id", referencedColumnName = "id")
//	@JsonIgnoreProperties("assets")
//	private Bitcoin coin;
//
//	
//	private double buyPrice;
//	
//	private double sellPrice;
//	
//	@JsonIgnore
//	@OneToOne
//	private Order order;
//	
//	public OrderItem(Long id, double quantity, Bitcoin coin, double buyPrice, double sellPrice, Order order) {
//		super();
//		this.id = id;
//		this.quantity = quantity;
//		this.coin = coin;
//		this.buyPrice = buyPrice;
//		this.sellPrice = sellPrice;
//		this.order = order;
//	}
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public double getQuantity() {
//		return quantity;
//	}
//	public void setQuantity(double quantity) {
//		this.quantity = quantity;
//	}
//	public Bitcoin getCoin() {
//		return coin;
//	}
//	public void setCoin(Bitcoin coin) {
//		this.coin = coin;
//	}
//	public double getBuyPrice() {
//		return buyPrice;
//	}
//	public void setBuyPrice(double buyPrice) {
//		this.buyPrice = buyPrice;
//	}
//	public double getSellPrice() {
//		return sellPrice;
//	}
//	public void setSellPrice(double sellPrice) {
//		this.sellPrice = sellPrice;
//	}
//	public Order getOrder() {
//		return order;
//	}
//	public void setOrder(Order order) {
//		this.order = order;
//	}
//	
//	
//}













@Document("order_items")
public class OrderItem {

    @Id
    private Long id;

    private double quantity;

    @DBRef
    private Bitcoin coin;

    private double buyPrice;
    private double sellPrice;

    @DBRef
    @JsonBackReference
    private Order order;

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

	public Bitcoin getCoin() {
		return coin;
	}

	public void setCoin(Bitcoin coin) {
		this.coin = coin;
	}

	public double getBuyPrice() {
		return buyPrice;
	}

	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
	}

	public double getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
    
    
}
