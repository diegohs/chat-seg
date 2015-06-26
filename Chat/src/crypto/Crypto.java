package crypto;

import java.security.PublicKey;

public interface Crypto {

	public abstract void alteraChave(String s);

	public abstract byte[] encrypt(byte[] plaintext);
	
	public abstract byte[] decrypt(byte[] plaintext);
	public abstract byte[] decrypt(byte[] plaintext, byte[] hash );
	
	public abstract void setPub(PublicKey pub);
	
	public abstract PublicKey getMyPub();

}