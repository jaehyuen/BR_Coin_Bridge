package com.brcoin.bridge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	public void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors()
			.and()
			.csrf()
			.disable()
			.authorizeRequests()
			.antMatchers("/api/fabric/jwt")
			.permitAll()
			.antMatchers("/api/brcoin/**")
			.hasAuthority("BRCOIN")
			.antMatchers("/api/fabric/**")
			.hasAuthority("FABRIC")
			.anyRequest()
			.authenticated()
			.and()
			.addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

	}

}
