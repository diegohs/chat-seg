package crypto;

public interface Crypto {

	public abstract void alteraChave(String s);

	public abstract byte[] encrypt(byte[] plaintext);
	
	public abstract byte[] decrypt(byte[] plaintext);

}