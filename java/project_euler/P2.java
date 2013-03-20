package firstpage;

public class P2 {

	public static void main( String[] args ) {
		long sum = 0;
		
		long fibLow = 1;
		long fibHigh = 1;
		
		while ( fibHigh <= 4000000 ) {
			if ( fibHigh % 2 == 0 ) {
				sum += fibHigh;
			}
			
			long temp = fibHigh;
			fibHigh = fibLow + fibHigh;
			fibLow = temp;
		}
		
		System.out.println( sum );
	}
}
