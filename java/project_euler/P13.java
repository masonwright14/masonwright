package firstpage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class P13 {

	static String[] inputArray;
	static long[] longArray;

	public static void main(String[] args) {
		inputArray = getTextInput();
		longArray = getLongArray( inputArray );
		
		long sum = 0l;
		for ( long currentLong: longArray ) {
			sum += currentLong;
		}
		System.out.println( sum );
	}

	private static long[] getLongArray(String[] inputArray ) {
		long[] result = new long[100];
	
		int index = 0;
		for ( String currentString: inputArray ) {
			String firstFifteen = currentString.substring( 0, 15 );
			long longDigits = Long.parseLong( firstFifteen );
			result[ index ] = longDigits;
			index++;
		}
		
		return result;
	}

	private static String[] getTextInput() {
		String[] result = new String[100];

		try {
			Scanner scan = new Scanner( new File( "p13" ) );
			
			int index = 0;
			while ( scan.hasNext() ) {
				result[ index ] = scan.nextLine();
				index++;
			}
			
		} catch ( FileNotFoundException e ) {
			e.printStackTrace();
		}
		
		return result;
	}
}
