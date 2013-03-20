package functionalAnalysis;

public abstract class Analysis {

	private static double TOLERANCE = 0.4;
	
	
	public static double getLg( final double input ) {
		return Math.log( input ) / Math.log( 2.0 );
	}
	
	
	public static double solveForNLgNEquals( final double input ) {
		
		return solveNLgNEqualsIterate( input );
		
		/*
		return solveNLgNEqualsRecurse(
			input,
			0.0,
			input * input
		);
		*/
	}
	
	
	public static double solveForTwoPowerFloor( final double input ) {
		int guess = 0;
		
		while ( true ) {
			double power = Math.pow( 2, guess );
			
			if ( power > input )
				return guess - 1;
			
			guess++;
		}
	}
	
	
	public static double getFactorial( final double input ) {
		double result = 1.0;
		int counter = ( int ) input;
		
		while ( counter > 1 ) {
			result *= counter;
			counter--;
		}
		
		return result;
	}
	
	
	public static double solveForFactorialFloor( final double input ) {
		int guess = 1;
		
		while ( true ) {
			double factorial = getFactorial( guess );
			
			if ( factorial > input )
				return guess - 1;
			
			guess++;
		}
	}
	
	
	private static double solveNLgNEqualsIterate( 
			final double target
		) {
			double lowGuess = 0.0;
			double highGuess = target * target;
			
			while( true ) {
				double currentGuess = getAverage( lowGuess, highGuess );
				double currentResult = currentGuess * getLg( currentGuess );
				
				if ( withinTolerance( currentResult, target ) )
					return currentGuess;
				
				if ( currentResult < target )
					lowGuess = currentGuess;
				else
					highGuess = currentGuess;
			}
		}
	
	
	@SuppressWarnings("unused")
	private static double solveNLgNEqualsRecurse( 
		final double target,
		final double lowGuess,
		final double highGuess
	) {
		double currentGuess = getAverage( lowGuess, highGuess );
		double currentResult = currentGuess * getLg( currentGuess );
		
		if ( withinTolerance( currentResult, target ) )
			return currentGuess;
		
		if ( currentResult < target )
			return solveNLgNEqualsRecurse (
				target,
				currentGuess,
				highGuess
			);
		
		return solveNLgNEqualsRecurse (
			target,
			lowGuess,
			currentGuess
		);
	}
	
	
	private static boolean withinTolerance( final double firstNum, final double secondNum ) {
		return getDifference( firstNum, secondNum ) <= TOLERANCE;
	}
	
	
	private static double getDifference( final double firstNum, final double secondNum ) {
		double result = Math.abs( firstNum - secondNum );
		return result;
	}
	
	
	private static double getAverage( final double firstNum, final double secondNum ) {
		return ( firstNum + secondNum ) / 2.0;
	}
}
