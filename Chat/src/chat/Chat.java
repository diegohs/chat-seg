package chat;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import crypto.Crypto;
import crypto.Rc4;
import crypto.Sdes;

public class Chat extends Thread {
    
	private Socket conexao;
	private static String key = "";
	private static Crypto crypto;
	
	public static Crypto getCrypto() {
		return crypto;
	}

	public static void setCrypto(String crypto, String key) {
		setKey(key);
		if(crypto.equalsIgnoreCase("sdes"))
			Chat.crypto = new Sdes(key);
		else if(crypto.equalsIgnoreCase("rc4"))
			Chat.crypto = new Rc4(key);
	}

	public static String getKey() {
		return key;
	}

	public static void setKey(String key) {
		Chat.key = key;	
	}

	public Chat(Socket socket) {
        this.conexao = socket;
    }
    
    public static void main(String args[])
    {
    	Socket socket = null;
    	
    	if (args.length != 4){
    		System.out.println("Uso: chat <cliente|servidor> <endereco> <porta> <rc4|sdes>");
    		System.exit(-1);
    	} else {
    		
    		try {
    		
	    		if(args[0].equalsIgnoreCase("cliente")){
	    			String endereco = args[1];
	            	int porta = Integer.valueOf(args[2]);

	            	// Conecta ao servidor
            		socket = new Socket(endereco,porta);
	    			
	    		} else if(args[0].equalsIgnoreCase("servidor")){
	    			int porta = Integer.valueOf(args[2]);
	    			
	    			// Abre um socket na porta especificada
					ServerSocket server = new ServerSocket(porta);
					socket = server.accept();
					
	    		} else {
	    			System.out.println(": \tchat <cliente> <endereco> <porta>");
	        		System.out.println("\tchat <servidor> <porta>");
	        		System.exit(-1);
	    		}
	    		
	    		if(args[3].equalsIgnoreCase("rc4")){
	    			// Criptografia RC4
	    	    	setKey("e04fd020ea3a6910a2d808002b30309d");
	    	    	crypto = new Rc4(getKey());
	    		} else if(args[3].equalsIgnoreCase("sdes")){
	    			// Criptografia SDES
	    	    	setKey("1010000010");
	    	    	crypto = new Sdes(getKey());
	    		} else {
	    			System.out.println(": \tchat <cliente> <endereco> <porta>");
	        		System.out.println("\tchat <servidor> <porta>");
	        		System.exit(-1);
	    		}
	    		
                // Inicia a Thread que far� a leitura das mensagens que chegarem
                Thread thread = new Chat(socket);
                thread.start();

                // Processo para leitura do teclado e envio de mensagens
                DataOutputStream  saida = new DataOutputStream(socket.getOutputStream()); 
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in,Charset.forName("UTF-8")));
                String msg;
                byte[] msgCriptografada;
                
                // Leitura das mensagens digitadas para enviar
                while (true)
                {
                    msg = teclado.readLine();
                    if(msg.split(":")[0].equalsIgnoreCase("/key")){
                		setKey(msg.split(":")[1]);
                		Chat.crypto.alteraChave(key);
                	} else if(msg.split(":")[0].equalsIgnoreCase("/alg")){
                		setCrypto(msg.split(":")[1],msg.split(":")[2]);
                	} else if(msg.split(":")[0].equalsIgnoreCase("/get")){
                		System.out.println("Key: " + getKey());
                	} else {
	            		msgCriptografada = crypto.encrypt(msg.getBytes(Charset.forName("UTF-8")));
	            	
	                    try{
	                    	saida.writeInt(msgCriptografada.length);
	                        saida.write(msgCriptografada);
	                    }
	                    catch(Exception e){
	                        System.err.println(e.getMessage());
	                    }
                	}
                }
            } catch (IOException e) {
                System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
            }
    	}
    }

    // Realiza a leitura das mensagens que chegam
    public void run()
    {
    	int length;
        try {
        	DataInputStream  entrada = new DataInputStream(this.conexao.getInputStream());
        	String msg;
            byte[] msgCriptografada = null;
            while (true)
            {
            	length = entrada.readInt();
            	if(length>0) {
            	    byte[] message = new byte[length];
            	    entrada.readFully(message, 0, message.length);
            	    msgCriptografada = crypto.decrypt(message);
            	}
                try{
                	msg = new String(msgCriptografada, Charset.forName("UTF-8"));
                	System.out.println("Remoto: " + msg);
                }
                catch(Exception e){
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma Falha... .. ." + 
                " IOException: " + e);
        }
    }
}