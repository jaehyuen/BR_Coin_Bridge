package com.brcoin.bridge.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brcoin.bridge.client.CryptoClient;
import com.brcoin.bridge.service.BrcoinService;
import com.brcoin.bridge.vo.ResultVo;
import com.brcoin.bridge.vo.TokenVo;
import com.brcoin.bridge.vo.TransferVo;
import com.brcoin.bridge.vo.WalletVo;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/brcoin")
@RequiredArgsConstructor
public class BrcoinController {

	final private BrcoinService brcoinService;
	final private CryptoClient  cryptoClient;

	@Operation(summary = "바락코인 지갑 생성", description = "바락코인 지갑을 생성하는 api 입니다.")
	@PostMapping("/wallet")
	public ResponseEntity<ResultVo<Map<String, String>>> createWallet(String publicKey) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(brcoinService.createWallet(publicKey));

	}

	@Operation(summary = "바락코인 지갑 조회", description = "바락코인 지갑을 조회하는 api 입니다.")
	@GetMapping("/wallet")
	public ResponseEntity<ResultVo<WalletVo>> queryWallet(String walletId) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(brcoinService.queryWallet(walletId));

	}

	@Operation(summary = "바락코인(토큰) 생성", description = "바락코인(토큰)을 발행하는 api 입니다.")
	@PostMapping("/token")
	public ResponseEntity<ResultVo<Object>> createToken(@RequestBody TokenVo tokenVo) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(brcoinService.createToken(tokenVo));

	}

	@Operation(summary = "바락코인(토큰) 송금", description = "바락코인(토큰)을 송금하는 api 입니다.")
	@PostMapping("/tranfer")
	public ResponseEntity<ResultVo<Object>> transfer(@RequestBody TransferVo transferVo) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(brcoinService.transferToken(transferVo));

	}

	@PostMapping("/test")
	public ResponseEntity<String> test(@RequestBody HashMap<String, String> map) throws NoSuchAlgorithmException, NoSuchProviderException, IOException {

		String publicKey = map.get("publicKey");
//				.replaceAll("^-----.*-----$", "");
		System.out.println("publicKey " + publicKey);
		String privateKey = map.get("privateKey");
//			.replaceAll("\n", "");
		String plainText  = "플레인 텍스트";
		System.out.println("평문: " + plainText);
		String encryptedText = cryptoClient.testEncode(plainText, publicKey);
		System.out.println("암호화: " + encryptedText);
		String decryptedText = cryptoClient.testDecode(encryptedText, privateKey);
		System.out.println("복호화: " + decryptedText);
		String signText = cryptoClient.sign(plainText, privateKey);
		System.out.println("서명: " + signText);
		boolean result = cryptoClient.verifySignarue(plainText, signText, publicKey);
		System.out.println("인증: " + result);

		return ResponseEntity.status(HttpStatus.OK)
			.body("");

	}

}
