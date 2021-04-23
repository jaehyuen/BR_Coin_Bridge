package com.brcoin.bridge.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChaincodeFunction {

	CREATE_WALLET(0, "createWallet"), // 지갑 생성
	QUERY_WALLET(1, "queryWallet"), // 지갑 조회
	CREATE_TOKEN(2, "createToken"), // 토큰(코인) 생성
	TOTAL_SUPPLY(3, "totalSupply"), // 토큰(코인)의 총 발행량 조회
	BALANCE_OF(4, "balanceOf"), // 지갑에 있는 자산 조회
	TRANSFER(5, "transfer"); // 송금

	private final int    index;
	private final String value;
}
