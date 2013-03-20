package board;

import java.util.ArrayList;

import userinterface.UserInterface;

public class Board {

	private static int NUM_HOUSES = 14;
	private static int SOUTH_STORE = 6;
	private static int NORTH_STORE = 13;
	public static final int MAX_SEEDS_PER_HOUSE = 6;
	public static final int WIN_VALUE = 100;
	public static final int LOSS_VALUE = -100;
	private int[] houses;
	private static Board myBoard;
	private static int seedsPerHouse;
	private static ArrayList<Integer> southIndexesList = null;
	private static ArrayList<Integer> northIndexesList = null;

	
	public Board( final int seedsPerHouse ) {
		this.houses = new int[ NUM_HOUSES ];
		for ( int i = 0; i < SOUTH_STORE; i++ )
			this.houses[ i ] = seedsPerHouse;
		
		for ( int i = SOUTH_STORE + 1; i < NORTH_STORE; i++ )
			this.houses[ i ] = seedsPerHouse;
	}
	
	
	public static int[] getBoard() {
		return cloneArray( myBoard.houses );
	}
	
	
	public static int getSouthScore() {
		return myBoard.houses[ SOUTH_STORE ];
	}
	
	
	public static int getNorthScore() {
		return myBoard.houses[ NORTH_STORE ];
	}
	
	
	public static int getValue( final int[] boardState, final boolean isSouthPerspective ) {
		int southValue = boardState[ SOUTH_STORE ] - boardState[ NORTH_STORE ];
		
		if ( isGameOver( boardState ) ) {
			southValue = getSouthValueAfterGameOver( boardState );
			
			if ( isSouthPerspective ) {
				if ( southValue > 0 )
					return WIN_VALUE;
				else if ( southValue < 0 )
					return LOSS_VALUE;
			}
			else {
				if ( southValue > 0 )
					return LOSS_VALUE;
				else if ( southValue < 0 )
					return WIN_VALUE;
			}
		}
		
		if( isSouthPerspective )
			return southValue;
		
		return southValue * -1;	
	}
	
	
	private static int getSouthValueAfterGameOver( final int[] boardState ) {
		int southValue = 0;
		for ( int i = 0; i <= SOUTH_STORE; i++ )
			southValue += boardState[ i ];
		
		for ( int j = SOUTH_STORE + 1; j <= NORTH_STORE; j++ )
			southValue -= boardState [ j ];
		
		return southValue;
	}


	public static ArrayList<Integer> getHouseIndexesForPlayer( final boolean isSouth ) {
		if ( isSouth )
			return getSouthIndexesList();
		
		return getNorthIndexesList();
	}
	
	
	public static ArrayList<Integer> getNextHousesToMoveForPlayer( final int[] boardState, final boolean isSouth ) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		ArrayList<Integer> indexesToMove;
		
		if ( isSouth )
			indexesToMove = getSouthIndexesList();
		else	
			indexesToMove = getNorthIndexesList();
		
		for ( Integer currentIndex: indexesToMove ) {
			if ( boardState[ currentIndex ] != 0 )
				result.add( currentIndex );
		}
		
		if ( result.isEmpty() )
			throw new IllegalArgumentException();
			
