package com.brcoin.bridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties("fabric")
public class FabricConfig {

	  private String walletPath; 
	  private String connectionConfig; 
	  private String caIpPort; 
	  private String caCertPath; 
	  private String hostIp; 
	  private String caEnrollId;
	  private String caEnrollPw; 
	  private String mspId; 
	  private String channelName;
	  private String chaincodeName;

	
}
