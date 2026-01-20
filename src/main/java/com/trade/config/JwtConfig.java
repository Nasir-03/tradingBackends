package com.trade.config;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtConfig {

	private String SECRET_KEY = "anhretnbfgter423vbfgter@ewr4366terff4432";
	
	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateToken(UserDetails userDetails) {
		CustomUserDetails users = (CustomUserDetails)userDetails;
	    Map<String, Object> claims = new HashMap<>();
	    
	    claims.put("fullName", users.getFullName());
	    claims.put("role", users.getRole());
	    claims.put("email", users.getEmail());
	    
	    return Jwts.builder()
	    		.subject(userDetails.getUsername())
	    		.claims(claims)
	    		.issuedAt(new Date())
	    		.expiration(new Date(System.currentTimeMillis() +1000L * 60 * 60 * 24 * 7))
	    		.signWith(getSecretKey())
	    		.compact();
	}
	
	private Claims extractAllClaims(String token) {
		 return Jwts.parser()
	                .verifyWith(getSecretKey())
	                .build()
	                .parseSignedClaims(token)
	                .getPayload();
	}
	
	public String getUserNameFromToken(String token) {
		return extractAllClaims(token).getSubject();
	}
	
	private boolean isTokenExpired(String token) {
		Date expirationDate = extractAllClaims(token).getExpiration();
		return expirationDate.before(new Date());
	}
	
	public boolean validateToken(String token, String userName) {
	    final String extractedUsername = getUserNameFromToken(token);
	    return (userName.equals(extractedUsername) && !isTokenExpired(token));
	}

}
