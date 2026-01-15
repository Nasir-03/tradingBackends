package com.trade.modal;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.trade.domain.WithdrawlStatus;




@Document("withdrawls")
public class Withdrawl {

    @Id
    private Long id;

    private WithdrawlStatus withdrawlStatus;
    private Long amount;
    
    private Long userId;

    @DBRef
    private User user;
    private LocalDateTime date = LocalDateTime.now();
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public WithdrawlStatus getWithdrawlStatus() {
		return withdrawlStatus;
	}
	public void setWithdrawlStatus(WithdrawlStatus withdrawlStatus) {
		this.withdrawlStatus = withdrawlStatus;
	}
	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public LocalDateTime getDate() {
		return date;
	}
	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	public Withdrawl(Long id, WithdrawlStatus withdrawlStatus, Long amount, Long userId, User user,
			LocalDateTime date) {
		super();
		this.id = id;
		this.withdrawlStatus = withdrawlStatus;
		this.amount = amount;
		this.userId = userId;
		this.user = user;
		this.date = date;
	}
	public Withdrawl() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
