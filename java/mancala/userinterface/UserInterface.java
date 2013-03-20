package userinterface;

import board.Board;
import player.Player;
import tester.Tester;

public abstract class UserInterface {
	

	public static void start() {
		// if test mode, ask more prompts about test mode
		if ( promptWhichMode() )
			promptForTestMode();
		else
			promptForPlayMode();
	}
	
	
	public static int humanSelectMovePieRule() {
		return promptForMovePieRule();
	}


	private static int promptForMovePieRule() {
		final String prompt = "Enter a house to move from (A-F), or S to swap sides: ";
		displayText( prompt );
		String input = Utilities.nextCharAsString().toLowerCase();
		while( 
			( input.compareTo( "a" ) < 0 || input.compareTo( "f" ) > 0 ) && 
			! input.equals( "s" ) 
		) {
			displayText( "Invalid input. " + prompt );
			input = Utilities.nextCharAsString().toLowerCase();
		}
		
		return getMoveSelectionInt( input );
	}
	
	
	private static int getMoveSelectionInt( final String selectionString ) {
		final String input = selectionString.toLowerCase();
		
		if ( input.equals( "a" ) )
			return 0;
		if ( input.equals( "b" ) )
			return 1;
		if ( input.equals( "c" ) )
			return 2;
		if ( input.equals( "d" ) )
			return 3;
		if ( input.equals( "e" ) )
			return 4;
		if ( input.equals( "f" ) )
			return 5;
		if ( input.equals( "s" ) )
			return Player.PIE_SWAP_INT;
		throw new IllegalArgumentException();
	}


	/*
	 * display board with markers along South
	 * display prompt for next move
	 * keep prompting until valid selection made
	 *    selection must be A-F or a-f
	 * return "a" - "f"
	 */
	public static int humanSelectMoveNoPieRule() {
		return promptForMoveNoPieRule();
	}
	
	
	public static int humanSelectMoveWithPieRule() {
		return promptForMoveWithPieRule();
	}
	
	
	public static void displayForBonusMove() {
		displayText( "Bonus move!" );
	}
	
	
	private static int promptForMoveWithPieRule() {
		final String prompt = "Enter a house to move from (A-F), or S to swap: ";
		displayText( prompt );
		String input = Utilities.nextCharAsString().toLowerCase();
		while( 
			( input.compareTo( "a" ) < 0 || input.compareTo( "f" ) > 0 ) &&
			( ! input.equals( "s" ) )
		) {
			displayText( "Invalid input. " + prompt );
			input = Utilities.nextCharAsString().toLowerCase();
		}
		
		return getMoveSelectionInt( input );		
	}


	private static int promptForMoveNoPieRule() {
		final String prompt = "Enter a house to move from (A-F): ";
		displayText( prompt );
		String input = Utilities.nextCharAsString().toLowerCase();
		while( input.compareTo( "a" ) < 0 || input.compareTo( "f" ) > 0 ) {
			displayText( "Invalid input. " + prompt );
			input = Utilities.nextCharAsString().toLowerCase();
		}
		
		return getMoveSelectionInt( input );
	}


	private static void promptForTestMode() {
		int depth = promptForDepth();
		Tester.setMaxDepth( depth );
		Board.setSeedsPerHouse( Board.MAX_SEEDS_PER_HOUSE );
		Tester.runTest();
	}
	
	
	private static void promptForPlayMode() {
		final int depth = promptForDepth();
		final int seeds = promptForSeeds();
		final boolean isFirstPlayerHuman = promptWhichPlayer();
		
		Player.setDepth( depth );
		Board.setSeedsPerHouse( seeds );
		Player.play( isFirstPlayerHuman );
	}
	
	
	private static int promptForSeeds() {
		return intPrompt( 1, 6, "Enter seeds per house: (1-6) " );
	}
	
	
	private static int promptForDepth() {
		return intPrompt( 1, Player.MAX_DEPTH, "Enter max search depth: (1-" + Player.MAX_DEPTH + ") " );
	}
	
	
	private static boolean promptWhichPlayer() {
		return booleanPrompt( "Will you move first? (Y or N) " );
	}
	
	
	private static boolean promptWhichMode() {
		return booleanPrompt( "Run test mode? (Y or N) " );
	}
	
	
	public static void displayText( final String text ) {
		System.out.println( text );
	}
	
	
	private static int intPrompt(
		final int minResult,
		final int maxResult,
		final String prompt
	) {
		displayText( prompt );
		int result = Utilities.nextInt();
		while( result < minResult || result > maxResult ) {
			displayText( "Invalid input. " + prompt );
			result = Utilities.nextInt();
		}
		
		return result;
	}
	
	
	private static boolean booleanPrompt(
		final String prompt
	) {
		displayText( prompt );
		String userResponse = Utilities.nextYOrN();
		while( userResponse == null ) {
			displayText( "Invalid input. " + prompt );
			userResponse = Utilities.nextYOrN();
		}
		
		if ( userResponse.equals( "y" ) )
			return true;
		
		return false;	
	}


	public static void displayResult( 
		int aSouthDepth, 
		int aNorthDepth,
		boolean aIsSouthFirst, 
		int southScore, 
		int northScore 
	) {
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "South depth: ").append( aSouthDepth );
			displayText( sb.toString() );
		}
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "North depth: ").append( aNorthDepth );
			displayText( sb.toString() );
		}
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "Is South first: ").append( aIsSouthFirst );
			displayText( sb.toString() );
		}
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "South score: ").append( southScore );
			displayText( sb.toString() );
		}
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "North score: ").append( northScore );
			displayText( sb.toString() );
		}
		{
			if ( northScore > southScore )
				displayText( "Winner: North" );
			else if ( southScore > northScore )
				displayText( "Winner: South" );
			else
				displayText( "Tie" );
		}
		
		displayText("");
	}
}
