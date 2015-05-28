package chat;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

import rc4.Rc4;

public class Chat extends Thread {
    
	private Socket conexao;
	private static String key = "e04fd020ea3a6910a2d808002b30309d";
	
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
        
    	if (args.length < 2 && args.length > 3){
    		System.out.println("Uso: \tchat <cliente> <endereco> <porta>");
    		System.out.println("\tchat <servidor> <porta>");
    		System.exit(-1);
    	} else {
    		
    		if(args[0].equalsIgnoreCase("cliente")){
    			
    			String endereco = args[1];
            	int porta = Integer.valueOf(args[2]);
            	
            	try {
            		// Conecta ao servidor
            		Socket socket = new Socket(endereco,porta);
            		
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
                        //System.out.print("Voce > ");
                        msg = teclado.readLine();
                        Rc4 rc4 = new Rc4(getKey());
                        msgCriptografada = rc4.encrypt(msg.getBytes(Charset.forName("UTF-8")));
                        
                        try{
                        	saida.writeInt(msgCriptografada.length);
                            saida.write(msgCriptografada);
                        }
                        catch(Exception e){
                            System.err.println(e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
                }
    		} else if(args[0].equalsIgnoreCase("servidor")){
    			
    			int porta = Integer.valueOf(args[1]);
    			
    				try {
    					// Abre um socket na porta especificada
    					ServerSocket server = new ServerSocket(porta);
    					Socket conexao = server.accept();
    					
    					// Inicia a Thread que fara a leitura das mensagens
    					Thread t = new Chat(conexao);
    					t.start();
    					
    					// Processo para leitura do teclado e envio de mensagens
    					DataOutputStream  saida = new DataOutputStream(conexao.getOutputStream()); 
                        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in,Charset.forName("UTF-8")));
                        String msg;
                        byte[] msgCriptografada;
                        
                        // Leitura das mensagens digitadas para enviar
                        while (true)
                        {
                            //System.out.print("Voce > ");
                            msg = teclado.readLine();
                            Rc4 rc4 = new Rc4(getKey());
                            msgCriptografada = rc4.encrypt(msg.getBytes(Charset.forName("UTF-8")));
                            
                            try{
                            	saida.writeInt(msgCriptografada.length);
                                saida.write(msgCriptografada);
                            }
                            catch(Exception e){
                                System.err.println(e.getMessage());
                            }
                        }
    					
    					
    				} catch (IOException e) {
    					System.out.println("IOException: " + e);
    				}
    		} else {
    			System.out.println(": \tchat <cliente> <endereco> <porta>");
        		System.out.println("\tchat <servidor> <porta>");
        		System.exit(-1);
    		}
    	}
    }

    // Realiza a leitura das mensagens que chegam
    public void run()
    {
    	int length;
        try {
        	DataInputStream  entrada = new DataInputStream(this.conexao.getInputStream());
        	//BufferedInputStream entrada = new BufferedInputStream(this.conexao.getInputStream());
            String msg;
            byte[] msgCriptografada = null;
            while (true)
            {
            	length = entrada.readInt();
            	if(length>0) {
            	    byte[] message = new byte[length];
            	    entrada.readFully(message, 0, message.length); // read the message
            	    Rc4 rc4 = new Rc4(getKey());
            	    msgCriptografada = rc4.encrypt(message);
            	}
                try{
                	msg = new String(msgCriptografada, Charset.forName("UTF-8"));
                    System.out.println("Remoto: " + msg);
                }
                catch(Exception e){
                    System.err.println(e.getMessage());
                }
                //System.out.println("Voce > ");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma Falha... .. ." + 
                " IOException: " + e);
        }
    }
}