package crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class MainClass {

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

	public MainClass() {

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

	public boolean decrypt(byte[] hash,byte[] plaintext) {
		

			//sig.initVerify(keyPair.getPublic());
			try {
				sig.initVerify(keyPair.getPublic());
				sig.update(plaintext);
				return sig.verify(hash);
				
			} catch (InvalidKeyException | SignatureException e) {
				
				e.printStackTrace();
			}
			
			return false;
	}
	
	public static void main(String[] args) throws Exception {
		MainClass rsa = new MainClass();
		
		byte[] texto = "diego".getBytes("UTF8");
		byte[] hash = rsa.encrypt(texto);
		
		System.out.println(rsa.decrypt(hash, texto));
		
	}
}