		return result;
	}
	
	
	public static ArrayList<int[]> getNextBoardStatesForPlayer( final int[] boardState, final boolean isSouth ) {
		ArrayList<int[]> result = new ArrayList<int[]>();
		ArrayList<Integer> indexesToMove;
		
		if ( isSouth )
			indexesToMove = getSouthIndexesList();
		else	
			indexesToMove = getNorthIndexesList();
		
		for ( Integer currentIndex: indexesToMove ) {
			result.add( makeMove( boardState, currentIndex ) );
		}
		
		return result;
	}
	
	
	private static ArrayList<Integer> getNorthIndexesList() {
		if ( northIndexesList == null ) {
			northIndexesList = new ArrayList<Integer>();
			northIndexesList.add( 7 );
			northIndexesList.add( 8 );
			northIndexesList.add( 9 );
			northIndexesList.add( 10 );
			northIndexesList.add( 11 );
			northIndexesList.add( 12 );
		}
		
		return northIndexesList;
	}


	private static ArrayList<Integer> getSouthIndexesList() {
		if ( southIndexesList == null ) {
			southIndexesList = new ArrayList<Integer>();
			southIndexesList.add( 0 );
			southIndexesList.add( 1 );
			southIndexesList.add( 2 );
			southIndexesList.add( 3 );
			southIndexesList.add( 4 );
			southIndexesList.add( 5 );
		}
		
		return southIndexesList;
	}
	
	
	public int getValue( boolean isSouthPerspective ) {
		return getValue( this.houses, isSouthPerspective );
	}
	
	
	public static void setupBoard() {
		myBoard = new Board( seedsPerHouse );
	}
	
	
	public static boolean hasSeeds( final int house ) {
		return myBoard.houses[ house ] != 0;
	}
	
	
	public static void setSeedsPerHouse( final int seeds ) {
		if ( seeds > MAX_SEEDS_PER_HOUSE )
			throw new IllegalArgumentException();
		
		seedsPerHouse = seeds;	
	}
	
	
	private static int getOppositeHouse( final int house ) {
		return NORTH_STORE - 1 - house;
	}
	
	
	private static boolean isOwnSide( final int house, final boolean isSouth ) {
		if ( isSouth )
			return house < SOUTH_STORE;
		
		return house > SOUTH_STORE && house < NORTH_STORE;
	}
	
	
	public static boolean isGameOver() {
		return isGameOver( myBoard.houses );
	}
	
	
	public static boolean isGameOver( final int[] boardState ) {
		boolean southEmpty = true;
		for( int i = 0; i < SOUTH_STORE; i++ )
			// if south not empty
			if ( boardState[ i ] != 0 ) {
				southEmpty = false;
				continue;
			}
		if ( southEmpty )
			return true;
		
		for( int i = SOUTH_STORE + 1; i < NORTH_STORE; i++ )
			// if north not empty either
			if ( boardState[ i ] != 0 )
				return false;
		
		return true;
	}
	
	
	private static boolean isSouthSide( final int house ) {
		return house < SOUTH_STORE;
	}
	
	
	/*
	 * error if house is not 0-5 or 7-12
	 * get seedsLeft from number in house
	 *    error if 0
	 * set seeds in house to 0
	 * set currentHouse house + 1
	 * set southMoving to true if 0-5, else false
	 * while seedsLeft > 0
	 *    increment seeds in currentHouse, decrement seedsLeft
	 *    increment currentHouse
	 *    if currentHouse = SOUTH_STORE and ! southMoving, increment again
	 *    if currentHouse = NORTH_STORE and southMoving, increment again
	 *    if currentHouse > 13, set to 0
	 * if isOwnSide( southMoving, currentHouse ) and houses[ currentHouse ] == 1
	 *    move seeds from currentHouse to store (SOUTH_STORE if southMoving)
	 *    move seeds from getOppositeHouse( currentHouse ) to store (SOUTH_STORE if southMoving)
	 * return currentHouse == SOUTH_STORE || currentHouse == NORTH_STORE && ! isGameOver()
	 */
	public static boolean makeMove( final int house ) {
		if ( house < 0 || house >= NORTH_STORE )
			throw new IllegalArgumentException();
		
		int seedsLeft = myBoard.houses[ house ];
		if ( seedsLeft == 0 )
			throw new IllegalArgumentException();
		
		myBoard.houses[ house ] = 0;
		int currentHouse = house;
		final boolean isSouthMoving = isSouthSide( house );
		
		while ( seedsLeft > 0 ) {
			currentHouse = moveToNextHouse( currentHouse, isSouthMoving );
			myBoard.houses[ currentHouse ]++;
			seedsLeft --;
		}

		if ( canCapture( myBoard.houses, currentHouse, isSouthMoving ) )
			capture( myBoard.houses, currentHouse );
		
		return producesBonusMove( currentHouse );
	}
	
	
	public static void makeMove( final int[] nextState ) {
		myBoard.houses = nextState;
	}
	
	
	public static void doPieSwap() {
		for ( int i = 0; i <= SOUTH_STORE; i++ ) {
			int temp = myBoard.houses[ i ];
			myBoard.houses[ i ] = myBoard.houses[ SOUTH_STORE + 1 + i ];
			myBoard.houses[ SOUTH_STORE + 1 + i ] = temp;
		}
			
	}
	
	
	public static boolean producesBonusMove( final int[] boardState, final int house ) {
		int[] boardStateCopy = cloneArray( boardState );
		
		if ( house < 0 || house >= NORTH_STORE || house == SOUTH_STORE )
			throw new IllegalArgumentException();
		
		int seedsLeft = boardStateCopy[ house ];
		if ( seedsLeft == 0 )
			throw new IllegalArgumentException();
		
		boardStateCopy[ house ] = 0;
		
		int currentHouse = house;
		final boolean isSouthMoving = isSouthSide( house );
		
		while ( seedsLeft > 0 ) {
			currentHouse = moveToNextHouse( currentHouse, isSouthMoving );
			boardStateCopy[ currentHouse ]++;
			seedsLeft--;
		}
		
		return (
			( currentHouse == SOUTH_STORE || currentHouse == NORTH_STORE ) &&
			! isGameOver( boardStateCopy )
		);
	}
	
	
	public static int[] makeMove( final int[] priorState, final int house ) {	
		int[] inputState = cloneArray( priorState );
		
		if ( house < 0 || house >= NORTH_STORE )
			throw new IllegalArgumentException();
		
		int seedsLeft = inputState[ house ];
		if ( seedsLeft == 0 )
			throw new IllegalArgumentException();
		
		inputState[ house ] = 0;
		int currentHouse = house;
		final boolean isSouthMoving = isSouthSide( house );
		
		while ( seedsLeft > 0 ) {
			currentHouse = moveToNextHouse( currentHouse, isSouthMoving );
			inputState[ currentHouse ]++;
			seedsLeft --;
		}

		if ( canCapture( inputState, currentHouse, isSouthMoving ) )
			capture( inputState, currentHouse );
		
		return inputState;
	}
	
	
	private static int[] cloneArray( final int[] input ) {
		int[] output = new int[ input.length ];
		for ( int i = 0; i < input.length; i++ ) {
			output[ i ] = input[ i ];
		}
		
		return output;
	}
	
	
	public static void displayBoard() {
		displayNorth();
		displayStores();
		displaySouth();
		displayMarkers();
		UserInterface.displayText( "" );
		UserInterface.displayText( "" );
	}
	
	
	private static void displayMarkers() {
		final String s = "\t\ta\tb\tc\td\te\tf";
		UserInterface.displayText( s );	
	}


	private static void displaySouth() {
		StringBuilder builder = new StringBuilder();
		builder.append( "\t\t" );
		for( int i = 0; i < SOUTH_STORE; i++ ) {
			builder.append( getStringValue( myBoard.houses[ i ] ) );
			builder.append( "\t" );
		}
		
		UserInterface.displayText( builder.toString() );		
	}


	private static void displayStores() {
		StringBuilder builder = new StringBuilder();
		builder.append( "NORTH: " );
		builder.append( getStringValue( myBoard.houses[ NORTH_STORE ] ) );
		builder.append( "\t\t\t\t\t\t\t" );
		builder.append( "SOUTH: " );
		builder.append( getStringValue( myBoard.houses[ SOUTH_STORE  ] ) );
		UserInterface.displayText( builder.toString() );
	}


	private static void displayNorth() {
		StringBuilder builder = new StringBuilder();
		builder.append( "\t\t" );
		for( int i = NORTH_STORE - 1; i > SOUTH_STORE; i-- ) {
			builder.append( getStringValue( myBoard.houses[ i ] ) );
			builder.append( "\t" );
		}
		
		UserInterface.displayText( builder.toString() );
	}


	private static Object getStringValue( final int intValue ) {
		if ( intValue != 0 )
			return intValue + "";
		return "_";
	}


	/*
	 * return currentHouse == SOUTH_STORE || currentHouse == NORTH_STORE && ! isGameOver()
	 */
	private static boolean producesBonusMove( final int lastHouse ) {
		return ( lastHouse == SOUTH_STORE || lastHouse == NORTH_STORE ) &&
			! isGameOver( myBoard.houses );
	}


	/*
	 * if isOwnSide( southMoving, currentHouse ) and houses[ currentHouse ] == 1
	 */
	private static boolean canCapture( final int[] boardState, final int currentHouse, final boolean isSouthMoving) {
		return( 
			isOwnSide( currentHouse, isSouthMoving ) && 
			boardState[ currentHouse ] == 1 &&
			boardState[ getOppositeHouse( currentHouse ) ] > 0
		);
	}

