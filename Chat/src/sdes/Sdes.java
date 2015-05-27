package sdes;

public class Sdes{
	
	private String chave = "1010000010";
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

	public String criptografar(String texto){		
		
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
			saida = output.getBytes();
		}
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
	}
	
}