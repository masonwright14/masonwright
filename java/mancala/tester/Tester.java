package tester;

import java.util.ArrayList;

import engine.Engine;
import engine.EngineDecision;
import board.Board;
import player.Player;
import userinterface.UserInterface;

public abstract class Tester {

	private static int maxDepth;
	public static final int PIE_SWAP_INT = -1;
	private static boolean isSouthTurn;
	private static int southDepth;
	private static int northDepth;
	private static ResultList resultList;
	
	
	public static void runTest() {
		resultList = new ResultList();
		// for each search depth from 1 to 1 less than the max...
		for ( int underdogDepth = 1; underdogDepth < maxDepth; underdogDepth++ ) {
			// for each search depth from 1 greater than the underdog's, up to the max...
			for ( int favoriteDepth = underdogDepth + 1; favoriteDepth <= maxDepth; favoriteDepth++ ) {
				// run a test game between the underdog and favorite, with underdog moving first
				testGame( underdogDepth, favoriteDepth, true, false );
				// run another game with favorite moving first
				testGame( underdogDepth, favoriteDepth, false, false );
			}
		}
		
		resultList.printAll();
		resultList.printWonLossByDepth();
		resultList.printUpsets();
		resultList.printTotalPointsByDepth();
	}
	
	public static void main( String[] args ) {
		Board.setSeedsPerHouse( 6 );
		testGame( 2, 1, true, true );
	}
	
	
	public static void testGame( 
		int aSouthDepth, 
		int aNorthDepth, 
		boolean aIsSouthFirst,
		boolean shouldDisplayProgress
	) {
		isSouthTurn = aIsSouthFirst;
		southDepth = aSouthDepth;
		northDepth = aNorthDepth;
		
		Board.setupBoard();
		if ( shouldDisplayProgress )
			Board.displayBoard();
		playFirstMove( shouldDisplayProgress );
		while( ! Board.isGameOver() ) {
			computerSelectMove( shouldDisplayProgress );
		}
		finishGame( shouldDisplayProgress );	
		
		int firstPlayerDepth, secondPlayerDepth, firstPlayerScore, secondPlayerScore;
		if ( aIsSouthFirst ) {
			firstPlayerDepth = aSouthDepth;
			secondPlayerDepth = aNorthDepth;
			firstPlayerScore = Board.getSouthScore();
			secondPlayerScore = Board.getNorthScore();
		}
		else {
			firstPlayerDepth = aNorthDepth;
			secondPlayerDepth = aSouthDepth;
			firstPlayerScore = Board.getNorthScore();
			secondPlayerScore = Board.getSouthScore();
		}
		
		Result currentResult = new Result(
			firstPlayerDepth,
			secondPlayerDepth,
			firstPlayerScore,
			secondPlayerScore
		);
		
		resultList.addResult( currentResult );
	}
	
	
	private static void finishGame( boolean shouldDisplayProgress ) {
		Board.collectFinalSeeds();
		if ( shouldDisplayProgress )
			Board.displayBoard();
	}
	
	
	private static int getCurrentDepth() {
		if ( isSouthTurn )
			return southDepth;
		
		return northDepth;
	}
	
	
	private static void playFirstMove(
		boolean shouldDisplayProgress
	) {
		ArrayList<int[]> moveSequence = Engine.selectFirstMoveForPieRule(
			Board.getBoard(),
			getCurrentDepth(), 
			isSouthTurn
		);
				
		for ( int[] nextMove: moveSequence ) {
			Board.makeMove( nextMove );
			if ( shouldDisplayProgress )
				Board.displayBoard();
		}
				
		isSouthTurn = ! isSouthTurn;
		
		EngineDecision decision = Engine.selectMoveOrPieSwap( 
			Board.getBoard(),
			getCurrentDepth(), 
			isSouthTurn
		);
			
		if ( decision.isMakePieSwap() ) {
			Board.doPieSwap();
			if ( shouldDisplayProgress ) {
				UserInterface.displayText( "Swapping." );
				Board.displayBoard();
			}
			isSouthTurn = ! isSouthTurn;
		}
		else {
			for ( int[] nextMove: decision.getMoveSequence() ) {
				Board.makeMove( nextMove );
				if ( shouldDisplayProgress )
					Board.displayBoard();
			}
				
			isSouthTurn = ! isSouthTurn;
		}
	}
	
	
	private static void computerSelectMove( final boolean shouldDisplayBoard ) {
		ArrayList<int[]> moveSequence = Engine.selectMove(
			Board.getBoard(),
			getCurrentDepth(), 
			isSouthTurn
		);
		
		for ( int[] nextMove: moveSequence ) {
			Board.makeMove( nextMove );
			if ( shouldDisplayBoard )
				Board.displayBoard();
		}
		
		isSouthTurn = ! isSouthTurn;
	}


	public static void setMaxDepth( final int aDepth ) {
		if ( aDepth < 2 || aDepth > Player.MAX_DEPTH )
			throw new IllegalArgumentException();
		
		maxDepth = aDepth;
	}
}
