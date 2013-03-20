package automata;

import java.util.Set;

public final class DFA {

    // number of states
    private final int q;
    
    // number of symbols in the alphabet
    private final int s;
    
    // transition function table: q X s -> q
    private final int d[][];
    
    // start state (1 <= q0 <= q)
    private final int q0;
    
    // set of accepting states. for each member, 1 <= x <= q
    private final Set<Integer> f;
    
    public DFA(final int aQ, final int aS, final int aD[][], final int aQ0, final Set<Integer> aF) {
        if (aQ < 1) {
            throw new IllegalArgumentException();
        }
        this.q = aQ;
        
        if (aS < 1) {
            throw new IllegalArgumentException();
        }
        this.s = aS;
        
        // must have a transition from each state
        if (aD.length != aQ) {
            throw new IllegalArgumentException();
        }
       
        // must have a transition from each symbol
        for (int i = 0; i < aQ; i++) {
            if (aD[i].length != aS) {
                throw new IllegalArgumentException();
            }
        }
        
        // next state must be a valid state
        for (int i = 0; i < aQ; i++) {
            for (int j = 0; j < aS; j++) {
                int current = aD[i][j];
                if (current < 1 || current > aQ) {
                    throw new IllegalArgumentException();
                }
            }
        }
        this.d = aD;
        
        if (aQ0 < 1 || aQ0 > aQ) {
            throw new IllegalArgumentException();
        }
        this.q0 = aQ0;
        
        for (Integer integer: aF) {
            if (integer < 1 || integer > aQ) {
                throw new IllegalArgumentException();
            }
        }
        this.f = aF;
    }
    
    public int getQ() {
        return this.q;
    }
    
    public int getS() {
        return this.s;
    }
    
    public int getQ0() {
        return this.q0;
    }
    
    public boolean isAccepting(final int state) {
        return this.f.contains(state);
    }
    
    public int getNext(final int state, final int symbol) {
        if (state < 1 || state > this.q || symbol < 1 || symbol > this.s) {
            throw new IllegalArgumentException();
        }
        
        return d[state - 1][symbol - 1];
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DFA [\n q=");
        builder.append(q);
        builder.append(",\n s=");
        builder.append(s);
        builder.append(",\n d=");
        builder.append(Util.stringify2dArray(d));
        builder.append(",\n q0=");
        builder.append(q0);
        builder.append(",\n f=");
        builder.append(f);
        builder.append("\n]");
        return builder.toString();
    }
}
