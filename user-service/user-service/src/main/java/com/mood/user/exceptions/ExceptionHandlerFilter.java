package com.mood.user.exceptions;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;

public class ExceptionHandlerFilter  extends OncePerRequestFilter{

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException, JWTVerificationException {
		try {
			filterChain.doFilter(request, response);
		} catch (ServletException e) {
			System.out.println(e.getMessage());
		} catch (JWTVerificationException e) {
			response = createResponse(response, HttpServletResponse.SC_FORBIDDEN, "Invalid JWT");
		} catch (RuntimeException e) {
			response = createResponse(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} 
		
	}

	private HttpServletResponse createResponse(HttpServletResponse response, int statusCode, String messageBody) throws IOException {
		response.setStatus(statusCode);
		response.getWriter().write(messageBody);
		response.getWriter().flush();
		return response;
	}
}
