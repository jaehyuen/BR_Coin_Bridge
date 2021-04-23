package com.brcoin.bridge.vo;

import java.util.List;

import lombok.Data;

@Data
public class WalletVo {
	private int regDate;
	private String password;
	private String jobType;
	private List<String> jobArgs;
	private int jobDate;
	private List<BalanceInfoVo> balance;
	private String nonce;

}
