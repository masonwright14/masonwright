package automata;

import java.util.HashSet;
import java.util.Set;

public final class DFADriver {

    public static void main(final String[] args) {
        DFA dfa = getDFA22();
        System.out.println(dfa);
        int arr[] = {1, 2, 1, 2};
        DFAEvaluator.evaluate(dfa, arr, true);
        int arr2[] = {1, 2, 1, 1, 1, 2};
        DFAEvaluator.evaluate(dfa, arr2, true);
        int arr3[] = {};
        DFAEvaluator.evaluate(dfa, arr3, true);
        int arr4[] = {1, 2, 2, 1, 2, 1};
        DFAEvaluator.evaluate(dfa, arr4, true);
    }
    
    /*
     * accepts all lists ending in 2. Sipser p. 37
     */
    static DFA getDFA2() {
        int d[][] = {{1, 2},{1,2}};
        Set<Integer> f = new HashSet<Integer>();
        f.add(2);
        DFA result = new DFA(
            2, // Q
            2, // Sigma
            d, // d
            1, // q0
            f // f
        );
        return result;
    }
    
    /*
     * accepts all lists that start and end with same symbol.
     * Sipser p. 38
     */
    static DFA getDFA4() {
        int d[][] = {{2, 3}, {2, 4}, {5, 3}, {2, 4}, {5, 3}};
        Set<Integer> f = new HashSet<Integer>();
        f.add(2);
        f.add(3);
        DFA result = new DFA(
            5, // Q
            2, // Sigma
            d, // d
            1, // q0
            f // f
        );
        return result;
    }
    
    /*
     * accepts all lists that contain the sequence 1, 1, 2.
     * Sipser p. 44
     */
    static DFA getDFA22() {
        int d[][] = {{2, 1}, {3, 1}, {3, 4}, {4, 4}};
        Set<Integer> f = new HashSet<Integer>();
        f.add(4);
        DFA result = new DFA(
            4, // Q
            2, // Sigma
            d, // d
            1, // q0
            f // f
        );
        return result;
    }
}
