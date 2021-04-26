package com.brcoin.bridge.client;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.springframework.stereotype.Component;

@Component
public class CryptoClient {

	final int KEY_SIZE = 1024;

	public Map<String, String> generateKeys() throws NoSuchAlgorithmException, NoSuchProviderException, IOException {
		Security.addProvider(new BouncyCastleProvider());

		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
		generator.initialize(KEY_SIZE);

		KeyPair             keyPair   = generator.generateKeyPair();

		RSAPrivateKey       priv      = (RSAPrivateKey) keyPair.getPrivate();
		RSAPublicKey        pub       = (RSAPublicKey) keyPair.getPublic();

		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("private", getKeyString(priv, "TEST PRIVATE KEY"));
		resultMap.put("public", getKeyString(pub, "TEST PUBLIC KEY"));

		return resultMap;

	}

	private String getKeyString(Key key, String desc) throws IOException {

		// OutputStream 선언
		OutputStream output    = new OutputStream() {
									private StringBuilder string = new StringBuilder();

									@Override
									public void write(int b) throws IOException {
										this.string.append((char) b);
									}

									// Netbeans IDE automatically overrides this toString()
									public String toString() {
										return this.string.toString();
									}
								};
		PemObject    pem       = new PemObject(desc, key.getEncoded());
		PemWriter    pemWriter = new PemWriter(new OutputStreamWriter(output));

		try {
			pemWriter.writeObject(pem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			pemWriter.close();

		}
		return output.toString();
	}

	public String testEncode(String plainData, String privKey) {
		String encryptedData = null;
		try {
			// 개인키 string을 개인키 객체로 변환
			PublicKey publicKey = getPublicKey(privKey);

			// 만들어진 공개키객체를 기반으로 암호화모드로 설정
			Cipher    cipher    = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			// 평문을 암호화
			byte[] byteEncryptedData = cipher.doFinal(plainData.getBytes());
			encryptedData = Base64.getEncoder()
				.encodeToString(byteEncryptedData);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return encryptedData;

	}

	public String testDecode(String encryptedData, String pubKey) {
		String decryptedData = null;
		try {
			// 공개키 string을 공개키 객체로 변환
			PrivateKey privateKey = getPrivateKey(pubKey);

			// 만들어진 개인키객체를 기반으로 암호화모드로 설정하는 과정
			Cipher     cipher     = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			// 암호문을 디코딩
			byte[] byteEncryptedData = Base64.getDecoder()
				.decode(encryptedData.getBytes());
			byte[] byteDecryptedData = cipher.doFinal(byteEncryptedData);
			decryptedData = new String(byteDecryptedData);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return decryptedData;
	}

	private PublicKey getPublicKey(String stringPublicKey) {
		PublicKey publicKey = null;
		try {

			// key string에 필요없는값 지우기
			stringPublicKey.replaceAll("\n", "")
				.replace("-----BEGIN TEST PRIVATE KEY-----", "")
				.replace("-----END TEST PRIVATE KEY-----", "");

			// 공개키 string을 공개키 객체로 변환
			KeyFactory         keyFactory    = KeyFactory.getInstance("RSA");
			byte[]             bytePublicKey = Base64.getDecoder()
				.decode(stringPublicKey.getBytes());
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
			publicKey = keyFactory.generatePublic(publicKeySpec);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return publicKey;
	}

	private PrivateKey getPrivateKey(String stringPrivateKey) {
		PrivateKey privateKey = null;
		try {

			// key string에 필요없는값 지우기
			stringPrivateKey.replaceAll("\n", "")
				.replace("-----BEGIN TEST PUBLIC KEY-----", "")
				.replace("-----END TEST PUBLIC KEY-----", "");

			// 개인키 string을 개인키 객체로 변환
			KeyFactory          keyFactory     = KeyFactory.getInstance("RSA");
			byte[]              bytePrivateKey = Base64.getDecoder()
				.decode(stringPrivateKey.getBytes());
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
			privateKey = keyFactory.generatePrivate(privateKeySpec);

		} catch (Exception e) {
			// e.printStackTrace();
		}
		return privateKey;
	}

	public String sign(String plainText, String stringPrivateKey) {
		try {

			// 개인키 string을 개인키 객체로 변환
			PrivateKey privateKey       = getPrivateKey(stringPrivateKey);
			Signature  privateSignature = Signature.getInstance("SHA256withRSA");

			// 개인키로 데이터 서명
			privateSignature.initSign(privateKey);
			privateSignature.update(plainText.getBytes("UTF-8"));
			byte[] signature = privateSignature.sign();
			return Base64.getEncoder()
				.encodeToString(signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException | SignatureException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean verifySignarue(String plainText, String signature, String stringPublicKey) {
		Signature sig;
		try {
			// 공개키 string을 공개키 객체로 변환
			PublicKey publicKey = getPublicKey(stringPublicKey);
			sig = Signature.getInstance("SHA256withRSA");

			// 공개키로 서명 데이터 검사
			sig.initVerify(publicKey);
			sig.update(plainText.getBytes());

			if (!sig.verify(Base64.getDecoder()
				.decode(signature)))
				;
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			throw new RuntimeException(e);
		}
		return true;
	}
}
