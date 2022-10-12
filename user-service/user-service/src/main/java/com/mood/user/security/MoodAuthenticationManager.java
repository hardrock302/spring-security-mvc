package com.mood.user.security;


import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import com.mood.user.entity.AppUser;
import com.mood.user.entity.AppUser.UserStatus;
import com.mood.user.entity.Employee;
import com.mood.user.exceptions.AccountLockedException;
import com.mood.user.exceptions.UserNotFoundException;
import com.mood.user.repository.EmployeeRepository;
import com.mood.user.repository.UserRepository;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class MoodAuthenticationManager implements AuthenticationManager {



	private Logger logger = Logger.getLogger(getClass().getName());
	
	private UserRepository userRepository;
	private EmployeeRepository employeeRepository;
	private BCryptPasswordEncoder passwordEncoder;
	private String decryptedPassword;
	 
	
	public MoodAuthenticationManager(UserRepository userRepository, EmployeeRepository employeeRepository,
			BCryptPasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.employeeRepository = employeeRepository;
		this.passwordEncoder = passwordEncoder;
	}


	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		AppUser user = userRepository.findByEmail(authentication.getName());
		if (user != null) {
			if (user.getFailedLogins() >= 3) {
				throw new AccountLockedException("Account is locked due to failed logins.");
				
			}
			decryptedPassword = decryptBase64String(authentication.getCredentials().toString());
			if (user.getEnabled() == UserStatus.INACTIVE) {
				throw new BadCredentialsException("Account is not active.");
			} else if (!passwordEncoder.matches(decryptedPassword, user.getPassword()) && user.getEnabled() == UserStatus.ACTIVE) {
				int failedLogins = user.getFailedLogins()+1;
				userRepository.incrementFailedLogins(user.getEmail(), failedLogins);
				throw new BadCredentialsException("Invalid Credentials");
			} else if (passwordEncoder.matches(decryptedPassword, user.getPassword()) && user.getEnabled() == UserStatus.ACTIVE ){
				return new UsernamePasswordAuthenticationToken(authentication.getName(), user.getPassword());
			} else {
				throw new BadCredentialsException("Invalid Credentials");
			}
		} else {
			logger.error(authentication.getName() + " not found in users");
			Employee employee = employeeRepository.findByEmail(authentication.getName());
			if (employee == null) {
				throw new UserNotFoundException(authentication.getName() + " not found in employees.");
			}
			else if (employee.getFailedLogins() > 3) {
				throw new AccountLockedException("Account is locked due to failed logins.");
			} else if (employee.getEnabled() == UserStatus.INACTIVE) {
				throw new AccountLockedException("Employee account must be active to perform tasks.");
			} else if (!passwordEncoder.matches(decryptedPassword, employee.getPassword()) && employee.getEnabled() == UserStatus.ACTIVE) {
				int failedLogins = employee.getFailedLogins()+1;
				employeeRepository.incrementFailedLogins(employee.getEmail(), failedLogins);
				throw new BadCredentialsException("Invalid Credentials");
			} 
			decryptedPassword = decryptBase64String(authentication.getCredentials().toString());
			if (passwordEncoder.matches(decryptedPassword, employee.getPassword())){
				return new UsernamePasswordAuthenticationToken(authentication.getName(), employee.getPassword());
			}else {
				throw new BadCredentialsException("Invalid Credentials");
				
			}
		}
		
	}


	private String decryptBase64String(String base64String) {
		return new String(Base64Utils.decodeFromString(base64String));
	}
}
