package chat;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import crypto.Crypto;
import crypto.DiffieHellman;
import crypto.RSA;
import crypto.Rc4;
import crypto.Sdes;

public class Chat extends Thread {

	private static Socket conexao;
	private static String key = "";
	private static Crypto crypto;
	private static String alg = "rc4";
	private static DiffieHellman dh;
	private static String tipo;
	private static boolean negocia = true;

	public static Crypto getCrypto() {
		return crypto;
	}

	public static void setCrypto(String crypto, String key) {
		setKey(key);
		alg = crypto;
		if (crypto.equalsIgnoreCase("sdes"))
			Chat.crypto = new Sdes(key);
		else if (crypto.equalsIgnoreCase("rc4"))
			Chat.crypto = new Rc4(key);
		else if (crypto.equalsIgnoreCase("rsa"))
			Chat.crypto = new RSA();
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

	public static void main(String args[]) {
		Socket socket = null;
		dh = new DiffieHellman();

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		if (args.length != 4) {
			System.out
					.println("Uso: chat <cliente|servidor> <endereco> <porta> <rc4|sdes|rsa>");
			System.exit(-1);
		} else {

			try {

				alg = args[3];

				if (args[0].equalsIgnoreCase("cliente")) {
					String endereco = args[1];
					int porta = Integer.valueOf(args[2]);
					tipo = "cliente";

					// Conecta ao servidor
					socket = new Socket(endereco, porta);

				} else if (args[0].equalsIgnoreCase("servidor")) {
					int porta = Integer.valueOf(args[2]);
					tipo = "servidor";

					// Abre um socket na porta especificada
					ServerSocket server = new ServerSocket(porta);
					socket = server.accept();

				} else {
					System.out
							.println("Uso: chat <cliente|servidor> <endereco> <porta> <rc4|sdes|rsa>");
					System.exit(-1);
				}

				// Inicia a Thread que farï¿½ a leitura das mensagens que
				// chegarem
				Thread thread = new Chat(socket);
				thread.start();

				// Processo para leitura do teclado e envio de mensagens
				DataOutputStream saida = new DataOutputStream(
						socket.getOutputStream());
				BufferedReader teclado = new BufferedReader(
						new InputStreamReader(System.in,
								Charset.forName("UTF-8")));
				String msg;
				byte[] msgCriptografada;

				// Negocia chaves de sessÃ£o
				if (tipo.equalsIgnoreCase("cliente")) {
					
					if(alg.equalsIgnoreCase("rsa")){
						setCrypto(alg, null);
						ObjectOutputStream obOut = new ObjectOutputStream(conexao.getOutputStream());
						obOut.writeObject(crypto.getMyPub());
						obOut.flush();		
					} else {
						msgCriptografada = String.valueOf(dh.getPubY()).getBytes();
						try {
							saida.writeInt(msgCriptografada.length);
							saida.write(msgCriptografada);
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}					
				}
				
				// Leitura das mensagens digitadas para enviar
				byte[] digest;
				while (true) {
					msg = teclado.readLine();
					
					if (msg.split(":")[0].equalsIgnoreCase("/key")) {
						setKey(msg.split(":")[1]);
						Chat.crypto.alteraChave(key);
					} else if (msg.split(":")[0].equalsIgnoreCase("/alg")) {
						setCrypto(msg.split(":")[1], msg.split(":")[2]);
					} else if (msg.split(":")[0].equalsIgnoreCase("/get")) {
						System.out
								.println("Alg: " + alg + "\tKey: " + getKey());
					} else {

						if (alg.equalsIgnoreCase("rsa")) {
							
							msgCriptografada = crypto.encrypt(msg
									.getBytes(Charset.forName("UTF-8")));
							
							// Envia hash
							try {
								saida.writeInt(msgCriptografada.length);
								saida.write(msgCriptografada);
							} catch (Exception e) {
								System.err.println(e.getMessage());
							}

							// Envia msg
							try {
								saida.writeInt(msg.getBytes(Charset
										.forName("UTF-8")).length);
								saida.write(msg.getBytes(Charset
										.forName("UTF-8")));
							} catch (Exception e) {
								System.err.println(e.getMessage());
							}
						} else {
							msgCriptografada = crypto.encrypt(msg
									.getBytes(Charset.forName("UTF-8")));

							try {
								saida.writeInt(msgCriptografada.length);
								saida.write(msgCriptografada);
							} catch (Exception e) {
								System.err.println(e.getMessage());
							}
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Falha na Conexao... .. ."
						+ " IOException: " + e);
			}
		}
	}

	// Realiza a leitura das mensagens que chegam
	public void run() {
		int length;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		try {
			DataInputStream entrada = new DataInputStream(
					conexao.getInputStream());
			String msg = null;
			byte[] msgCriptografada = null;

			// Negocia chaves
			while (negocia && tipo.equalsIgnoreCase("servidor")) {
				
				if(alg.equalsIgnoreCase("rsa")){
					
					setCrypto(alg, null);
					
					// Recebe a public key
					ObjectInputStream obIn = new ObjectInputStream(conexao.getInputStream());
					Object obj;
					try {
						obj = obIn.readObject();
						PublicKey otherPublicKey = (PublicKey) obj;
						crypto.setPub(otherPublicKey);
						
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					
					// Envia chave publica
					ObjectOutputStream obOut = new ObjectOutputStream(conexao.getOutputStream());
					obOut.writeObject(crypto.getMyPub());
					obOut.flush();					
					
				} else {
					length = entrada.readInt();
					if (length > 0) {
						byte[] message = new byte[length];
						entrada.readFully(message, 0, message.length);
						msg = new String(message, Charset.forName("UTF-8"));
					}
					String chave = dh.calcK(Integer.valueOf(msg));
					setCrypto(alg, chave);

					// Envia chave publica para cliente
					DataOutputStream saida = new DataOutputStream(
							conexao.getOutputStream());
					msgCriptografada = String.valueOf(dh.getPubY()).getBytes();
					try {
						saida.writeInt(msgCriptografada.length);
						saida.write(msgCriptografada);
					} catch (Exception e) {
						System.err.println(e.getMessage());
					}					
				}
				negocia = false;
			}

			// Cliente recebe a chave publica e configura criptografia
			while (negocia && tipo.equalsIgnoreCase("cliente")) {
				
				if(alg.equalsIgnoreCase("rsa")){
					ObjectInputStream obIn = new ObjectInputStream(conexao.getInputStream());
					Object obj;
					try {
						obj = obIn.readObject();
						PublicKey otherPublicKey = (PublicKey) obj;
						crypto.setPub(otherPublicKey);
						
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					length = entrada.readInt();
					if (length > 0) {
						byte[] message = new byte[length];
						entrada.readFully(message, 0, message.length);
						msg = new String(message, Charset.forName("UTF-8"));
					}
					String chave = dh.calcK(Integer.valueOf(msg));
					setCrypto(alg, chave);
				}
				negocia = false;
			}

			// Leitura das mensagens recebidas
			byte[] message = null;
			byte[] digest = null;
			byte[] valida = null;
			String validaStr;
			String digestStr;
			while (true) {
				length = entrada.readInt();
				if (length > 0) {
					message = new byte[length];
					entrada.readFully(message, 0, message.length);
				}
				
				try {

					if (alg.equalsIgnoreCase("rsa")) {

						// Salva a autenticação
						digest = message.clone();

						// Le a mensagem
						length = entrada.readInt();
						if (length > 0) {
							message = new byte[length];
							entrada.readFully(message, 0, message.length);
						}
						
						
						valida = crypto.decrypt(message,digest);
						validaStr = new String(valida,"UTF8");
						
						if(validaStr.equalsIgnoreCase("true")){
							System.out.println(new String(message,Charset.forName("UTF-8")));
						}

						

					} else {
						msgCriptografada = crypto.decrypt(message);
						msg = new String(msgCriptografada,
								Charset.forName("UTF-8"));
						System.out.println("Remoto: " + msg);
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		} catch (IOException e) {
			System.out.println("Ocorreu uma Falha... .. ." + " IOException: "
					+ e);
		}
	}
}