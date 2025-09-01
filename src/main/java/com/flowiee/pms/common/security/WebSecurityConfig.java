package com.flowiee.pms.common.security;

import com.flowiee.pms.modules.system.service.impl.UserDetailsServiceImpl;
import com.flowiee.pms.common.enumeration.EndPoint;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Thay thế @EnableGlobalMethodSecurity
public class WebSecurityConfig {
	private final UserDetailsServiceImpl userDetailsServiceImpl;

	public WebSecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new CustomBCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder authenticationManagerBuilder =
				http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder
				.userDetailsService(userDetailsServiceImpl)
				.passwordEncoder(passwordEncoder());
		return authenticationManagerBuilder.build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf(csrf -> csrf.disable())
				.headers(headers -> headers.frameOptions().sameOrigin()) // iframe
				.authorizeHttpRequests(authorize -> authorize
						.antMatchers("/build/**", "/dist/**", "/js/**", "/plugins/**", "/uploads/**", "/actuator/**", "/swagger-ui/**", "/sls/order/tracking/**", "/favicon.ico")
								.permitAll()
						.antMatchers(EndPoint.URL_SYS_CONFIG.getValue(), EndPoint.URL_SYS_ACCOUNT.getValue(), EndPoint.URL_SYS_LOG.getValue())
								.hasRole("ADMIN")
						.anyRequest()
								.authenticated()
				)
				.formLogin(form -> form
						.loginPage(EndPoint.URL_LOGIN.getValue()).permitAll()
						.loginProcessingUrl(getAuthEndPoint())
						.failureUrl(EndPoint.URL_LOGIN.getValue() + "?success=fail")
						.defaultSuccessUrl("/")
				)
				.logout(logout -> logout
						.logoutUrl(EndPoint.URL_LOGOUT.getValue())
						.logoutSuccessUrl(EndPoint.URL_LOGIN.getValue())
						.deleteCookies("JSESSIONID")
						.invalidateHttpSession(true)
				)
				.exceptionHandling(exception -> exception
						.accessDeniedPage("/error/403")
				);

		return httpSecurity.build();
	}

	public static String getAuthEndPoint() {
		return "/j_spring_security_check";
	}

	@Bean
	public Filter logFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(HttpServletRequest request,
											HttpServletResponse response,
											FilterChain filterChain) throws ServletException, IOException {
				try {
					filterChain.doFilter(request, response);
				} catch (RuntimeException ex) {
					//logger.warn("No static resource: " + request.getRequestURL());
				}
			}
		};
	}
}