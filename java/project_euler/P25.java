package firstpage;

public class P25 {

	private static int[] termA;
	private static int[] termB;
	private static int[] termTemp;
	
	public static void main(String[] args) {
		termA = new int[ 1001 ];
		termB = new int[ 1001 ];
		termTemp = new int[ 1001 ];
		
		resetTerm( termA );
		resetTerm( termB );
		resetTerm( termTemp );
		
		termA[ termA.length - 1 ] = 1;
		termB[ termB.length - 1 ] = 1;
	}

	private static void resetTerm( int[] aTerm ) {
		for ( int index = 0; index < aTerm.length; index++ )
			aTerm[ index ] = 0;
	}
	
	private static void nextTerm() {
		storeBInTemp();
		addAToB();
		replaceAWithTemp();
	}
	
	private static void replaceAWithTemp() {
		for ( int i = 0; i < termA.length; i++ ) {
			termA[ i ] = termTemp[ i ];
		}
	}

	private static void storeBInTemp() {
		for ( int i = 0; i < termB.length; i++ ) {
			termTemp[ i ] = termB[ i ];
		}
	}

	private static void addAToB() {
		
	}
}