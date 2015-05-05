package chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import sdes.Sdes;

public class ClienteSocketSDES extends Thread {
    private Socket conexao;
    private static Sdes sdes = new Sdes("1010000010");    

    public ClienteSocketSDES(Socket socket) {
        this.conexao = socket;
    }    
    
    public static void main(String args[]) throws UnsupportedEncodingException
    {
        try {
            Socket socket = new Socket("127.0.0.1", 3000);
            PrintStream saida = new PrintStream(socket.getOutputStream());
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in,"ISO-8859-1"));
            System.out.print("Digite seu nome: ");
            String meuNome = teclado.readLine();
            saida.println(meuNome.toUpperCase());
            Thread thread = new ClienteSocketSDES(socket);
            thread.start();
            String msg, msgCriptografada;
            
            while (true)
            {
                System.out.print("Mensagem > ");
                msg = teclado.readLine();
                msgCriptografada = sdes.criptografar(msg);                
                saida.println(msgCriptografada);
            }
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }

    public void run()
    {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream(),"ISO-8859-1"));
            String msg, msgDescriptografada;            
            while (true)
            {
                msg = entrada.readLine();               
                msgDescriptografada = sdes.descriptografar(msg);                
                System.out.println(msgDescriptografada);
                System.out.print("Responder > ");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma Falha... .. ." + 
                " IOException: " + e);
        }
    }
}