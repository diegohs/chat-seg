package dh;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MainClass {
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String texto;
		
		texto = "Olá";
		for(int i=0;i<texto.length();i++){
			System.out.println(texto.getBytes(StandardCharsets.UTF_8)[i]);
			
		}
		
		byte[] temp = texto.getBytes(StandardCharsets.UTF_8);
		
		System.out.println(new String(temp,"UTF-8"));
	}

}