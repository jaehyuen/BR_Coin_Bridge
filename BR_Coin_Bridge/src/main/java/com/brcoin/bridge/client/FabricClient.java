package com.brcoin.bridge.client;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.DefaultQueryHandlers;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Transaction;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.gateway.spi.QueryHandlerFactory;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.stereotype.Component;

import com.brcoin.bridge.common.ChaincodeFunction;
import com.brcoin.bridge.config.FabricConfig;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FabricClient {

	final private FabricConfig     fabricConfig;
	private Gateway                gateway;
	private Network                network;
	private Contract               contract;
	private List<Collection<Peer>> peerList = new ArrayList<Collection<Peer>>();

	Gson                           gson     = new Gson();

	public void enrollAdmin() throws Exception {
		try {

			Properties props = new Properties();
			props.put("pemFile", fabricConfig.getCaCertPath());
			props.put("allowAllHostNames", "true");

			HFCAClient  caClient    = HFCAClient.createNewInstance(fabricConfig.getCaIpPort(), props);
			CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault()
				.getCryptoSuite();
			caClient.setCryptoSuite(cryptoSuite);

			// Create a wallet for managing identities
			Wallet wallet = Wallets.newFileSystemWallet(Paths.get(fabricConfig.getWalletPath()));

			if (wallet.get(fabricConfig.getCaEnrollId()) != null) { // 2.2
				return;
			}

			Enrollment enrollment = caClient.enroll(fabricConfig.getCaEnrollId(), fabricConfig.getCaEnrollPw(), new EnrollmentRequest());
			Identity   user       = Identities.newX509Identity(fabricConfig.getMspId(), enrollment);
			wallet.put(fabricConfig.getCaEnrollId(), user);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void connectFabric() throws Exception {

		try {

			Path            networkConfigPath = Paths.get(fabricConfig.getConnectionConfig());
			Path            walletPath        = Paths.get(fabricConfig.getWalletPath());
			Wallet          wallet            = Wallets.newFileSystemWallet(walletPath);      // 2.2
			Gateway.Builder builder           = Gateway.createBuilder();

			builder.identity(wallet, fabricConfig.getCaEnrollId())
				.networkConfig(networkConfigPath)
				.discovery(false);

			gateway  = builder.connect();
			network  = gateway.getNetwork(fabricConfig.getChannelName());
			contract = network.getContract(fabricConfig.getChaincodeName());

			Iterator<Peer> itrPeer = network.getChannel()
				.getPeers()
				.iterator();

			while (itrPeer.hasNext()) {

				Collection<Peer> peer = new ArrayList<Peer>();
				peer.add(itrPeer.next());
				peerList.add(peer);

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public <T> T queryFabric(ChaincodeFunction function, String param, Class<T> voClass) throws ContractException {

		Transaction tx       = contract.createTransaction(function.getValue());
		String      result   = new String(tx.evaluate(param));
		T           resultVo = gson.fromJson(result.replaceAll("\\\\", "")
			.replaceAll("\"\\[", "\\[")
			.replaceAll("\\]\"", "\\]"), voClass);

		return resultVo;
	}

	public String invokeFabric(ChaincodeFunction function, String param) throws ContractException, TimeoutException, InterruptedException {

		Transaction tx     = contract.createTransaction(function.getValue());

		String      result = null;
		try {
			result = new String(tx.setEndorsingPeers(peerList.get(0))
				.submit(param));

		} catch (TimeoutException e) {

			result = new String(tx.setEndorsingPeers(peerList.get(1))
				.submit(param));

		}
		return result;
	}

}
