package com.mood.user.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mood.user.entity.AppUser;
import com.mood.user.repository.EmployeeRepository;
import com.mood.user.repository.UserRepository;
import com.mood.user.service.UserService;



public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	
	private final int EXPIRY_TIME_IN_SECONDS = 7200000;
	private Logger logger = Logger.getLogger(getClass().getName());

	private String token;
	private UserRepository userRepository;
	private EmployeeRepository employeeRepository;
	
	public AuthenticationFilter(MoodAuthenticationManager authenticationManager, String jwtSecret) {
		this.setAuthenticationManager(authenticationManager);
		this.token = jwtSecret;
	}

	public AuthenticationFilter() {
		super();
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
		

		try {
			AppUser user = new ObjectMapper().readValue(request.getInputStream(), AppUser.class);
			Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
			return this.getAuthenticationManager().authenticate(authentication);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			throw new RuntimeException("The request could not be read.");
		}
		return null;
		
	}
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		logger.info("Login unsuccessful");
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		super.unsuccessfulAuthentication(request, response, failed);
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String token = JWT.create().withSubject(authResult.getName()).withExpiresAt(new Date(System.currentTimeMillis() + EXPIRY_TIME_IN_SECONDS))
				.sign(Algorithm.HMAC512(this.token));
		response.addHeader("Authorization", "Bearer " + token);
	}

	@Override
	@Autowired
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
	    super.setAuthenticationManager(authenticationManager);
	}
	
}
