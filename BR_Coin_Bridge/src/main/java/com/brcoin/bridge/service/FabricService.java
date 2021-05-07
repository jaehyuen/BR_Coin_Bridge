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


}
