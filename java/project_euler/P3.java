package firstpage;

public class P3 {
	
	
	public static void main( String[] args ) {
		final long initialTarget = 600851475143l;
		long target = initialTarget;
		
		long largestFactorYet = 1l;
		long currentFactor = 2l;
		
		while ( currentFactor * currentFactor <= target ) {
			if ( target % currentFactor == 0 ) {
				target /= currentFactor;
				largestFactorYet = currentFactor;
			}
			else {
				currentFactor = getNextPrime( currentFactor );
			}
		}
		
		long result = Math.max( largestFactorYet, target );
		
		System.out.println( result );
	}
	
	public static long getNextPrime( long lastPrime ) {
		long guess = lastPrime + 1;
		boolean isPrime = false;
		
		while ( ! isPrime ) {
			isPrime = true;
		
			for ( long i = 2; i * i <= guess && isPrime; i++ ) {
				if ( guess % i == 0 ) {
					isPrime = false;
					guess++;
				}
			}
		}
		
		
		return guess;
	}
}
