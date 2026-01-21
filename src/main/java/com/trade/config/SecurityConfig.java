//package com.trade.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//import org.springframework.stereotype.Component;
//import org.springframework.web.cors.CorsConfigurationSource;
//
//import com.trade.repositoy.UserRepository;
//
//@Component
//public class SecurityConfig {
//
//    private final UserRepository userRepository;
//    private final JwtTokenValidator jwtTokenValidator;
//
//    SecurityConfig(UserRepository userRepository,JwtTokenValidator jwtTokenValidator) {
//        this.userRepository = userRepository;
//        this.jwtTokenValidator = jwtTokenValidator;
//    }
//
//	@Bean
//	 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//	        http
//	            .csrf(csrf -> csrf.disable())
//	            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//	            .sessionManagement(session -> 
//	                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//	            )
//	            .authorizeHttpRequests(auth -> auth
//	                .requestMatchers("/auth/**").permitAll()
//	                .anyRequest().authenticated()
//	            )
//	            .addFilterBefore(jwtTokenValidator, BasicAuthenticationFilter.class);
//
//	        return http.build();
//	    }
//	
//	public CorsConfigurationSource corsConfigurationSource() {
//		return null;
//	}
//	
//	@Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//}







package com.trade.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtTokenValidator jwtTokenValidator) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/coins/**", "/chatbot/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenValidator, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
