package firstpage;

public class P4 {


	public static void main(String[] args) {

		int maxSoFar = 0;
		int iMax = 0;
		int jMax = 0;
		
		for ( int i = 100; i <= 999; i++ ) {
			for ( int j = 100; j <= 999; j++ ) {
				if ( i * j > maxSoFar) {
					if ( isPallindrome( i * j )  ) {
						maxSoFar = i * j;
						iMax = i;
						jMax = j;
					}
				}
			}
		}
		
		System.out.println( maxSoFar + ": " + iMax + " x " + jMax );
	}
	
	public static boolean isPallindrome( int input ) {
		if ( input >= 100000 )
			return isPallindrome6( input );
		else
			return isPallindrome5( input );
	}

	private static boolean isPallindrome5( int input ) {
		int d1 = input / 10000;
		int d2 = (input / 1000) % 10;
		int d4 = (input / 10) % 10;
		int d5 = input % 10;

		return d1 == d5 && d2 == d4;
	}

	private static boolean isPallindrome6(int input) {
		int d1 = input / 100000;
		int d2 = (input / 10000) % 10;
		int d3 = (input / 1000) % 10;
		int d4 = (input / 100) % 10;
		int d5 = (input / 10) % 10;
		int d6 = input % 10;

		return d1 == d6 && d2 == d5 && d3 == d4;
	}
}
