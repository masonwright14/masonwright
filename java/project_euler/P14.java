package firstpage;

public class P14 {
	
	// use dynamic programming to speed up results
	static long[] results = new long[1000000];

	public static void main(String[] args) {
		
		long maxLength = 0l;
		long maxInput = 0l;
		
		for ( int i = 1; i < 1000000; i++ ) {
			results[ i ] = collatz( i );
			if ( results[ i ] > maxLength ) {
				maxLength = results[ i ];
				maxInput = i;
			}
		}
		
		System.out.println( maxInput + " " + maxLength );
	}

	
	public static long collatz( final long initial ) {
		int length = 1;
		long value = initial;
		
		while ( value != 1 ) {
			if ( value % 2 == 0 ) {
				value /= 2;
				if ( value < 1000000 ) {
					if ( results[ (int) value ] != 0 ) {
						return length + results[ (int) value ];
					}
				}
			}
			else {
				value = value * 3 + 1;
			}
			length++;
		}
		
		return length;
	}
}
