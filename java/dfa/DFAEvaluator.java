package automata;

public abstract class DFAEvaluator {

    public static boolean evaluate(
        final DFA dfa, 
        final int input[], 
        final boolean print
    ) {        
        if (print) {
            System.out.print("Input string: ");
            Util.printArray(input);
            System.out.println();
        }
        int symbolIndex = 0;
        int state = dfa.getQ0();
        if (print) {
            Util.print(state, 0, -1);
        }
        while (input.length > symbolIndex) {
            int newSymbol = input[symbolIndex];
            state = dfa.getNext(state, newSymbol);
            if (print) {
                Util.print(state, symbolIndex, newSymbol);
            }
            
            symbolIndex++;
        }
        
        boolean result = dfa.isAccepting(state);
        if (print) {
            System.out.println("Result: " + result);
        }
        return result;
    }
}
