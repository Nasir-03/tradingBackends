package com.trade.DTO;

import com.trade.modal.Bitcoin;
import com.trade.modal.User;

public class AssetDTO {

	private Long id;
	private double quantity;
	private double buyPrice;
	private Bitcoin coin;
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
	public AssetDTO(Long id, double quantity, double buyPrice, Bitcoin coin, User user) {
		super();
		this.id = id;
		this.quantity = quantity;
		this.buyPrice = buyPrice;
		this.coin = coin;
		this.user = user;
	}
	public AssetDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
