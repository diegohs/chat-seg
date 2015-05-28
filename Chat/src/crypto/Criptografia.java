package crypto;
class Criptografia
{
  private int[] K1 = new int[8];
  private int[] K2 = new int[8];
  private int[] pt = new int[8];
  
	void manterDados(String plaintext , int[] k1, int[] k2){
		int[] pt = new int[8];		
		char c1;
		String ts ;

		try{
			for(int i=0;i<8;i++){
				c1 = plaintext.charAt(i);
				ts = Character.toString(c1);
				pt[i] = Integer.parseInt(ts);

				if(pt[i] !=0 && pt[i]!=1){
					System.exit(0);
					return;
				}
			}
		}
		catch(Exception e){
			System.exit(0);
			return ;
		}

		this.pt = pt;
		this.K1 = k1;
		this.K2 = k2;
	}
  
	void permutacao(){
		int[] temp = new int[8];

		temp[0] = pt[1];
		temp[1] = pt[5];
		temp[2] = pt[2];
		temp[3] = pt[0];
		temp[4] = pt[3];
		temp[5] = pt[7];
		temp[6] = pt[4];
		temp[7] = pt[6];

		pt = temp;
	} 
  
	void permutacaoInversa(){
		int[] temp = new int[8];

		temp[0] = pt[3];
		temp[1] = pt[0];
		temp[2] = pt[2];
		temp[3] = pt[4];
		temp[4] = pt[6];
		temp[5] = pt[1];
		temp[6] = pt[7];
		temp[7] = pt[5];

		pt = temp;
	}
  
	int[] mapeamentoF(int[] R, int[] SK){
		
		int[] temp = new int[8];

		// Expansao
		temp[0]  = R[3];
		temp[1]  = R[0];
		temp[2]  = R[1];
		temp[3]  = R[2];
		temp[4]  = R[1];
		temp[5]  = R[2];
		temp[6]  = R[3];
		temp[7]  = R[0];

		// XOR
		temp[0] = temp[0] ^ SK[0];
		temp[1] = temp[1] ^ SK[1];
		temp[2] = temp[2] ^ SK[2];
		temp[3] = temp[3] ^ SK[3];
		temp[4] = temp[4] ^ SK[4];
		temp[5] = temp[5] ^ SK[5];
		temp[6] = temp[6] ^ SK[6];
		temp[7] = temp[7] ^ SK[7];

		// Sub caixas S0 e S1
		final int[][] S0 = { {1,0,3,2} , {3,2,1,0} , {0,2,1,3} , {3,1,3,2} } ;
		final int[][] S1 = { {0,1,2,3},  {2,0,1,3}, {3,0,1,0}, {2,1,0,3}} ;

		int d11 = temp[0];
		int d14 = temp[3];

		int row1 = BinaryOp.BinToDec(d11,d14);

		int d12 = temp[1];
		int d13 = temp[2];
		int col1 = BinaryOp.BinToDec(d12,d13);

		int o1 = S0[row1][col1]; 

		int[] out1 = BinaryOp.DecToBinArr(o1);

		int d21 = temp[4];
		int d24 = temp[7];
		int row2 = BinaryOp.BinToDec(d21,d24);

		int d22 = temp[5];
		int d23 = temp[6];
		int col2 = BinaryOp.BinToDec(d22,d23);

		int o2 = S1[row2][col2];

		int[] out2 = BinaryOp.DecToBinArr(o2); 

		int[] out = new int[4];
		out[0] = out1[0];
		out[1] = out1[1];
		out[2] = out2[0];
		out[3] = out2[1];

		int [] O_Per = new int[4];
		O_Per[0] = out[1];
		O_Per[1] = out[3];
		O_Per[2] = out[2];
		O_Per[3] = out[0];

		return O_Per;
	}
  
	int[] funcaoFK(int[] L, int[] R,int[] SK){
		int[] temp = new int[4];
		int[] out = new int[8];

		temp = mapeamentoF(R,SK);

		out[0] = L[0] ^ temp[0];
		out[1] = L[1] ^ temp[1];
		out[2] = L[2] ^ temp[2];
		out[3] = L[3] ^ temp[3];

		out[4] = R[0];
		out[5] = R[1];
		out[6] = R[2];
		out[7] = R[3];
		
		return out;
	}
  
	int[] trocaFK(int[] in){
		int[] temp = new int[8];
		
		temp[0] = in[4];
		temp[1] = in[5];
		temp[2] = in[6];
		temp[3] = in[7];

		temp[4] = in[0];
		temp[5] = in[1];
		temp[6] = in[2];
		temp[7] = in[3];	

		return temp;
	}

	String criptografar(String plaintext , int[] LK, int[] RK){

		manterDados(plaintext,LK,RK);
		permutacao();

		int[] LH = new int[4];
		int[] RH = new int[4];
		LH[0] = pt[0];
		LH[1] = pt[1];
		LH[2] = pt[2];
		LH[3] = pt[3];

		RH[0] = pt[4];
		RH[1] = pt[5];
		RH[2] = pt[6];
		RH[3] = pt[7];

		int[] r1 = new int[8];
		r1 = funcaoFK(LH,RH,K1);

		int[] temp = new int[8];
		temp = trocaFK(r1);

		LH[0] = temp[0];
		LH[1] = temp[1];
		LH[2] = temp[2];
		LH[3] = temp[3];

		RH[0] = temp[4];
		RH[1] = temp[5];
		RH[2] = temp[6];
		RH[3] = temp[7];

		int[] r2 = new int[8];
		r2 = funcaoFK(LH,RH,K2);
		pt = r2;
		permutacaoInversa();
		
		StringBuilder strBuilder =  new StringBuilder();
		for(int i=0; i<8; i++)
			strBuilder.append(pt[i]);

		return strBuilder.toString() ;
	}
 
}