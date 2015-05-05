package chat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;

import rc4.Rc4;
public class ClienteSocket extends Thread {
    private Socket conexao;

    public ClienteSocket(Socket socket) {
        this.conexao = socket;
    }
    
    public static void main(String args[])
    {
        try {
            Socket socket = new Socket("127.0.0.1", 3000);
            PrintStream saida = new PrintStream(socket.getOutputStream());
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in,"ISO-8859-1"));
            System.out.print("Digite seu nome: ");
            String meuNome = teclado.readLine();
            saida.println(meuNome.toUpperCase());
            Thread thread = new ClienteSocket(socket);
            thread.start();
            String msg;
            byte[] msgCriptografada;
            while (true)
            {
                System.out.print("Mensagem > ");
                msg = teclado.readLine();
                Rc4 rc4 = new Rc4("e04fd020ea3a6910a2d808002b30309d");    	
                msgCriptografada = rc4.encrypt(msg.getBytes(Charset.forName("ISO-8859-1")));
                
                try{
                    msg = new String(msgCriptografada, "ISO-8859-1");
                    saida.println(msg);
                }
                catch(UnsupportedEncodingException e){
                    System.err.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Falha na Conexao... .. ." + " IOException: " + e);
        }
    }

    public void run()
    {
        try {
            BufferedReader entrada = new BufferedReader(new InputStreamReader(this.conexao.getInputStream(),"ISO-8859-1"));
            String msg;
            byte[] msgCriptografada;
            while (true)
            {
                msg = entrada.readLine();               
                Rc4 rc4 = new Rc4("e04fd020ea3a6910a2d808002b30309d");    	
                msgCriptografada = rc4.encrypt(msg.getBytes(Charset.forName("ISO-8859-1")));
                
                try{
                    msg = new String(msgCriptografada, "ISO-8859-1");
                    System.out.println(msg);
                }
                catch(UnsupportedEncodingException e){
                    System.err.println(e.getMessage());
                }
                System.out.print("Responder > ");
            }
        } catch (IOException e) {
            System.out.println("Ocorreu uma Falha... .. ." + 
                " IOException: " + e);
        }
    }
}