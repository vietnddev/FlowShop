package com.flowiee.pms.security;

import com.flowiee.pms.service.system.impl.UserDetailsServiceImpl;
import com.flowiee.pms.common.enumeration.EndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.http.HttpServletRequest;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsServiceImpl userDetailsServiceImpl;
	@Autowired
	DevAuthBypassFilter devAuthBypassFilter;

//	public DevAuthBypassFilter devAuthBypassFilter() {
//		return new DevAuthBypassFilter();
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> authenticationDetailsSource() {
		return new WebAuthenticationDetailsSource();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.csrf().disable().cors();

		//Cấu hình phần này để có thể nhúng URL vào các thẻ như iframe,..
		//httpSecurity.headers().frameOptions().sameOrigin();

		httpSecurity
				.cors().and()
				.csrf().disable()
				.headers().frameOptions().sameOrigin().and() //Cấu hình phần này để có thể nhúng URL vào các thẻ như iframe,..
				.authorizeRequests()
				.antMatchers(EndPoint.URL_SYS_CONFIG.getValue(),
							 EndPoint.URL_SYS_ACCOUNT.getValue(),
							 EndPoint.URL_SYS_LOG.getValue()).hasRole("ADMIN")
				.antMatchers("/build/**", "/dist/**", "/js/**", "/plugins/**", "/uploads/**", "/actuator/**", "/swagger-ui/**").permitAll()
				.anyRequest().authenticated()
				.and()
				//Page login
				.formLogin()
					.loginPage(EndPoint.URL_LOGIN.getValue()).permitAll()
					.loginProcessingUrl(getAuthEndPoint())
					.failureUrl(EndPoint.URL_LOGIN.getValue() + "?success=fail")
					.permitAll()
				//Login OK thì redirect vào page danh sách sản phẩm
				.defaultSuccessUrl("/")
				.authenticationDetailsSource(authenticationDetailsSource())
				.and()
				.addFilterBefore(devAuthBypassFilter, UsernamePasswordAuthenticationFilter.class)
				.httpBasic()
				.and()
				.logout()
					.logoutUrl(EndPoint.URL_LOGOUT.getValue()) // Endpoint cho đăng xuất
					.logoutSuccessUrl(EndPoint.URL_LOGIN.getValue()) // Đường dẫn sau khi đăng xuất thành công
				.deleteCookies("JSESSIONID") // Xóa cookies sau khi đăng xuất
				.invalidateHttpSession(true) // Hủy phiên làm việc
				.and()
				.userDetailsService(userDetailsService())
				//Page default if you are not authorized
				.exceptionHandling().accessDeniedPage("/error/403");
	}
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurerAdapter() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry.addMapping("/**");
//			}
//		};
//	}

	public static String getAuthEndPoint() {
		return "/j_spring_security_check";
	}
}