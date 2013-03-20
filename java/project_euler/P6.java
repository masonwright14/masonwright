package firstpage;

public class P6 {


	public static void main(String[] args) {
		int input = 100;
		System.out.println( Math.abs( sumOfSquares( input) - squareOfSum( input ) ) );
	}

	
	public static int sumOfSquares( int max ) {
		int sum = 0;
		
		for ( int i = 1; i <= max; i++ ) {
			sum += i * i;
		}
		
		return sum;
	}
	
	public static int squareOfSum( int input ) {
		int sum = 0;
		
		for ( int i = 0; i <= input; i++ ) {
			sum += i;
		}
		
		return sum * sum;
	}
}
