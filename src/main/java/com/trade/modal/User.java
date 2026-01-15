package com.trade.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trade.domain.USER_ROLE;

//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.trade.domain.USER_ROLE;
//
//import jakarta.persistence.Embedded;
//import jakarta.persistence.Entity;
//import jakarta.persistence.EnumType;
//import jakarta.persistence.Enumerated;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//
//@Entity
//@Table(name = "users")
//public class User {
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;
//	
//	private String fullName;
//	private String email;
//	
//	private String password;
//	
//	@Enumerated(EnumType.STRING)
//	private USER_ROLE role = USER_ROLE.USER;
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public String getFullName() {
//		return fullName;
//	}
//
//	public void setFullName(String fullName) {
//		this.fullName = fullName;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public USER_ROLE getRole() {
//		return role;
//	}
//
//	public void setRole(USER_ROLE role) {
//		this.role = role;
//	}
//
//	public User(Long id, String fullName, String email, String password, USER_ROLE role) {
//		super();
//		this.id = id;
//		this.fullName = fullName;
//		this.email = email;
//		this.password = password;
//		this.role = role;
//	}
//
//	public User() {
//		super();
//		// TODO Auto-generated constructor stub
//	}
//	
//	
//}













@Document("users")
public class User {

    @Id
    private Long id;

    private String fullName;
    private String email;
    
    @JsonIgnore
    private String password;
    private USER_ROLE role = USER_ROLE.USER;
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public USER_ROLE getRole() {
		return role;
	}
	public void setRole(USER_ROLE role) {
		this.role = role;
	}
	public User(Long id, String fullName, String email, String password, USER_ROLE role) {
		super();
		this.id = id;
		this.fullName = fullName;
		this.email = email;
		this.password = password;
		this.role = role;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
    
    
}
