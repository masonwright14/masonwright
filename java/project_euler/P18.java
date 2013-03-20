package firstpage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class P18 {

	static long[][] input;
	static long[][] bestProducts;
	
	// use dynamic programming
	public static void main(String[] args) {
		setupArray();
		setupOutputArray();
		fillProductArray();
		printBottomMax();
	}

	private static void printBottomMax() {
		long max = 1;
		
		for ( int i = 0; i < bestProducts.length; i++ ) {
			long currentValue = bestProducts[ bestProducts.length - 1 ][ i ];
			if ( currentValue > max )
				max = currentValue;
		}
		
		System.out.println( max );
	}

	/*
	 * strategy: fill from top row down, using dynamic programming.
	 * initialize first element to its own value.
	 * value of best product = max of best product to upper-left * self and best product to upper-right * self.
	 * 
	 * upper-left element: one row up, index - 1
	 * upper-right element: one row up, same index
	 */
	private static void fillProductArray() {		
		bestProducts[ 0 ][ 0 ] = input[ 0 ][ 0 ];
		
		for ( int row = 1; row < input.length; row++ ) {
			for ( int column = 0; column <= row; column++ ) {
				long upperLeftSum = 0;
				long upperRightSum = 0;
				
				if ( column > 0 )
					upperLeftSum = bestProducts[ row - 1 ][ column - 1 ];
				if ( column < row )
					upperRightSum = bestProducts[ row - 1 ][ column ];
				
				bestProducts[ row ][ column ] = Math.max( upperLeftSum, upperRightSum ) + input[ row ][ column ];
			}
		}
	}

	private static void setupOutputArray() {
		int size = input.length;
		
		bestProducts = new long[ size ][];
		for ( int i = 0; i < size; i++ ) {
			bestProducts[ i ] = new long[ i + 1 ];
			for ( int j = 0; j < i + 1; j++ ) {
				bestProducts[ i ][ j ] = 0;
			}
		}
	}

	private static void setupArray() {
		try {
			Scanner scan = new Scanner( new File( "p18" ) );
			int rows = 0;
			while ( scan.hasNext() ) {
				rows++;
				scan.nextLine();
			}
			
			input = new long[rows][];
			
			scan = new Scanner( new File( "p18" ) );
			
			int currentRowIndex = 0;
			while ( scan.hasNext() ) {
				String line = scan.nextLine();
				String[] tokens = line.split( " " );
				input[ currentRowIndex ] = new long[ tokens.length ];
				
				for ( int i = 0; i < tokens.length; i++ ) {
					input[ currentRowIndex ][ i ] = Long.parseLong( tokens[ i ] );
				}
				
				currentRowIndex++;
			}
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
