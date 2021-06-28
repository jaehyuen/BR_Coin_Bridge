package com.brcoin.bridge.vo;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtTokenVo {
	private String  accessToken;  // JWT 토큰
	private String  refreshToken; // JWT 토큰 재발급시 필요한 토큰
	private Instant expiresAt;    // JWT 토큰 만료 시간
	private String  userId;       // 사용자 ID
}
