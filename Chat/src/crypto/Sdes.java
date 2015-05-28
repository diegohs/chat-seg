package crypto;

//import java.nio.charset.Charset;

public class Sdes implements Crypto{
	
	private String chave = "";
	private Chaves chaveBuilder = new Chaves();
	
	public Sdes(String chave){
		setChave(chave);
		chaveBuilder.GerarChaves(chave);
	}
	
	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
		chaveBuilder.GerarChaves(chave);
	}

	/*public String criptografar(String texto){			
		String output,  binario;
		Criptografia cripto  = new Criptografia();
		StringBuilder sb = new StringBuilder();
		int ch;
		byte[] saida = new byte[texto.length()];
		
		for(int i=0;i<texto.length();i++){
			ch = texto.charAt(i);
			binario = String.format("%8s",Integer.toBinaryString(ch)).replace(' ','0');
			output = cripto.criptografar( binario, chaveBuilder.getK1(), chaveBuilder.getK2());
			//ch = Integer.parseInt(output,2);
			//sb.append((char)ch);
			//saida = output.getBytes();
			saida[i] = (byte) Integer.parseInt(output,2);
		}
		output = new String(saida,Charset.forName("UTF-8"));
		System.out.println(output);
		return sb.toString();
	}
	
	public String descriptografar(String texto){		
		
		String output,  binario;
		Criptografia cripto  = new Criptografia();
		StringBuilder sb = new StringBuilder();
		int ch;
		
		for(int i=0;i<texto.length();i++){
			ch = texto.charAt(i);
			binario = String.format("%8s",Integer.toBinaryString(ch)).replace(' ','0');
			output = cripto.criptografar( binario, chaveBuilder.getK2(), chaveBuilder.getK1());
			ch = Integer.parseInt(output,2);
			sb.append((char)ch);
		}
		return sb.toString();
	}*/

	@Override
	public void alteraChave(String s) {
		setChave(s);		
	}

	@Override
	public byte[] encrypt(byte[] plaintext) {
		
		String output,  binario;
		Criptografia cripto  = new Criptografia();
		byte[] saida = new byte[plaintext.length];
		
		for(int i=0;i<plaintext.length;i++){
			binario = String.format("%8s", Integer.toBinaryString(plaintext[i] & 0xFF)).replace(' ', '0');
			output = cripto.criptografar( binario, chaveBuilder.getK1(), chaveBuilder.getK2());
			saida[i] = (byte) Integer.parseInt(output,2);
		}
		return saida;
	}

	@Override
	public byte[] decrypt(byte[] plaintext) {
		String output,  binario;
		Criptografia cripto  = new Criptografia();
		byte[] saida = new byte[plaintext.length];
		
		for(int i=0;i<plaintext.length;i++){
			binario = String.format("%8s", Integer.toBinaryString(plaintext[i] & 0xFF)).replace(' ', '0');
			output = cripto.criptografar( binario, chaveBuilder.getK2(), chaveBuilder.getK1());
			saida[i] = (byte) Integer.parseInt(output,2);
		}
		return saida;
	}
	
	/*public static void main(String[] args) {
		Sdes sdes = new Sdes("1010000010");
		String texto = "diego";
		byte[] bytes = texto.getBytes(Charset.forName("UTF-8"));
		bytes = sdes.encrypt(bytes);
		texto = new String(bytes,Charset.forName("UTF-8"));
		System.out.println(texto);
		bytes = sdes.decrypt(bytes);
		texto = new String(bytes,Charset.forName("UTF-8"));
		System.out.println(texto);
	}*/
	
}