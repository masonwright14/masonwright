package userinterface;

import java.util.Scanner;

public abstract class Utilities {

	private static Scanner SCANNER = null;

	
	public static Scanner getScanner() {
		if ( SCANNER == null ) {
			SCANNER = new Scanner( System.in );
		}
			
		return SCANNER;
	}


	public static String nextYOrN() {
		final String input = getNextLine().toLowerCase();
		if ( input.equals( "y" ) || input.equals( "n" ) )
			return input;

		return null;
	}
	
	
	public static int nextInt() {
		final int input = getNextInt();
		return input;
	}
	
	
	public static String nextCharAsString() {
		final String input = getNextLine();
		return input.substring( 0, 1 );	
	}
	
	
	private static String getNextLine() {
		final Scanner scanner = getScanner();
		return scanner.nextLine();
	}

	
	private static int getNextInt() {
		final Scanner scanner = getScanner();
		return scanner.nextInt();
	}
}