package com.brcoin.bridge.common;

import static io.jsonwebtoken.Jwts.parser;
import static java.util.Date.from;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtProvider {

	private KeyStore   keyStore;
	private final Long jwtExpirationInMillis = (long) (1000 * 3600);

	@PostConstruct
	public void init() {
		try {
			keyStore = KeyStore.getInstance("JKS");
			InputStream resourceAsStream = getClass().getResourceAsStream("/brbridge.jks");
			keyStore.load(resourceAsStream, "Asdf!234".toCharArray());

		} catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
			throw new RuntimeException("Exception occurred while loading keystore", e);
		}
	}

	public String generateToken(String serviceName, String role) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, 5);

		Claims claims = Jwts.claims()
			.setSubject(serviceName);
		claims.put("role", role);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(from(Instant.now()))
			.signWith(getPrivateKey())
			.setExpiration(cal.getTime())
			.compact();
	}

	private PrivateKey getPrivateKey() {
		try {
			return (PrivateKey) keyStore.getKey("brbridge", "Asdf!234".toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
			throw new RuntimeException("Exception occured while retrieving public key from keystore", e);
		}
	}

	public boolean validateToken(String jwt) {

		try {
			parser().setSigningKey(getPublickey())
				.parseClaimsJws(jwt);
			return true;
		} catch (Exception e) {

			return false;
		}

	}

	private PublicKey getPublickey() {
		try {
			return keyStore.getCertificate("brbridge")
				.getPublicKey();
		} catch (KeyStoreException e) {
			throw new RuntimeException("Exception occured while retrieving public key from keystore", e);
		}
	}

	public String getServiceNameFromJwt(String token) {
		Claims claims = parser().setSigningKey(getPublickey())
			.parseClaimsJws(token)
			.getBody();

		return claims.getSubject();
	}

	public List<GrantedAuthority> getRoleFromJwt(String token) {
		Claims claims = parser().setSigningKey(getPublickey())
			.parseClaimsJws(token)
			.getBody();

		claims.get("role");
		List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		if (claims.get("role")
			.equals("BRCOIN")) {
			auth.add(new SimpleGrantedAuthority("BRCOIN"));
		}
		auth.add(new SimpleGrantedAuthority("FABRIC"));
		return auth;
	}

	public Long getJwtExpirationInMillis() {
		return jwtExpirationInMillis;
	}
}
