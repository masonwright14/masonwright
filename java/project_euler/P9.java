package firstpage;

public class P9 {


	public static void main(String[] args) {
		
		for ( int a = 1; a < 1000; a++ ) {
			for ( int b = 1; b * b + a * a < 1000000; b++ ) {
				double root = Math.sqrt( a * a + b * b );
				if ( isInteger( root ) ) {
					if ( a + b + Math.floor( root ) == 1000 ) {
						System.out.println( a + " " + b + " " + Math.floor( root ) );
						System.out.println( a * b * Math.floor( root ) );
						break;
					}
				}
			}
		}
		
	}
	
	public static boolean isInteger( double input ) {
		double tolerance = 0.0001;
		return Math.abs( input - Math.floor( input ) ) < tolerance;
	}
}
