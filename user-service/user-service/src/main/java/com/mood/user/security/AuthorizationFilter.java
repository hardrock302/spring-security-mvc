package com.mood.user.security;

import java.io.IOException;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mood.user.entity.AppUser;
import com.mood.user.entity.Constants;
import com.mood.user.entity.Employee;
import com.mood.user.repository.EmployeeRepository;
import com.mood.user.repository.UserRepository;


public class AuthorizationFilter extends OncePerRequestFilter{

	private String jwtSecret;
	private UserRepository userRepository;
	private EmployeeRepository employeeRepository;
	


	public AuthorizationFilter(String jwtSecret, UserRepository userRepository, EmployeeRepository employeeRepository) {
		super();
		this.jwtSecret = jwtSecret;
		this.userRepository = userRepository;
		this.employeeRepository = employeeRepository;
	}



	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			String header = request.getHeader(Constants.AUTH_HEADER);
			//Signup request has no auth token so ignore it
			if (header == null || !header.startsWith(Constants.BEARER)) {
				filterChain.doFilter(request, response);
				return;
			}
		
		String tokenString = header.replace("Bearer ", "");
		String user = JWT.require(Algorithm.HMAC512(jwtSecret)).build().verify(tokenString)
		.getSubject();
		Authentication authentication = null;
		AppUser appUser = userRepository.findByEmail(user);
		if (appUser != null) {
			authentication = new UsernamePasswordAuthenticationToken(user, null, appUser.getAuthorities());
		} else {
			//look for employee
			Employee employee = employeeRepository.findByEmail(user);
			authentication = new UsernamePasswordAuthenticationToken(user, null, employee.getAuthorities());
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}

}
