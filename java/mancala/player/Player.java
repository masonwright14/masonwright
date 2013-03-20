package player;

import java.util.ArrayList;

import engine.Engine;
import engine.EngineDecision;
import userinterface.UserInterface;
import board.Board;

/**
 * For playing a game between human and computer.
 * Human always plays for South.
 *
 */
public abstract class Player {

	public static final int MAX_DEPTH = 8;
	public static final int PIE_SWAP_INT = -1;
	private static int depth;
	private static boolean isHumansTurn;
	private static final boolean isComputerSouth = false;
	
	/* 
	 * handle first move
	 *    display board with markers
	 *    if human goes first:
	 *       human chooses move
	 *          display board after
	 *       computer chooses whether to switch or move and where
	 *          display board after
	 *    if computer goes first:
	 *       computer chooses move, knowing pie rule option is ahead
	 *          display board after
	 *       human chooses whether to switch or move and where
	 *          display board after
	 * while true
	 *    if computer's turn
	 *       computer chooses move, knowing not pie rule
	 *          display board after
	 *    else human's turn
	 *       human chooses move
	 *          display board after
	 *    
	 *    if game is over
	 *       exit to game over procedure
	 *       back to menu
	 *    
	 */
	public static void play( final boolean firstPlayerHuman ) {
		isHumansTurn = firstPlayerHuman;
		
		setupBoard();
		Board.displayBoard();
		playFirstMove();
		while( ! Board.isGameOver() ) {
			nextPlayerMove();
		}
		finishGame();
	}
	

	private static void finishGame() {
		Board.collectFinalSeeds();
		Board.displayBoard();
	}


	private static void nextPlayerMove() {
		if ( isHumansTurn )
			humanSelectMove();
		else
			computerSelectMove();
	}


	private static void setupBoard() {
		Board.setupBoard();
	}


	private static void playFirstMove() {
		if ( isHumansTurn )
			playFirstMoveHumanFirst();
		else
			playFirstMoveComputerFirst();
	}


	private static void playFirstMoveComputerFirst() {
		computerSelectMoveBeforePieRule();
		humanSelectMoveOrPieSwap();
	}


	private static void computerSelectMoveBeforePieRule() {
		ArrayList<int[]> moveSequence = Engine.selectFirstMoveForPieRule(
			Board.getBoard(),
			depth, 
			isComputerSouth
		);
			
		for ( int[] nextMove: moveSequence ) {
			Board.makeMove( nextMove );
			Board.displayBoard();
		}
			
		isHumansTurn = true;	
	}


	/*
	 * human chooses move
	 *    check if selection has at least 1 seed
	 *       if not, make select again
	 * computer chooses whether to switch or move and where
	 */
	private static void playFirstMoveHumanFirst() {
		humanSelectMove();
		computerSelectMoveOrPieSwap();
	}
	
	
	private static void computerSelectMove() {
		ArrayList<int[]> moveSequence = Engine.selectMove(
			Board.getBoard(),
			depth, 
			isComputerSouth
		);
		
		for ( int[] nextMove: moveSequence ) {
			Board.makeMove( nextMove );
			Board.displayBoard();
		}
		
		isHumansTurn = true;
	}


	/*
	 * computer selects move of pie swap
	 * call board to make the move and see if a bonus move follows
	 *    if the computer chooses a move with a bonus move, it makes all the moves
	 *    at once
	 */
	private static void computerSelectMoveOrPieSwap() {
		EngineDecision decision = Engine.selectMoveOrPieSwap( 
			Board.getBoard(),
			depth, 
			isComputerSouth
		);
		
		if ( decision.isMakePieSwap() ) {
			Board.doPieSwap();
			UserInterface.displayText( "Swapping." );
			Board.displayBoard();
			isHumansTurn = true;
		}
		else {
			for ( int[] nextMove: decision.getMoveSequence() ) {
				Board.makeMove( nextMove );
				Board.displayBoard();
			}
			
			isHumansTurn = true;
		}
	}


	private static boolean isValidMove( final int moveSelection ) {
		if ( moveSelection == PIE_SWAP_INT )
			return true;
		
		return Board.hasSeeds( moveSelection );
	}


	/*
	 * prompt for human to select move
	 *    while not valid (no seeds there), re-prompt
	 * call board to make the move and see if a bonus move follows
	 * if a bonus move is called for, this method calls itself again
	 */
	private static void humanSelectMove() {
		int moveSelection;
		boolean wasInvalid = false;
		do {
			if ( wasInvalid )
				UserInterface.displayText( "Invalid move: No seeds to sow." );
			
			moveSelection = UserInterface.humanSelectMoveNoPieRule();
			wasInvalid = true;
		} while ( ! isValidMove( moveSelection ) );
		
		final boolean bonusMoveNext = Board.makeMove( moveSelection );
		Board.displayBoard();
		
		if ( bonusMoveNext )
			humanSelectBonusMove();
		else
			isHumansTurn = false;
	}
	
	
	private static void humanSelectMoveOrPieSwap() {
		int moveSelection;
		boolean wasInvalid = false;
		do {
			if ( wasInvalid )
				UserInterface.displayText( "Invalid move: No seeds to sow." );
			
			moveSelection = UserInterface.humanSelectMoveWithPieRule();
			wasInvalid = true;
		} while ( ! isValidMove( moveSelection ) );
		
		if ( moveSelection == PIE_SWAP_INT ) {
			Board.doPieSwap();
			Board.displayBoard();
			isHumansTurn = false;
		}
		else {
			final boolean bonusMoveNext = Board.makeMove( moveSelection );
			Board.displayBoard();
			
			if ( bonusMoveNext )
				humanSelectBonusMove();
			else
				isHumansTurn = false;
		}
	}
	
	
	private static void humanSelectBonusMove() {
		UserInterface.displayForBonusMove();
		humanSelectMove();
	}


	public static void setDepth( final int aDepth ) {
		if ( aDepth > MAX_DEPTH )
			throw new IllegalArgumentException();
		
		depth = aDepth;
	}
}
