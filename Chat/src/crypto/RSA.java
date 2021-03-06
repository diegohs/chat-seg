package crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class RSA implements Crypto {

	private KeyPairGenerator kpg;
	private KeyPair keyPair;
	private Signature sig;
	private PublicKey pub;
	private PublicKey myPub;
	

	public PublicKey getMyPub() {
		return myPub;
	}

	public void setPub(PublicKey pub) {
		this.pub = pub;
		
		/*System.out.println("MyPub: \t" + myPub.toString());
		System.out.println("pub: \t" + pub.toString());*/
		
		
	}

	public RSA() {

		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(1024);
			keyPair = kpg.genKeyPair();
			sig = Signature.getInstance("MD5WithRSA");
			myPub = keyPair.getPublic();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void alteraChave(String s) {

	}

	@Override
	public byte[] encrypt(byte[] plaintext) {

		try {
			
			sig.initSign(keyPair.getPrivate());
			sig.update(plaintext);
			byte[] signatureBytes = sig.sign();
			return signatureBytes;
			
		} catch (SignatureException | InvalidKeyException e) {			
			e.printStackTrace();
		} 

		return null;
	}

	@Override
	public byte[] decrypt(byte[] plaintext){
		return plaintext;
		
	}
	
	public byte[] decrypt(byte[] plaintext, byte[] hash) {
		
		try {
			sig.initVerify(pub);
			sig.update(plaintext);
			if(sig.verify(hash)){
				return "true".getBytes("UTF8");
			} else {
				return "false".getBytes("UTF8");
			}
						
			
		} catch (InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}