package com.brcoin.bridge.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.brcoin.bridge.client.FabricClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InitConfig implements CommandLineRunner {

	final private FabricClient fabricClient;

	@Override
	public void run(String... args) throws Exception {
		fabricClient.enrollAdmin();
		fabricClient.connectFabric();
	}

}
