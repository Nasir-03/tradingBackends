package com.trade.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

//@Entity
@Document("paymentDetails")
public class PaymentDetails {

//	@Id
//	@GeneratedValue(strategy = GenerationType.AUTO)
	@Id
	private Long id;
	
	private String accountNumber;
	
	private String accountHolderName;
	
	private String ifsc;
	
	private String bankName;
	
	private Long userId;
	
//	@OneToOne
//	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	
	@DBRef
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private User user;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountHolderName() {
		return accountHolderName;
	}

	public void setAccountHolderName(String accountHolderName) {
		this.accountHolderName = accountHolderName;
	}

	public String getIfsc() {
		return ifsc;
	}

	public void setIfsc(String ifsc) {
		this.ifsc = ifsc;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public PaymentDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PaymentDetails(Long id, String accountNumber, String accountHolderName, String ifsc, String bankName,
			Long userId, User user) {
		super();
		this.id = id;
		this.accountNumber = accountNumber;
		this.accountHolderName = accountHolderName;
		this.ifsc = ifsc;
		this.bankName = bankName;
		this.userId = userId;
		this.user = user;
	}
	
	
}
