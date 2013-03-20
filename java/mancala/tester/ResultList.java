package tester;

import java.util.ArrayList;

import userinterface.UserInterface;

public class ResultList {
	
	private final ArrayList<Result> resultList;
	
	
	public ResultList() {
		this.resultList = new ArrayList<Result>();
	}

	
	public void addResult( final Result toAdd ) {
		this.resultList.add( toAdd );
	}
	
	
	public void printAll() {
		for ( Result currentResult: this.resultList )
			UserInterface.displayText( currentResult.toString() );
	}
	
	
	public void printUpsets() {
		StringBuilder sb = new StringBuilder();
		sb.append( "Upsets:\n" );
		
		for ( Result currentResult: this.resultList ) {
			if ( currentResult.isUpset() ) {
				sb.append( currentResult.toString() );
				sb.append( "\n" );
			}
		}
		
		UserInterface.displayText( sb.toString() );
	}
	
	
	public int getTotalPointsAgainstDepth( final int depth ) {
		int totalPointsAgainst = 0;
		for ( Result currentResult: this.resultList ) {
			if ( currentResult.isParticipant( depth ) ) {
				totalPointsAgainst += currentResult.getPointsAgainst( depth );
			}
		}
		
		return totalPointsAgainst;	
	}
	
	
	public int getTotalPointsForDepth( final int depth ) {
		int totalPointsFor = 0;
		for ( Result currentResult: this.resultList ) {
			if ( currentResult.isParticipant( depth ) ) {
				totalPointsFor += currentResult.getPointsFor( depth );
			}
		}
		
		return totalPointsFor;	
	}
	
	
	public void printTotalPointsByDepth() {
		StringBuilder sb = new StringBuilder();
		for ( int currentDepth = 1; currentDepth <= getMaxDepth(); currentDepth++ ) {
			sb.append( "Depth: ").append( currentDepth );
			sb.append( "\nTotal points for: " ).append( getTotalPointsForDepth( currentDepth ) );
			sb.append( "\nTotal points against: " ).append( getTotalPointsAgainstDepth( currentDepth ) );
			sb.append( "\n\n" );
		}
		
		UserInterface.displayText( sb.toString() );	
	}
	
	
	public void printWonLossByDepth() {
		StringBuilder sb = new StringBuilder();
		for ( int currentDepth = 1; currentDepth <= getMaxDepth(); currentDepth++ ) {
			sb.append( "Depth: ").append( currentDepth );
			sb.append( "\nWins: " ).append( getWinsByDepth( currentDepth ) );
			sb.append( "\nLosses: " ).append( getLossesByDepth( currentDepth ) );
			sb.append( "\nTies:" ).append( getTiesByDepth( currentDepth ) );
			sb.append( "\n\n" );
		}
		
		UserInterface.displayText( sb.toString() );
	}
	
	
	public int getWinsByDepth( final int depth ) {
		int wins = 0;
		for ( Result currentResult: this.resultList ) {
			if ( currentResult.isParticipant( depth ) ) {
				if ( ! currentResult.isTie() ) {
				   if ( currentResult.getWinnerDepth() == depth )
					   wins++;
				}
			}
		}
		
		return wins;
	}
	
	
	public int getLossesByDepth( final int depth ) {
		int losses = 0;
		for ( Result currentResult: this.resultList ) {
			if ( currentResult.isParticipant( depth ) ) {
				if ( ! currentResult.isTie() ) {
				   if ( currentResult.getWinnerDepth() != depth )
					   losses++;
				}
			}
		}
		
		return losses;
	}
	
	
	public int getTiesByDepth( final int depth ) {
		int ties = 0;
		for ( Result currentResult: this.resultList ) {
			if ( currentResult.isParticipant( depth ) ) {
				if ( currentResult.isTie() ) {
				   ties++;
				}
			}
		}
		
		return ties;	
	}
	
	
	private int getMaxDepth() {
		int maxDepth = 0;
		for( Result currentResult: this.resultList ) {
			if ( currentResult.getMaxDepth() > maxDepth )
				maxDepth = currentResult.getMaxDepth();
		}
		
		return maxDepth;
	}
}
