package com.brcoin.bridge.service;

import java.util.HashMap;
import java.util.Map;

import org.hyperledger.fabric.gateway.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brcoin.bridge.client.CryptoClient;
import com.brcoin.bridge.client.FabricClient;
import com.brcoin.bridge.common.ChaincodeFunction;
import com.brcoin.bridge.common.Util;
import com.brcoin.bridge.vo.ResultVo;
import com.brcoin.bridge.vo.TokenVo;
import com.brcoin.bridge.vo.TransferVo;
import com.brcoin.bridge.vo.WalletVo;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrcoinService {

	final private FabricClient fabricClient;
	final private CryptoClient cryptoClient;
	final private Util         util;

	private Logger             logger = LoggerFactory.getLogger(this.getClass());
	private Gson               gson   = new Gson();

	public ResultVo<WalletVo> queryWallet(String walletId) {

		logger.debug("[queryWallet] start ");
		logger.debug("[queryWallet] walletId -> " + walletId);

		WalletVo walletVo = null;

		try {

			walletVo = fabricClient.queryFabric(ChaincodeFunction.QUERY_WALLET, walletId, WalletVo.class);

		} catch (ContractException e) {

			logger.debug("[queryWallet] error ");
			return util.setResult("9999", false, e.getMessage(), null);

		}

		logger.debug("[queryWallet] finish ");
		return util.setResult("0000", true, "success query wallet", walletVo);

	}

	public ResultVo<Map<String, String>> createWallet(String password) {
		logger.debug("[createWallet] start ");
		logger.debug("[createWallet] password -> " + password);

		String              result = null;
		Map<String, String> keyMap = new HashMap<String, String>();

		try {

			keyMap = cryptoClient.generateKeys();
			result = fabricClient.invokeFabric(ChaincodeFunction.CREATE_WALLET, keyMap.remove("public"))
				.replaceAll("\"", "");

		} catch (Exception e) {

			logger.debug("[createWallet] error ");
			return util.setResult("9999", false, e.getMessage(), null);

		}

		logger.debug("[createWallet] finish ");
		keyMap.put("address", result);
		return util.setResult("0000", true, "success create wallet", keyMap);
	}

	public ResultVo<Object> createToken(TokenVo tokenVo) {

		logger.debug("[createToken] start ");
		logger.debug("[createToken] tokenVo -> " + tokenVo);

		try {

			fabricClient.invokeFabric(ChaincodeFunction.CREATE_TOKEN, gson.toJson(tokenVo));

		} catch (Exception e) {

			logger.debug("[createToken] error ");
			return util.setResult("9999", false, e.getMessage(), null);
		}

		logger.debug("[createToken] finish ");
		return util.setResult("0000", true, "success create token", null);

	}

	public ResultVo<Object> transferToken(TransferVo transferVo) {

		logger.debug("[transferToken] start ");
		logger.debug("[transferToken] transferVo -> " + transferVo);

		try {

			fabricClient.invokeFabric(ChaincodeFunction.TRANSFER, gson.toJson(transferVo));

		} catch (Exception e) {

			logger.debug("[transferToken] error ");
			return util.setResult("9999", false, e.getMessage(), null);
		}

		logger.debug("[transferToken] finish ");
		return util.setResult("0000", true, "success transfer token", null);

	}

}
