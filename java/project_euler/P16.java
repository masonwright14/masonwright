package firstpage;

public class P16 {

	private static int[] output;
	
	public static void main( String[] args ) {
		setupOutput();
		
		int input = 1000;
		arrayPower( input );
		printResult();
		printSum();
	}
	
	private static void printSum() {
		int sum = 0;
		
		for ( int i = 0; i < output.length; i++ ) {
			sum += output[ i ];
		}
		
		System.out.println( sum );
	}

	private static void printResult() {
		int index = 0;
		while ( output[ index ] == 0 ) {
			index++;
		}
		
		while ( index < output.length ) {
			System.out.print( output[ index ] );
			index++;
		}
		System.out.println();
	}

	private static void arrayPower(int input) {
		for ( int i = input; i>= 1; i-- ) {
			arrayPowerStep();
		}
	}

	private static void arrayPowerStep() {
		for ( int index = 0; index < output.length; index++ ) {
			output[ index ] *= 2;
			if ( output[ index ] >= 10 ) {
				output[ index ] -= 10;
				output[ index - 1 ] += 1; // add 1 by default because multiplying 5 * 2 through 9 * 2 all add just 1
			}
		}
	}

	private static void setupOutput() {
		output = new int[ 1000 ];
		for ( int i = 0; i < output.length; i++ ) {
			output[ i ] = 0;
		}
		output[ output.length - 1 ] = 1;
	}
}
