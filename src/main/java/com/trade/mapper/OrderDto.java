package com.trade.mapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.trade.domain.OrderStatus;
import com.trade.domain.OrderType;
import com.trade.modal.OrderItem;
import com.trade.modal.User;

public class OrderDto {

	private User user;

	private OrderType orderType;

	private BigDecimal price;

	private LocalDateTime timeStamp = LocalDateTime.now();

	private OrderStatus status;

	private OrderItem item;
	
	private Long userId;

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

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public OrderItem getItem() {
		return item;
	}

	public void setItem(OrderItem item) {
		this.item = item;
	}

	public OrderDto(User user, OrderType orderType, BigDecimal price, LocalDateTime timeStamp, OrderStatus status,
			OrderItem item) {
		this.user = user;
		this.orderType = orderType;
		this.price = price;
		this.timeStamp = timeStamp;
		this.status = status;
		this.item = item;
	}

}
