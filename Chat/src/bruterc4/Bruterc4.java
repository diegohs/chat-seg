package bruterc4;
import java.nio.charset.Charset;
import java.util.Random;

import crypto.Rc4;

public class Bruterc4 {
	public String[] frases = {
			"Ola",
			"Como vai?",
			"Bom dia!",
			"Boa Tarde",
			"Tudo bem?",
			"Sim.",
			"Nao.",
			"Adeus",
			"Ate logo",
			"lol",
			":)",
			":(",
			"ok",
			"td bem",
			"sem problemas",
			"tchau",
			"reuniao",
			"me liga",
			"obrigado!",
			"de nada",
			"muito obrigado",
			":0"
	};
	
	public void reveal(byte[] encripted,String origk){
		String key;
		Rc4 rcteste = new Rc4("0000000000");
		
		System.out.println("\n\nTentando revelar o segredo...\n");
		
		// Testando todas as chaves possíveis
		// 0    = 00 0000 0000
		// 1023 = 11 1111 1111
		for(int i=0;i<1024;i++){
			// Transformando inteiro em string de binários (com 10 casas)
			key = String.format("%010d", Integer.parseInt(Integer.toBinaryString(i)));
			
			// Alterando a chave
			rcteste = new Rc4(key);

			// Tentando decriptografar com a chave atual
			byte[] msgCriptografada = rcteste.decrypt( encripted );
			String msg = new String(msgCriptografada, Charset.forName("UTF-8"));

			// Para cada palavra conhecida, verifica se a decriptografia coincide
			for(int j=0;j<this.frases.length;j++){
				if( this.frases[j].equals(msg) ){
					System.out.println("Frase descoberta: "+this.frases[j]);
					System.out.println("Chave utilizada: "+key);
					break;
				}
			}
		}
	}
	
    public static void main(String args[])
    {
    	Bruterc4 bruterc4 = new Bruterc4();
    	Random gerador = new Random();
    	String key = "";
    	
    	// Montando uma chave aleatória para ser descoberta
    	for(int i=0;i<10;i++){
    		key += gerador.nextInt(2);
    	}
    	
    	System.out.println("Chave a ser descoberta: "+key);
    	
    	// Escolhendo aleatóriamente uma frase para ser descoberta (entre as frases conhecidas)
    	String frase = bruterc4.frases[gerador.nextInt(bruterc4.frases.length)];
    	
    	System.out.println("Frase a ser descoberta: "+frase);
    	
    	// Iniciando o RC4 para criptografar a mensagem e simular o que foi capturado
    	Rc4 rc = new Rc4(key);
    	
    	byte[] msgCriptografada;
    	//String msg;
    	
    	msgCriptografada = rc.encrypt(frase.getBytes(Charset.forName("UTF-8")));
    	//msg = new String(msgCriptografada, Charset.forName("UTF-8"));
    	
    	System.out.println("O que foi capturado: "+msgCriptografada);
    	
    	bruterc4.reveal(msgCriptografada,key);
    	
    	System.out.println("Fim!");
    	/*
    	msgCriptografada = rc.decrypt(msgCriptografada);
    	msg = new String(msgCriptografada, Charset.forName("UTF-8"));
    	
    	System.out.println(msg);

    	System.out.println(new String("pláintext".getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8")));
    	*/
    }
}
