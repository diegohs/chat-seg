package crypto;

import java.math.BigDecimal;
import java.util.Random;

public class DiffieHellman {
	
	private int q=353, alpha=3, privX, pubY, K;
	
	public DiffieHellman(){
		Random randomGenerator = new Random();
		privX = randomGenerator.nextInt(q+1);
		
		BigDecimal bigQ = new BigDecimal(q);
		BigDecimal temp = new BigDecimal(alpha).pow(privX).remainder(bigQ);
		
		pubY = temp.intValue();
	}
	
	public int getPubY(){
		return pubY;
	}
	
	public String calcK(int y){
		
		BigDecimal bigQ = new BigDecimal(q);
		BigDecimal temp = new BigDecimal(y).pow(privX).remainder(bigQ);
		K = temp.intValue();
		return String.format("%10s", Integer.toBinaryString(K)).replace(' ', '0');
	}
	
	public void calcY(){
		BigDecimal bigQ = new BigDecimal(q);
		BigDecimal temp = new BigDecimal(alpha).pow(privX).remainder(bigQ);
		pubY = temp.intValue();
	}
	
	public static void main(String[] args) {
		DiffieHellman dfA = new DiffieHellman();
		DiffieHellman dfB = new DiffieHellman();
		
		System.out.println(dfA.calcK(dfB.getPubY()));
		System.out.println(dfB.calcK(dfA.getPubY()));
		
		String msg;
		msg = String.valueOf(dfA.getPubY());
		System.out.println(dfB.calcK(Integer.valueOf(msg)));
	}
}
