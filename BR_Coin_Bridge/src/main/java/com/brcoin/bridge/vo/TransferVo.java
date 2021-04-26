package com.brcoin.bridge.vo;

import lombok.Data;

@Data
public class TransferVo {

	private String fromAddr;
	private String toAddr;
	private String amount;
	private String tokenId;
	private String unlockDate;

}
