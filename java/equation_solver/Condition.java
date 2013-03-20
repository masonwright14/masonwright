package edu.vanderbilt.solver;

import java.util.ArrayList;
import java.util.List;

public final class Condition {

    public static final int NUM_TERMS = 4;
    
    /*
     * these 4 terms represent A, B, C, and D in the expression:
     * A / B = C / D
     */
    private final List<Term> terms;
    private Value value;
    
    public Condition(final List<Term> aTerms) {
        this.terms = new ArrayList<Term>();
        assert aTerms.size() == NUM_TERMS;
        this.terms.addAll(aTerms);
        this.value = null;
    }
    
    public static boolean isConstant(final Term aTerm) {
        return aTerm == Term.L || aTerm == Term.M || aTerm == Term.LmM;
    }
    
    public int termsWithMatch() {
        int sum = 0;
        for (int i = 0; i < this.terms.size(); i++) {
            for (int j = 0; j < this.terms.size(); j++) {
                if (
                    this.terms.get(i) == this.terms.get(j)
                    && i != j
                ) {
                    sum++;
                    
                    // max one match per term
                    continue;
                }
            }
        }
        return sum;
    }
    
    public boolean has(final Term target) {
        return this.terms.contains(target);
    }
    
    /*
    public static void main(String[] args) {
        List<Term> terms = new ArrayList<Term>();
        terms.add(Term.M);
        terms.add(Term.Y);
        terms.add(Term.L);
        terms.add(Term.X);
        Condition cond = new Condition(terms);
        System.out.println(cond.hasNonDiagonal(Term.X, Term.L));
    }
    */
    
    public boolean hasDiagonal(final Term a, final Term b) {
        return (this.terms.get(0) == a && this.terms.get(3) == b)
            || (this.terms.get(3) == a && this.terms.get(0) == b)
            || (this.terms.get(1) == a && this.terms.get(2) == b)
            || (this.terms.get(2) == a && this.terms.get(1) == b);
    }
    
    public boolean hasNonDiagonal(final Term a, final Term b) {
        for (int i = 0; i < NUM_TERMS; i++) {
            for (int j = i + 1; j < NUM_TERMS; j++) {
                if ((this.terms.get(i) == a && this.terms.get(j) == b)
                || (this.terms.get(j) == a && this.terms.get(i) == b)) {
                    if (i == 0) {
                        if (j != 3) {
                            return true;
                        }
                    } else if (i == 1) {
                        if (j != 2) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    public int constants() {
        int sum = 0;
        for (Term term: this.terms) {
            if (isConstant(term)) {
                sum++;
            }
        }
        return sum;
    }
    
    public int variables() {
        int sum = 0;
        for (Term term: this.terms) {
            if (!isConstant(term)) {
                sum++;
            }
        }
        return sum;   
    }
    
    public int countTerm(final Term aTerm) {
        int sum = 0;
        for (Term term: this.terms) {
            if (term == aTerm) {
                sum++;
            }
        }
        return sum;      
    }
    
    public Term a() {
        final int a = 0;
        return this.terms.get(a);
    }
    
    public Term b() {
        final int b = 1;
        return this.terms.get(b);
    }
    
    public Term c() {
        final int c = 2;
        return this.terms.get(c);
    }
    
    public Term d() {
        final int d = 3;
        return this.terms.get(d);
    }
    
    public List<Term> getTerms() {
        return this.terms;
    }
    
    public void setValue(final Value aValue) {
        if (this.value != null && this.value != aValue) {
           throw new IllegalStateException(
               this.value.toString() + " " + aValue.toString()
           );
        }
        this.value = aValue;
    }
    
    public Value getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Condition [terms=");
        builder.append(terms);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }
}
