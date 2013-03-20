package firstpage;

public class P10 {

	public static void main(String[] args) {
		long currentPrime = 2l;
		long sum = 0l;
		
		while( currentPrime < 2000000 ) {
			sum += currentPrime;
			currentPrime = getNextPrime( currentPrime );
		}
		
		System.out.println( sum );
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
