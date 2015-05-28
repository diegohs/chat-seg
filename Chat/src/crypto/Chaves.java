package crypto;
class Chaves{
	private int[] chave = new int[10];
	private int[] k1 = new int[8];
	private int[] k2 = new int[8];
	private boolean flag = false;

	void GerarChaves(String entrada ){
		int[] chave = new int[10];
		char c1;
		String str ;

		try{
			for(int i=0;i<10;i++){
				c1 = entrada.charAt(i);
				str = Character.toString(c1);
				chave[i] = Integer.parseInt(str);

				if(chave[i] !=0 && chave[i]!=1){
					System.exit(0);
					return ;
				}
			}
		}
		catch(Exception e){
			System.exit(0);
			return ;
		}
		
		this.chave = chave;
		permutarP10();
		rotacaoLS1();
		this.k1 = permutarP8();
		rotacaoLS2();
		this.k2 = permutarP8();
		flag = true;
	}
	
	private void permutarP10(){
		int[] temp = new int[10];

		temp[0] = chave[2];
		temp[1] = chave[4];
		temp[2] = chave[1];
		temp[3] = chave[6];
		temp[4] = chave[3];
		temp[5] = chave[9];
		temp[6] = chave[0];
		temp[7] = chave[8];
		temp[8] = chave[7];
		temp[9] = chave[5];

		chave = temp;
	}

	private void rotacaoLS1(){
		int[] temp = new int[10];

		temp[0] = chave[1];
		temp[1] = chave[2];
		temp[2] = chave[3];
		temp[3] = chave[4];
		temp[4] = chave[0];

		temp[5] = chave[6];
		temp[6] = chave[7];
		temp[7] = chave[8];
		temp[8] = chave[9];
		temp[9] = chave[5];

		chave = temp;
	}

	private int[] permutarP8(){
		int[] temp = new int[8];

		temp[0] = chave[5];
		temp[1] = chave[2];
		temp[2] = chave[6];
		temp[3] = chave[3];
		temp[4] = chave[7];
		temp[5] = chave[4];
		temp[6] = chave[9];
		temp[7] = chave[8];

		return temp;
	}

	private void rotacaoLS2(){
		int[] temp = new int[10];

		temp[0] = chave[2];
		temp[1] = chave[3];
		temp[2] = chave[4];
		temp[3] = chave[0];
		temp[4] = chave[1];

		temp[5] = chave[7];
		temp[6] = chave[8];
		temp[7] = chave[9];
		temp[8] = chave[5];
		temp[9] = chave[6];

		chave = temp;
	}

	public int[] getK1(){
	if(!flag){
		return null;
	}
		return k1;
	}

	public int[] getK2(){
		if(!flag){
			return null;
		}
		return k2;
	}
}