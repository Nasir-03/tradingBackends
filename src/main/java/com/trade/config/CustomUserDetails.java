//package com.trade.config;
//
//import java.util.Collection;
//import java.util.List;
//
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import com.trade.domain.USER_ROLE;
//
//public class CustomUserDetails implements UserDetails{
//
//	private String fullName;
//	private String email;
//	private USER_ROLE role;
//	private String password;
//	
//	public CustomUserDetails(String fullName, String email, String password, USER_ROLE role) {
//	    this.fullName = fullName;
//	    this.email = email;
//	    this.password = password;
//	    this.role = role;
//	}
//
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
//	public USER_ROLE getRole() {
//		return role;
//	}
//
//	public void setRole(USER_ROLE role) {
//		this.role = role;
//	}
//
//
//	@Override
//	public Collection<? extends GrantedAuthority> getAuthorities() {
//	    return List.of();
//	}
//
//
//	@Override
//	public String getPassword() {
//		// TODO Auto-generated method stub
//		return password;
//	}
//
//	@Override
//	public String getUsername() {
//		// TODO Auto-generated method stub
//		return email;
//	}
//
//	@Override
//    public boolean isAccountNonExpired() {
//        return true; // change if you track account expiry
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true; // change if you track account locks
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true; // change if you track password expiry
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true; // change if you track active/inactive users
//    }
//
//}


















package com.trade.config;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.trade.domain.USER_ROLE;

public class CustomUserDetails implements UserDetails {

    private String fullName;
    private String email;
    private USER_ROLE role;
    private String password;

    public CustomUserDetails(String fullName, String email, String password, USER_ROLE role) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public USER_ROLE getRole() { return role; }
    public void setRole(USER_ROLE role) { this.role = role; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // convert enum role to a granted authority, adapt prefix if needed
        if (role == null) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return email; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
