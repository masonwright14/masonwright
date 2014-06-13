package coalitiongames;

import java.util.List;

public abstract class Util {
    
    public static <T> String demandAsMatrix(
        final List<List<T>> demand
    ) {
        final StringBuilder builder = new StringBuilder();
        for (final List<T> row: demand) {
            for (final T cur: row) {
                builder.append(cur).append(' ');
            }
            builder.append('\n');
        }
        
        return builder.toString();
    }
    
    public static <T> void printDemandAsMatrix(
        final List<List<T>> demand
    ) {   
        for (final List<T> row: demand) {
            final StringBuilder builder = new StringBuilder();
            for (final T cur: row) {
                builder.append(cur).append(' ');
            }
            System.out.println(builder.toString());
        }
    }
    
    /**
     * Prints adjacency list as 0-based, so demand for the first agent
     * in the list prints as demand for agent "0".
     */
    public static void printDemandAsAdjacencyList(
        final List<List<Integer>> demand
    ) {
        for (int i = 0; i < demand.size(); i++) {
            final StringBuilder builder = new StringBuilder();
            final List<Integer> row = demand.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == 1 && i != j) {
                    builder.append(j).append(' ');
                }
            }
            System.out.println(builder.toString());
        }
    }
}
