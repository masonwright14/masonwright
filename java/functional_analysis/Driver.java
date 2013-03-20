package functionalAnalysis;

public final class Driver {

	
	public static void main( final String[] args ) {
		final double timeInMicroseconds =
			1000000.0 // second
			* 60.0 // minute
			* 60.0 // hour
			* 24.0 // day
			* 30.0 // month
			* 12.0 // year (approximate)
			* 100.0 // century (approximate)
			;
		System.out.println();
		System.out.println( "Time in microseconds:" );
		System.out.println( timeInMicroseconds );
		
		final double squareResult = timeInMicroseconds * timeInMicroseconds;
		System.out.println();
		System.out.println( "Number of cycles of sqrt n algorithm:" );
		System.out.println( squareResult );
		
		final double nLgNResult = Analysis.solveForNLgNEquals( timeInMicroseconds );
		System.out.println();
		System.out.println( "Number of cycles of n lg n algorithm:" );
		System.out.println( nLgNResult );
		
		System.out.println();
		System.out.println( "Conterting back to original time via n lg n:");
		System.out.println( Analysis.getLg( nLgNResult ) * nLgNResult );
		
		final double sqrtResult = Math.sqrt( timeInMicroseconds );
		System.out.println();
		System.out.println( "Number of cycles of n ^ 2 algorithm:" );
		System.out.println( sqrtResult );
		
		final double cbrtResult = Math.cbrt( timeInMicroseconds );
		System.out.println();
		System.out.println( "Number of cycles of n ^ 3 algorithm:" );
		System.out.println( cbrtResult );
		
		final double powerTwoResult = Analysis.solveForTwoPowerFloor( timeInMicroseconds );
		System.out.println();
		System.out.println( "Number of cycles of 2 ^ n algorithm:" );
		System.out.println( powerTwoResult );
		
		final double factorialResult = Analysis.solveForFactorialFloor( timeInMicroseconds );
		System.out.println();
		System.out.println( "Number of cycles of n! algorithm:" );
		System.out.println( factorialResult );
	}
}
