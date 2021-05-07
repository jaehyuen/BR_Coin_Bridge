package com.brcoin.bridge.vo;

import lombok.Data;

@Data
public class TransferVo {

	private String fromAddr;   // 보내는 지갑주소
	private String toAddr;     // 받는 지갑주소
	private String amount;     // 보내는 량
	private String tokenId;    // 토큰 아이디
	private String unlockDate; // 거래정지 날짜 (unix timestamp)

	private String signature; //서명 데이터값 (base64 인코딩)

}
