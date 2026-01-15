package com.trade.modal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.trade.domain.OrderStatus;
import com.trade.domain.OrderType;

//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import com.trade.domain.OrderStatus;
//import com.trade.domain.OrderType;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.OneToOne;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "orders")
//public class Order {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//	
//	@ManyToOne
//	private User user;
//	
//	@Column(nullable = false)
//	private OrderType orderType;
//	
//	@Column(nullable = false)
//	private BigDecimal price;
//	
//	private LocalDateTime timeStamp = LocalDateTime.now();
//	
//	@Column(nullable = false)
//	private OrderStatus status;
//	
//	@OneToOne(mappedBy = "order")
//	private OrderItem item;
//	
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public User getUser() {
//		return user;
//	}
//	public void setUser(User user) {
//		this.user = user;
//	}
//	public OrderType getOrderType() {
//		return orderType;
//	}
//	public void setOrderType(OrderType orderType) {
//		this.orderType = orderType;
//	}
//	public BigDecimal getPrice() {
//		return price;
//	}
//	public void setPrice(BigDecimal price) {
//		this.price = price;
//	}
//	public LocalDateTime getTimeStamp() {
//		return timeStamp;
//	}
//	public void setTimeStamp(LocalDateTime timeStamp) {
//		this.timeStamp = timeStamp;
//	}
//	public OrderStatus getStatus() {
//		return status;
//	}
//	public void setStatus(OrderStatus status) {
//		this.status = status;
//	}
//	public OrderItem getItem() {
//		return item;
//	}
//	public void setItem(OrderItem item) {
//		this.item = item;
//	}
//	public Order(User user, OrderType orderType, BigDecimal price, LocalDateTime timeStamp, OrderStatus status,
//			OrderItem item) {
//		this.user = user;
//		this.orderType = orderType;
//		this.price = price;
//		this.timeStamp = timeStamp;
//		this.status = status;
//		this.item = item;
//	}
//	
//	public Order() {
//		
//	}
//	
//}










@Document("orders")
public class Order {

    @Id
    private Long id;

    @DBRef
    @JsonIgnoreProperties({"orders","password","wallet"})
    private User user;

    private OrderType orderType;
    private BigDecimal price;
    private LocalDateTime timeStamp = LocalDateTime.now();
    private OrderStatus status;

    @DBRef
    @JsonManagedReference
    private OrderItem item;
    

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

	public Order(Long id, User user, OrderType orderType, BigDecimal price, LocalDateTime timeStamp, OrderStatus status,
			OrderItem item) {
		this.id = id;
		this.user = user;
		this.orderType = orderType;
		this.price = price;
		this.timeStamp = timeStamp;
		this.status = status;
		this.item = item;
	}

	public Order() {
		super();
		// TODO Auto-generated constructor stub
	}
    
	
    
}
