package com.mood.user.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.mood.user.exceptions.ExceptionHandlerFilter;
import com.mood.user.repository.EmployeeRepository;
import com.mood.user.repository.UserRepository;

import lombok.AllArgsConstructor;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PropertySource("classpath:local.properties")
public class SecurityConfig {
	
	@Autowired
	private DataSource securityDataSource;
	
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Value("${jwt.secret}")
	private String jwtSecret;
	private MoodAuthenticationManager authenticationManager;
	
	
	public SecurityConfig() {
		super();
	}



	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	   auth.jdbcAuthentication().dataSource(securityDataSource).passwordEncoder(passwordEncoder);
	    
	}
	

	
	@Bean
	protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		authenticationManager = new MoodAuthenticationManager(userRepository, employeeRepository, passwordEncoder);
		AuthorizationFilter authorizationFilter = new AuthorizationFilter(jwtSecret, userRepository, employeeRepository);
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, jwtSecret);
		authenticationFilter.setFilterProcessesUrl("/authenticate");
		
		http.headers().frameOptions().disable()
						.and()
						.csrf().disable()
						.authorizeRequests()
						.expressionHandler(webSecurityExpressionHandler())
						.antMatchers(HttpMethod.POST, "/users/register/").permitAll()
						.antMatchers(HttpMethod.POST, "/users/admin/add/").permitAll()
						.anyRequest().authenticated()
						.and()
						.addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
						.addFilter(authenticationFilter)
						.addFilterAfter(authorizationFilter, AuthenticationFilter.class)
						.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		return http.build();
	}
	
	@Bean
	public RoleHierarchy roleHierarchy() {
	    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
	    String hierarchy = "ROLE_ADMIN > ROLE_DIAMOND > ROLE_GOLD > ROLE_UNPAID";
	    roleHierarchy.setHierarchy(hierarchy);
	    return roleHierarchy;
	}
	
	@Bean
	public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
	    DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
	    expressionHandler.setRoleHierarchy(roleHierarchy());
	    return expressionHandler;
	}
	
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
    
	public static String decodeBase64Password(String password) {
		byte[] decodedBytes = Base64Utils.decode(password.getBytes());
		String decodedPassword = new String(decodedBytes);
		return decodedPassword;
    }
	
}
