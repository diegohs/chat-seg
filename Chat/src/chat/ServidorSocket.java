package chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServidorSocket extends Thread {
	private static Map<String, PrintStream> MAP_CLIENTES;
	private Socket conexao;
	private String nomeCliente;
	private static List<String> LISTA_DE_NOMES = new ArrayList<String>();

	public ServidorSocket(Socket socket) {
		this.conexao = socket;
	}

	public boolean armazena(String newName) {
		for (int i = 0; i < LISTA_DE_NOMES.size(); i++) {
			if (LISTA_DE_NOMES.get(i).equals(newName))
				return true;
		}
		LISTA_DE_NOMES.add(newName);
		return false;
	}

	public void remove(String oldName) {
		for (int i = 0; i < LISTA_DE_NOMES.size(); i++) {
			if (LISTA_DE_NOMES.get(i).equals(oldName))
				LISTA_DE_NOMES.remove(oldName);
		}
	}

	public static void main(String args[]) {
		MAP_CLIENTES = new HashMap<String, PrintStream>();
		try {
			ServerSocket server = new ServerSocket(3000);
			while (true) {
				Socket conexao = server.accept();
				Thread t = new ServidorSocket(conexao);
				t.start();
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e);
		}
	}

	public void run() {
		try {
			BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream(),"ISO-8859-1"));
			PrintStream saida = new PrintStream(this.conexao.getOutputStream());
			this.nomeCliente = entrada.readLine();
			if (armazena(this.nomeCliente)) {
				saida.println("Este nome ja existe! Conecte novamente com outro Nome.");
				this.conexao.close();
				return;
			}
			if (this.nomeCliente == null) {
				return;
			}

			MAP_CLIENTES.put(this.nomeCliente, saida);
			String[] msg = entrada.readLine().split(":");
			
			while (msg != null && !(msg[0].trim().equals(""))) {
				send(saida, " escreveu: ", msg);
				msg = entrada.readLine().split(":");
			}

			String[] out = { " do bate-papo!" };
			send(saida, " saiu", out);
			remove(this.nomeCliente);
			MAP_CLIENTES.remove(this.nomeCliente);
			this.conexao.close();
		} catch (IOException e) {
			System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
		}
	}

	public void send(PrintStream saida, String acao, String[] msg) {
		out: for (Map.Entry<String, PrintStream> cliente : MAP_CLIENTES.entrySet()) {
			PrintStream chat = cliente.getValue();
			if (chat != saida) {
				if (msg.length == 1) {
					//chat.println(this.nomeCliente + acao + msg[0]);
					chat.println(msg[0]);
				} else {
					if (msg[1].equalsIgnoreCase(cliente.getKey())) {
						chat.println(this.nomeCliente + acao + msg[0]);
						break out;
					}
				}
			}
		}
	}
}