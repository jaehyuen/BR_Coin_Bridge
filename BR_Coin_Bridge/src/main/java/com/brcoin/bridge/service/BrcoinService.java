package com.brcoin.bridge.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.gateway.ContractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brcoin.bridge.client.FabricClient;
import com.brcoin.bridge.common.ChaincodeFunction;
import com.brcoin.bridge.common.JwtProvider;
import com.brcoin.bridge.common.Util;
import com.brcoin.bridge.vo.JwtTokenVo;
import com.brcoin.bridge.vo.ResultVo;
import com.brcoin.bridge.vo.TokenVo;
import com.brcoin.bridge.vo.TransferVo;
import com.brcoin.bridge.vo.WalletVo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrcoinService {

	final private FabricClient fabricClient;
	final private Util         util;
	private final JwtProvider  jwtProvider;

	private Logger             logger = LoggerFactory.getLogger(this.getClass());
	private Gson               gson   = new Gson();

	/**
	 * 지갑을 조회하는 서비스
	 * 
	 * @param walletId 조회할 지갑 주소
	 * @return ResultVo<WalletVo> 결과 vo(지갑 정보 vo)
	 */

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

	/**
	 * 지갑을 생성하는 서비스
	 * 
	 * @param publicKey 지갑의 공개키
	 * 
	 * @return ResultVo<String> 결과 vo(생성한 지갑 주소)
	 */

	public ResultVo<String> createWallet(String publicKey) {
		logger.debug("[createWallet] start ");
		logger.debug("[createWallet] publicKey -> " + publicKey);

		String address = null;

		try {

			address = fabricClient.invokeFabric(ChaincodeFunction.CREATE_WALLET, publicKey)
				.replaceAll("\"", "");

		} catch (Exception e) {

			logger.debug("[createWallet] error ");
			return util.setResult("9999", false, e.getMessage(), null);

		}

		logger.debug("[createWallet] finish ");

		return util.setResult("0000", true, "success create wallet", address);
	}

	/**
	 * 토큰을 생성하는 서비스
	 * 
	 * @param tokenVo 생성할 토큰 정보 vo
	 * 
	 * @return ResultVo<String> 결과 vo(생성한 토큰 id)
	 */

	public ResultVo<String> createToken(TokenVo tokenVo) {

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

	/**
	 * 토큰 송금 서비스
	 * 
	 * @param transferVo 송금 정보 vo
	 * 
	 * @return ResultVo<String> 결과 vo(송금 결과)
	 */

	public ResultVo<String> transferToken(TransferVo transferVo) {

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

	public ResultVo<List<TokenVo>> getAllTokens() {

		List<TokenVo> result = new ArrayList<TokenVo>();
		try {

			result = fabricClient.queryFabric(ChaincodeFunction.QUERY_ALL_TOKENS, "", new TypeToken<List<TokenVo>>() {
			}.getType());

		} catch (Exception e) {

			logger.debug("[getToken] error ");
			return util.setResult("9999", false, e.getMessage(), null);
		}

		logger.debug("[getToken] finish ");
		return util.setResult("0000", true, "success getToken ", result);
	}

	public ResultVo<JwtTokenVo> test(String role) {

		// 정상 로그인시 JWT 토큰 발급
		String     token      = jwtProvider.generateToken("brwallet", role);

		// 리턴값 생성
		JwtTokenVo jwtTokenVo = JwtTokenVo.builder()
			.accessToken(token)
			.refreshToken("")
			.expiresAt(Instant.now()
				.plusMillis(jwtProvider.getJwtExpirationInMillis()))
			.userId("")
			.build();

		return util.setResult("0000", true, "Success login", jwtTokenVo);
	}

}
