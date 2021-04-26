package com.brcoin.bridge.service;

import org.springframework.stereotype.Service;

import com.brcoin.bridge.client.FabricClient;
import com.brcoin.bridge.common.ChaincodeFunction;
import com.brcoin.bridge.vo.TokenVo;
import com.brcoin.bridge.vo.WalletVo;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrcoinService {

	final private FabricClient fabricClient;
	Gson gson = new Gson();

	public void test() {

		String walletId;

		walletId = fabricClient.invokeFabric(ChaincodeFunction.CREATE_WALLET, "zzz");
		System.out.println(walletId);
		System.out.println(fabricClient.queryFabric(ChaincodeFunction.QUERY_WALLET, walletId.replaceAll("\"", ""), WalletVo.class));

	}

	public WalletVo queryWallet(String walletId) {
		return fabricClient.queryFabric(ChaincodeFunction.QUERY_WALLET, walletId, WalletVo.class);

	}

	public String createWallet(String password) {

		return fabricClient.invokeFabric(ChaincodeFunction.CREATE_WALLET, password)
			.replaceAll("\"", "");
	}
	
	public void createToken(TokenVo tokenVo) {
		
		System.out.println(tokenVo);
		System.out.println(gson.toJson(tokenVo));
		fabricClient.invokeFabric(ChaincodeFunction.CREATE_TOKEN,gson.toJson(tokenVo));
		
	}

}
