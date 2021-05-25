package com.brcoin.bridge.vo;

import java.util.List;

import lombok.Data;

@Data
public class TokenVo {

	private String          owner;       // 토큰 주인 주소
	private String          symbol;      // 토큰 심볼
	private String          totalSupply; // 토큰 총 공급량
	private String          name;        // 토큰 이름
	private String          information; // 토큰 정보
	private String          url;         // 토큰 관련 url
	private int             decimal;     // 토큰 소수점 자리수 최대 8
	private List<ReserveVo> reserve;     // 최초 분배 관련

	private int             createDate;
	private int             tokenId;
	private String          jobType;
//	private String[]        jobArgs;
	private int             jobDate;

	private String          signature;   // 서명 데이터값 (base64 인코딩)
}
