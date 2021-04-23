package com.brcoin.bridge.vo;

import java.util.List;

import lombok.Data;

@Data
public class TokenVo {

	private String owner;
	private String symbol;
	private String totalSupply;
	private String name;
	private String information;
	private String url;
	private String decimal;
	private List<ReserveVo> reserve;
}
