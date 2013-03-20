package tester;

public class Result {

	
	final int firstPlayerDepth;
	final int secondPlayerDepth;
	final int firstPlayerScore;
	final int secondPlayerScore;
	
	public static enum ResultType {
		WIN, LOSS, TIE
	}
	
	
	public Result(
		final int firstPlayerDepth,
		final int secondPlayerDepth,
		final int firstPlayerScore,
		final int secondPlayerScore
	) {
		this.firstPlayerDepth = firstPlayerDepth;
		this.secondPlayerDepth = secondPlayerDepth;
		this.firstPlayerScore = firstPlayerScore;
		this.secondPlayerScore = secondPlayerScore;
	}

	
	public boolean isUpset() {
		if ( firstPlayerDepth == secondPlayerDepth )
			return false;
		
		if ( firstPlayerDepth > secondPlayerDepth )
			return firstPlayerScore < secondPlayerScore;
			
		return secondPlayerScore < firstPlayerScore;
	}
	
	
	public boolean isParticipant( final int depth ) {
		return depth == firstPlayerDepth || depth == secondPlayerDepth;
	}
	
	
	public boolean isTie() {
		return firstPlayerScore == secondPlayerScore;
	}
	
	
	private ResultType getResultForPlayer( final boolean isFirstPlayer ) {
		if ( isTie() )
			return ResultType.TIE;
		
		if ( isFirstPlayer ) {
			if ( firstPlayerScore > secondPlayerScore )
				return ResultType.WIN;
			
			return ResultType.LOSS;
		}
			
		if ( secondPlayerScore > firstPlayerScore )
			return ResultType.WIN;
		
		return ResultType.LOSS;	
	}
	
	
	public int getWinnerDepth() {
		if ( isTie() )
			throw new IllegalStateException();
		
		if ( firstPlayerScore > secondPlayerScore )
			return firstPlayerDepth;
		
		return secondPlayerDepth;
	}
	
	
	public int getLoserDepth() {
		if ( isTie() )
			throw new IllegalStateException();
		
		if ( firstPlayerScore < secondPlayerScore )
			return firstPlayerDepth;
		
		return secondPlayerDepth;	
	}
	
	
	public int 	getPointsAgainst( final int depth ) {
		if ( ! isParticipant( depth ) )
			throw new IllegalArgumentException();
		
		if ( depth == firstPlayerDepth ) {
			return secondPlayerScore;
		}
		
		return firstPlayerScore;	
	}
	
	
	public int getPointsFor( final int depth ) {
		if ( ! isParticipant( depth ) )
			throw new IllegalArgumentException();
		
		if ( depth == firstPlayerDepth ) {
			return firstPlayerScore;
		}
		
		return secondPlayerScore;	
	}
	
	
	public int getMaxDepth() {
		if ( firstPlayerDepth > secondPlayerDepth )
			return firstPlayerDepth;
		
		return secondPlayerDepth;
	}
	
	
	public ResultType getResult( final int depth ) {
		if ( ! isParticipant( depth ) )
			throw new IllegalArgumentException();
		
		if ( depth == firstPlayerDepth ) {
			return getResultForPlayer( true );
		}
		
		return getResultForPlayer( false );
	}


	@Override
	public String toString() {
		return "Result [firstPlayerDepth=" + firstPlayerDepth
			+ ", secondPlayerDepth=" + secondPlayerDepth
			+ ", firstPlayerScore=" + firstPlayerScore
			+ ", secondPlayerScore=" + secondPlayerScore + "]";
	}
}
