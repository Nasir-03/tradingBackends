package com.trade.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.trade.domain.PaymentMethod;
import com.trade.domain.PaymentOrderStatus;

//@Entity
@Document("paymentOrder")
public class PaymentOrder {

//	@GeneratedValue(strategy = GenerationType.AUTO)
	
	@Id
	private Long id;
	
	private Long amount;
	
	private PaymentOrderStatus status;
	
	private PaymentMethod paymentMethod;
	
//	@ManyToOne
	
	@DBRef
	private User user;
	
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

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public PaymentOrderStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentOrderStatus status) {
		this.status = status;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PaymentOrder(Long id, Long amount, PaymentOrderStatus status, PaymentMethod paymentMethod, User user,
			Long userId) {
		this.id = id;
		this.amount = amount;
		this.status = status;
		this.paymentMethod = paymentMethod;
		this.user = user;
		this.userId = userId;
	}

	public PaymentOrder() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
