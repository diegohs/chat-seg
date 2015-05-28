package crypto;

public class Rc4 implements Crypto {
	private static String keyBkp;
    private final byte[] S = new byte[256];
    private final byte[] T = new byte[256];

    public Rc4(String s) {
    	keyBkp = String.valueOf(s);
    	alteraChave(s);
    }
    
    /* (non-Javadoc)
	 * @see crypto.Crypto#alteraChave(java.lang.String)
	 */
    @Override
	public void alteraChave(String s){
    	byte[] key = hexStringToByteArray(s);
    	
        if (key.length < 1 || key.length > 256) {
            System.err.println("Chave inválida!");
            System.exit(1);
        } else {
            // Realizando permutação
            for (int i = 0; i < 256; i++) {
                S[i] = (byte) i;
                T[i] = key[i % key.length];
            }
            int j = 0;
            byte tmp;
            for (int i = 0; i < 256; i++) {
                j = (j + S[i] + T[i]) & 0xff;
                tmp = S[j];
                S[j] = S[i];
                S[i] = tmp;
            }
        }
    	
    }

    /* (non-Javadoc)
	 * @see crypto.Crypto#encrypt(byte[])
	 */
    @Override
	public byte[] encrypt(final byte[] plaintext) {
        final byte[] ciphertext = new byte[plaintext.length];
        int i = 0, j = 0, k, t;
        byte tmp;
        
        for (int l = 0; l < plaintext.length; l++) {
            i = (i + 1) & 0xff; 
            j = (j + S[i]) & 0xff; 
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
            t = (S[i] + S[j]) & 0xff; 
            k = S[t];
            ciphertext[l] = (byte) (plaintext[l] ^ k);
        }
        alteraChave(keyBkp);
        return ciphertext;
    }
    
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

	@Override
	public byte[] decrypt(byte[] plaintext) {
		return encrypt(plaintext);
	}   
}
