package com.brcoin.bridge.service;

import org.springframework.stereotype.Service;

import com.brcoin.bridge.client.FabricClient;
import com.brcoin.bridge.common.ChaincodeFunction;
import com.brcoin.bridge.vo.WalletVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FabricService {

	final private FabricClient fabricClient;

	public void test() {

		String walletId;

			walletId = fabricClient.invokeFabric(ChaincodeFunction.CREATE_WALLET, "zzz");
			System.out.println(walletId);
			System.out.println(fabricClient.queryFabric(ChaincodeFunction.QUERY_WALLET, walletId.replaceAll("\"", ""), WalletVo.class));


	}

}
