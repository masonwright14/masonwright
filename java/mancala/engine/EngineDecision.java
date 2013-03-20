package engine;

import java.util.ArrayList;

public class EngineDecision {

	private boolean makePieSwap;
	private ArrayList<int[]> moveSequence;
	
	public EngineDecision( final boolean makePieSwap, final ArrayList<int[]> moveSequence ) {
		this.makePieSwap = makePieSwap;
		this.moveSequence = moveSequence;
	}

	public boolean isMakePieSwap() {
		return makePieSwap;
	}

	public ArrayList<int[]> getMoveSequence() {
		return moveSequence;
	}
}
