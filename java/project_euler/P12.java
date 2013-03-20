package firstpage;

public class P12 {

	public static void main(String[] args) {

		int currentTriangleNumber = 1;
		int currentToAdd = 2;
		while( countFactors( currentTriangleNumber ) <= 500 ) {
			currentTriangleNumber += currentToAdd;
			currentToAdd++;
		}
		
		System.out.println( currentTriangleNumber );
	}


	public static int countFactors( final int input ) {
		int target = input;
		
		int runningProduct = 1;
		long currentFactor = 2l;
		int currentFactorCount = 0;
		
		while ( target >= currentFactor ) {
			if ( target % currentFactor == 0 ) {
				target /= currentFactor;
				currentFactorCount++;
			} else {
				runningProduct *= ( currentFactorCount + 1 );
				currentFactor = getNextPrime( currentFactor );
				currentFactorCount = 0;
			}
		}
		
		runningProduct *= ( currentFactorCount + 1 );
		
		return runningProduct;
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
