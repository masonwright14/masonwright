package engine;

import java.util.ArrayList;

import board.Board;

public abstract class Engine {
	
	/*
	 * get all move sequences from current board position as ArrayList<int[]>.
	 * initialize currentBestValue to Board.LOSS_VALUE.
	 * initialize currentBestMoveSequence to null
	 * for each move sequence found
	 *    currentValue <- run negamaxRecurse(
	 *       boardState after the move sequence
	 *       maxDepth - 1
	 *       Board.LOSS_VALUE
	 *       Board.WIN_VALUE
	 *       opposite of isSouthMoving
	 *    )
	 *    if currentValue > currentBestValue
	 *       currentBestValue <- currentValue
	 *       currentBestMoveSequence <- moveSequence
	 *       
	 * return currentBestMoveSequence
	 */
	public static ArrayList<int[]> selectMove( 
		final int[] boardState,
		final int maxDepth, 
		final boolean isSouthMoving
	) {
		final ArrayList<ArrayList<int[]>> nextMoveSequences = getMovesFromBase( boardState, isSouthMoving );
		if ( nextMoveSequences == null )
			throw new IllegalStateException();
		
		int currentBestValue = Board.LOSS_VALUE;
		ArrayList<int[]> currentBestMoveSequence = null;
		for ( final ArrayList<int[]> currentMoveSequence: nextMoveSequences ) {
			final int[] lastBoardState = currentMoveSequence.get( currentMoveSequence.size() - 1 );
			final int currentMoveValue = -1 * negamaxRecurse( 
				lastBoardState,
				maxDepth - 1,
				Board.LOSS_VALUE,
				Board.WIN_VALUE,
				! isSouthMoving,
				! isSouthMoving
			);
			
			if ( currentMoveValue >= currentBestValue ) {
				/*
				System.out.println( "new best: " + currentMoveValue );
				
				for( int[] currentMove: currentMoveSequence ) {
					printBoardState( currentMove );
				}
				System.out.println();
				*/
				currentBestValue = currentMoveValue;
				currentBestMoveSequence = currentMoveSequence;
			}
		}
		
		// System.out.println( "final best: " + currentBestValue );
		
		return currentBestMoveSequence;
	}
	
	
	@SuppressWarnings("unused")
	private static void printBoardState( final int[] boardState ) {
		for ( int currentInt: boardState ) {
			System.out.print( currentInt + " " );
		}
		System.out.println();
	}
	
	
	public static ArrayList<int[]> selectFirstMoveForPieRule( 
		final int[] boardState,
		final int maxDepth,
		final boolean isSouthMoving
	) {
		final ArrayList<ArrayList<int[]>> nextMoveSequences = getMovesFromBase( boardState, isSouthMoving );
		if ( nextMoveSequences == null )
			throw new IllegalStateException();
		
		int currentBestValue = Board.WIN_VALUE;
		ArrayList<int[]> currentBestMoveSequence = null;
		for ( final ArrayList<int[]> currentMoveSequence: nextMoveSequences ) {
			final int[] lastBoardState = currentMoveSequence.get( currentMoveSequence.size() - 1 );
			final int currentMoveValue = -1 * negamaxRecurse( 
				lastBoardState,
				maxDepth - 1,
				Board.LOSS_VALUE,
				Board.WIN_VALUE,
				! isSouthMoving,
				! isSouthMoving
			);
			
			if ( Math.abs( currentMoveValue ) < Math.abs( currentBestValue ) ) {
				/*
				System.out.println( "new best: " + currentMoveValue );
				
				for( int[] currentMove: currentMoveSequence ) {
					printBoardState( currentMove );
				}
				System.out.println();
				*/
				currentBestValue = currentMoveValue;
				currentBestMoveSequence = currentMoveSequence;
			}
		}
		
		// System.out.println( "final best: " + currentBestValue );
		
		return currentBestMoveSequence;	
	}
	
	
	public static EngineDecision selectMoveOrPieSwap(
		int[] boardState, 
		int maxDepth, 
		boolean isSouthMoving
	) {
		// get list of available move sequences for this player (a move sequence is a move followed by any bonus moves)
		final ArrayList<ArrayList<int[]>> nextMoveSequences = getMovesFromBase( boardState, isSouthMoving );
		// start out assuming you can do no worse than a loss
		int currentBestValue = Board.LOSS_VALUE;
		// initialize best known move sequence to null
		ArrayList<int[]> currentBestMoveSequence = null;
		// for each sequence of moves you could make next ...
		for ( final ArrayList<int[]> currentMoveSequence: nextMoveSequences ) {
			// get the last board state this player would produce in the move sequence (including bonus moves)
			final int[] lastBoardState = currentMoveSequence.get( currentMoveSequence.size() - 1 );
			// get the value for this player of that board state
			final int currentMoveValue = -1 * negamaxRecurse( 
				lastBoardState,
				maxDepth - 1,
				Board.LOSS_VALUE,
				Board.WIN_VALUE,
				! isSouthMoving,
				! isSouthMoving
			);
			
			// if this is the best value seen so far...
			if ( currentMoveValue > currentBestValue ) {
				// update the current best value
				currentBestValue = currentMoveValue;
				// update the current best move sequence
				currentBestMoveSequence = currentMoveSequence;
			}
		}
		
		// don't swap sides if you've seen a minimax value that is in your favor.
		// if you've seen a move that leaves the board in your favor...
		if ( currentBestValue > 0 ) {
			// do NOT swap sides. instead, make this move.
			return new EngineDecision( false, currentBestMoveSequence );
		}
		else {
			// swap sides if all moves evaluate in opponent's favor
			return new EngineDecision( true, null );
		}
	}
	
	
	/*
	 * returns a list of lists of moves that could be taken next.
	 *    each list in the outer list is a move sequence.
	 *    each int[] in the inner list is a board state, as an array of 14 ints
	 * 
	 * make empty result ArrayList<ArrayList<int[]>> 
	 * 
	 * for each possible move from the initial board state, for the given player:
	 *    if does not produce a bonus move
	 *       add ArrayList<int[]> with just this move to result list
	 *    else
	 *       add all from ArrayList<ArrayList<int[]>> from recursive results to the result list
	 * 
	 * return result
	 */
	private static ArrayList<ArrayList<int[]>> getMovesFromBase( final int[] boardState, final boolean isSouth ) {
		ArrayList<ArrayList<int[]>> result = new ArrayList<ArrayList<int[]>>();
		for( int houseToMove: Board.getNextHousesToMoveForPlayer( boardState, isSouth ) ) {
			if ( Board.producesBonusMove( boardState, houseToMove ) ) {
				ArrayList<int[]> priorMoves = new ArrayList<int[]>();
				priorMoves.add( boardState );
				priorMoves.add( Board.makeMove( boardState, houseToMove ) );
				result.addAll( getMovesFromRecurse( priorMoves, isSouth ) );
			}
			// no bonus move
			else {
				ArrayList<int[]> toAdd = new ArrayList<int[]>();
				toAdd.add( Board.makeMove( boardState, houseToMove ) );
				result.add( toAdd );
			}
		}
		
		return result;
	}
	
	
	private static ArrayList<int[]> cloneArrayList( final ArrayList<int[]> input ) {
		ArrayList<int[]> output = new ArrayList<int[]>();
		
		for ( int[] currentArray: input )
			output.add( currentArray );
		
		return output;
	}
	
	
	private static ArrayList<ArrayList<int[]>> getMovesFromRecurse( 
		final ArrayList<int[]> priorSequence, 
		final boolean isSouth
	) {
		ArrayList<ArrayList<int[]>> result = new ArrayList<ArrayList<int[]>>();
		
		int[] mostRecentState = priorSequence.get( priorSequence.size() - 1 );
		for( int houseToMove: Board.getNextHousesToMoveForPlayer( mostRecentState, isSouth ) ) {
			// if a bonus move is produced
			if ( Board.producesBonusMove( mostRecentState, houseToMove ) ) {
				ArrayList<int[]> toAdd = cloneArrayList( priorSequence );
				toAdd.add( Board.makeMove( mostRecentState, houseToMove ) );
				result.addAll( getMovesFromRecurse( toAdd, isSouth ) );
			}
			// else if no bonus move
			else {
				ArrayList<int[]> toAdd = cloneArrayList( priorSequence );
				toAdd.add( Board.makeMove( mostRecentState, houseToMove ) );
				result.add( toAdd );
			}	
		}
		
		return result;
	}
	
	
	/*
	 * return the heuristic value of the board from the perspective of the first player to move. 
	 */
	private static int negamaxRecurse( 
		final int[] boardState, 
		final int depthLeft,  
		final int alphaGuessInput,
		final int betaGuess,
		final boolean isSouthMoving,
		final boolean isSouthRoot
	) {		
		int currentAlphaGuess = alphaGuessInput;
		
		// base case: have reached max search depth, or a game over condition / terminal node
		if ( 
			depthLeft == 0 ||
			Board.isGameOver( boardState )
		) {
			/*
			System.out.println( "base value: " + Board.getValue( boardState, isSouthMoving ) );
			System.out.println( "board:" );
			System.out.println();
			printBoardState( boardState );
			*/
			// if from the first player's perspective
			if ( isSouthRoot == isSouthMoving )
				return Board.getValue( boardState, isSouthRoot );
			else
				return -1 * Board.getValue( boardState, isSouthRoot );
		}
		else { 		// if must search deeper by recursion
			final ArrayList<ArrayList<int[]>> nextMoveSequences = getMovesFromBase( boardState, isSouthMoving );
			for ( final ArrayList<int[]> currentMoveSequence: nextMoveSequences ) {
				final int[] lastBoardState = currentMoveSequence.get( currentMoveSequence.size() - 1 );
				/*
				System.out.print( "board state: " );
				for( int i = 0; i < lastBoardState.length; i++ )
					System.out.print( lastBoardState[ i ] + " " );
				System.out.println();
				*/
				final int currentMoveValue = -1 * negamaxRecurse( 
					lastBoardState,
					depthLeft - 1,
					-1 * betaGuess,
					-1 * currentAlphaGuess,
					! isSouthMoving,
					isSouthRoot
				);
				
				if ( currentMoveValue >= betaGuess ) {
					/*
					System.out.println( "pruning: " + currentMoveValue + " >= " + betaGuess );
					System.out.println( "value is " + currentMoveValue );
					System.out.println();
					*/
					return currentMoveValue;
				}
				if ( currentMoveValue >= currentAlphaGuess ) {
					/*
					System.out.println( "new best guess: " + currentMoveValue + " >= " + currentAlphaGuess );
					System.out.println();
					*/
					currentAlphaGuess = currentMoveValue;
				}
						
			}
				
			// System.out.println( "returning from last link: " + currentAlphaGuess );
			return currentAlphaGuess;
		}
	}
}
