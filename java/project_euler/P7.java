package firstpage;

public class P7 {

	
	public static void main( String[] args ) {
		final int max = 10001;
		int currentIndex = 1;
		long currentNumber = 2;
		
		while ( currentIndex < max ) {
			currentNumber = getNextPrime( currentNumber );
			currentIndex = currentIndex + 1;
		}
		
		System.out.println( currentNumber );
	}
	
	
	public static long getNextPrime( long lastPrime ) {
		long guess = lastPrime + 1;
		boolean isPrime = false;
		
		while ( ! isPrime ) {
			isPrime = true;
		
			for ( long i = 2; i * i <= guess && isPrime; i++ ) {
				if ( guess % i == 0 ) {
					isPrime = false;
					guess = guess + 1;
				}
			}
		}
		
		
		return guess;
	}
}
