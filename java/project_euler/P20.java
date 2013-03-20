package firstpage;

public class P20 {

	private static int ARRAY_LENGTH = 200;
	private static int[] output = new int[ ARRAY_LENGTH ];
	private static int[] carries = new int[ ARRAY_LENGTH ];

	public static void main(String[] args) {
		initializeOutput();
		resetCarries();
		
		for ( int i = 1; i <= 100; i++ ) {
			multiplyBy( i );
		}
		
		printDigitSum();
		printOutput();
	}
	
	
	private static void printDigitSum() {
		int total = 0;
		for ( int i = 0; i < output.length; i++ ) {
			total += output[ i ];
		}
		
		System.out.println( total );
	}
	
	
	private static void printOutput() {
		StringBuilder sb = new StringBuilder();;
		
		int index = 0;
		while ( output[ index ] == 0 ) {
			index++;
		}
		while ( index < output.length ) {
			sb.append( output[ index ] );
			index++;
		}
		
		System.out.println( sb.toString() );
	}
	
	
	private static void resetCarries() {
		for ( int i = 0; i < carries.length; i++ )
			carries[ i ] = 0;
	}
	
	
	private static void addCarries() {
		for ( int i = carries.length - 2; i > 0; i-- ) {
			output[ i ] += carries[ i ];
			while ( output[ i ] >= 10 ) {
				output[ i ] -= 10;
				carries[ i - 1 ] += 1;
			}
		}
	}


	private static void initializeOutput() {
		for ( int i = 0; i < output.length; i++ )
			output[ i ] = 0;
		
		output[ output.length - 1 ] = 1; 
	}


	private static void multiplyBy( final int factor ) {
		if ( factor > 100 || factor < 0 )
			throw new IllegalArgumentException();
		
		resetCarries();		
		
		for ( int index = 2; index < output.length; index++ ) {
			int product = factor * output[ index ];
			
			int onesProduct = product % 10;
			product /= 10;
			int tensProduct = product % 10;
			product /= 10;
			int hundredsProduct = product;
			
			output[ index ] = onesProduct;
			carries[ index - 1 ] += tensProduct;
			carries[ index - 2 ] += hundredsProduct;
		}
		
		addCarries();
	}
}
