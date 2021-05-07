package com.brcoin.bridge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@Operation(summary = "바락코인 지갑 생성", description = "바락코인 지갑을 생성하는 api 입니다.")
	@PostMapping("/wallet")
	public ResponseEntity<ResultVo<String>> createWallet(String publicKey) {

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
	public ResponseEntity<ResultVo<String>> createToken(@RequestBody TokenVo tokenVo) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(brcoinService.createToken(tokenVo));

	}

	@Operation(summary = "바락코인(토큰) 송금", description = "바락코인(토큰)을 송금하는 api 입니다.")
	@PostMapping("/tranfer")
	public ResponseEntity<ResultVo<String>> transfer(@RequestBody TransferVo transferVo) {

		return ResponseEntity.status(HttpStatus.OK)
			.body(brcoinService.transferToken(transferVo));

	}

}
