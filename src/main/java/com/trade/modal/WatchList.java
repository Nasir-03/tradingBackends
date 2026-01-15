package com.trade.modal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;














@Document("watchlists")
public class WatchList {

    @Id
    private Long id;

    @DBRef
    private User user;

    @DBRef
    private List<Bitcoin> coins = new ArrayList<>();
    
    private Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Bitcoin> getCoins() {
		return coins;
	}

	public void setCoins(List<Bitcoin> coins) {
		this.coins = coins;
	}

	public WatchList(Long id, User user, List<Bitcoin> coins, Long userId) {
		super();
		this.id = id;
		this.user = user;
		this.coins = coins;
		this.userId = userId;
	}

	public WatchList() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}

