package com.trade.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

//@Entity
@Document("twoFactor")
public class TwoFactorAuth {

//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Id
	private Long id;
	
	 private String secretKey;
	 private boolean enabled;
	 
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public TwoFactorAuth(Long id, String secretKey, boolean enabled) {
		this.id = id;
		this.secretKey = secretKey;
		this.enabled = enabled;
	}
	
	public TwoFactorAuth() {
		// TODO Auto-generated constructor stub
	}
	 
	 
}
