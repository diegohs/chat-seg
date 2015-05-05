package sdes;
class BinaryOp
{
  static int BinToDec(int...bits){
     int temp=0;
     int base = 1;
     for(int i=bits.length-1 ; i>=0;i--){
        temp = temp + (bits[i]*base);
        base = base * 2 ;
     }
	 return temp;
  }
  
  static int[] DecToBinArr(int no){
    if(no==0){
		int[] zero = new int[2];
		zero[0] = 0;
		zero[1] = 0;
		return zero;	
	}
    int[] temp = new int[10] ;
	int count = 0 ;
    for(int i= 0 ; no!= 0 ; i++){
      temp[i] = no % 2;
      no = no/2;
	  count++;
    }
    
	int[] temp2 = new int[count];
	
	for(int i=count-1, j=0;i>=0 && j<count;i--,j++){
		temp2[j] = temp[i];
	}
	
	if(count<2){
		temp = new int[2];
		temp[0] = 0;
		temp[1] = temp2[0];
		return temp;
	}
	 
	return temp2;
  }
}