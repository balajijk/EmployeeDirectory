package com.headspring.employee.directory.controller;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.headspring.employee.directory.dao.EmployeeUserDetailServiceImpl;

@RestController
public class AuthController {
	

	@RequestMapping("/user")
	public Principal user(Principal user) {
		if (user == null) {
			return null;
		}
		if (("admin").equals(user.getName())) {
			System.out.println("Admin user");
		} else {
			System.out.println("Employee");
		}
		return user;
	}

	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class SecurityConfiguration extends
			WebSecurityConfigurerAdapter {
		@Autowired
		EmployeeUserDetailServiceImpl employeeUserDetailService;
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.httpBasic().and().authorizeRequests()
					.antMatchers("/", "/login", "/user").permitAll()
					.anyRequest().authenticated().and().csrf()
					.csrfTokenRepository(csrfTokenRepository()).and()
					.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
		}

		private Filter csrfHeaderFilter() {
			return new OncePerRequestFilter() {
				@Override
				protected void doFilterInternal(HttpServletRequest request,
						HttpServletResponse response, FilterChain filterChain)
						throws ServletException, IOException {
					CsrfToken csrf = (CsrfToken) request
							.getAttribute(CsrfToken.class.getName());
					if (csrf != null) {
						Cookie cookie = WebUtils.getCookie(request,
								"XSRF-TOKEN");
						String token = csrf.getToken();
						if (cookie == null || token != null
								&& !token.equals(cookie.getValue())) {
							cookie = new Cookie("XSRF-TOKEN", token);
							cookie.setPath("/");
							response.addCookie(cookie);
						}
					}
					filterChain.doFilter(request, response);
				}
			};
		}

		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}

		/*
		 * @TODO Authenticate using employee email/password 
		 * #hint use UserDetailsService 
		 */
		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth)
				throws Exception {
			
			auth.userDetailsService(employeeUserDetailService).passwordEncoder(new BCryptPasswordEncoder());
			
			/*auth.inMemoryAuthentication().withUser("user").password("password")
					.roles("USER");
			auth.inMemoryAuthentication().withUser("admin")
					.password("password").roles("ADMIN");*/
		}
	}
	
	

}
