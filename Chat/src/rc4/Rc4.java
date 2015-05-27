/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rc4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Rc4 {
    private final byte[] S = new byte[256];
    private final byte[] T = new byte[256];

    public Rc4(String s) {
    	alteraChave(s);
    }
    
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
                j = (j + S[i] + T[i]) % 256;
                
                if(j < 0) j += 256;

                tmp = S[j];
                S[j] = S[i];
                S[i] = tmp;
            }
        }
    	
    }

    public byte[] encrypt(final byte[] plaintext) {
        final byte[] ciphertext = new byte[plaintext.length];
        int i = 0, j = 0, k, t;
        byte tmp;
        
        for (int l = 0; l < plaintext.length; l++) {
            i = (i + 1) % 256;
            if(i<0) i+=256;
            j = (j + S[i]) % 256;
            if(j<0) j+=256;
            tmp = S[j];
            S[j] = S[i];
            S[i] = tmp;
            t = (S[i] + S[j]) % 256;
            if(t<0) t+=256;
            k = S[t];
            ciphertext[l] = (byte) (plaintext[l] ^ k);
        }
        
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
    
    
    public static void main(String args[]) throws IOException {
        
        byte[] response;
        String plaintext,decoded = "";
        
        try{
        	BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
        	while(true){
        		plaintext = teclado.readLine();
        		Rc4 cripta = new Rc4("e04fd020ea3a6910a2d808002b30309d");
            	response = cripta.encrypt(plaintext.getBytes(Charset.forName("UTF-8")));
                decoded = new String(response, "UTF-8");
                System.out.println("Criptografado: " + decoded);
                
                cripta = new Rc4("e04fd020ea3a6910a2d808002b30309d");
                response = cripta.encrypt(decoded.getBytes(Charset.forName("UTF-8")));
                plaintext = new String(response, "UTF-8");
                System.out.println("Plaintext: " + plaintext);
                
        	}        	
        }
        catch(UnsupportedEncodingException e){
            System.err.println(e.getMessage());
        }
    }
    
}
