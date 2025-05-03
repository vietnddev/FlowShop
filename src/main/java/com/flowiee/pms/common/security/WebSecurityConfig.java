package com.flowiee.pms.common.security;

import com.flowiee.pms.modules.user.service.UserDetailsServiceImpl;
import com.flowiee.pms.common.enumeration.EndPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Thay thế @EnableGlobalMethodSecurity
public class WebSecurityConfig {
	private final UserDetailsServiceImpl userDetailsServiceImpl;
	private final DevAuthBypassFilter devAuthBypassFilter;

	public WebSecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl,
							 DevAuthBypassFilter devAuthBypassFilter) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
		this.devAuthBypassFilter = devAuthBypassFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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
	public AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
		return new WebAuthenticationDetailsSource();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.cors().and()
				.csrf().disable()
				.headers(headers -> headers.frameOptions().sameOrigin()) // Cấu hình iframe
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(
								EndPoint.URL_SYS_CONFIG.getValue(),
								EndPoint.URL_SYS_ACCOUNT.getValue(),
								EndPoint.URL_SYS_LOG.getValue()
						).hasRole("ADMIN")
						.requestMatchers(
								"/build/**", "/dist/**", "/js/**", "/plugins/**",
								"/uploads/**", "/actuator/**", "/swagger-ui/**",
								"/sls/order/tracking/**"
						).permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage(EndPoint.URL_LOGIN.getValue()).permitAll()
						.loginProcessingUrl(getAuthEndPoint())
						.failureUrl(EndPoint.URL_LOGIN.getValue() + "?success=fail")
						.defaultSuccessUrl("/")
						.authenticationDetailsSource(authenticationDetailsSource())
				)
				.addFilterBefore(devAuthBypassFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic()
				.and()
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
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
}