/*
 * move seeds from currentHouse to store (SOUTH_STORE if southMoving)
 * move seeds from getOppositeHouse( currentHouse ) to store (SOUTH_STORE if southMoving)
 */
	private static void capture( final int[] boardState, final int currentHouse ) {
		final int oppositeHouse = getOppositeHouse( currentHouse );
		final int toCapture = boardState[ oppositeHouse ] + boardState[ currentHouse ];
		boardState[ oppositeHouse ] = 0;
		boardState[ currentHouse ] = 0;
		
		if ( isSouthSide( currentHouse ) )
			boardState[ SOUTH_STORE ] += toCapture;
		else
			boardState[ NORTH_STORE ] += toCapture;
	}


/*
 * increment currentHouse
 *    if currentHouse = SOUTH_STORE and ! southMoving, increment again
 *    if currentHouse = NORTH_STORE and southMoving, increment again
 *    if currentHouse > 13, set to 0
 */
	private static int moveToNextHouse( final int currentHouse, final boolean isSouthMoving) {
		int result = currentHouse + 1;
		if ( result == SOUTH_STORE && ! isSouthMoving )
			result++;
		if ( result == NORTH_STORE && isSouthMoving )
			result++;
		if ( result > NORTH_STORE )
			result = 0;
		
		return result;
	}


	public static void collectFinalSeeds() {
		for ( int i = 0; i < SOUTH_STORE; i++ ) {
			myBoard.houses[ SOUTH_STORE ] += myBoard.houses[ i ];
			myBoard.houses[ i ] = 0;
		}
		
		for ( int j = SOUTH_STORE + 1; j < NORTH_STORE; j++ ) {
			myBoard.houses[ NORTH_STORE ] += myBoard.houses[ j ];
			myBoard.houses[ j ] = 0;
		}
	}
}
