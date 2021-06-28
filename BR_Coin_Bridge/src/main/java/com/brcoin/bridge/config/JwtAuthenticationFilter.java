package com.brcoin.bridge.config;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.brcoin.bridge.common.JwtProvider;
import com.brcoin.bridge.common.Util;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final Util        util;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String       uri    = request.getRequestURI();
		ObjectMapper mapper = new ObjectMapper();

		if (!uri.startsWith("/api/fabric/jwt")) {

			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {

				String                              serviceName    = jwtProvider.getServiceNameFromJwt(jwt);

				List<GrantedAuthority>              authorities    = jwtProvider.getRoleFromJwt(jwt);
				
				System.out.println("authorities : "+authorities);
				System.out.println("serviceName : "+serviceName);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(serviceName, null, authorities);

				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext()
					.setAuthentication(authentication);
				filterChain.doFilter(request, response);
			} else {

				response.setStatus(401);
				response.setContentType("application/json;charset=UTF-8");
				response.getWriter()
					.write(mapper.writeValueAsString(util.setResult("9999", false, "JWT 토큰값을 확인해주십시오", null)));

			}
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");

		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return bearerToken;
	}
}
