package theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class RsdAnalysis {

    public static void main(final String[] args) {
        // testIsEvenParity();
        final int agents = 6;
        testNext(agents);
    }
    
    
    public static RsdResult analyzeRsd(
        final int numPlayers,
        final int teamSize
    ) {
        final int minPlayers = 4;
        if (numPlayers < minPlayers) {
            throw new IllegalArgumentException();
        }
        if (teamSize <= 1 || teamSize >= numPlayers) {
            throw new IllegalArgumentException();
        }
        if (numPlayers % teamSize != 0) {
            throw new IllegalArgumentException();
        }
        final int maxPlayers = 12;
        if (numPlayers > maxPlayers) {
            throw new IllegalArgumentException("too many agents");
        }
        
        final List<Long> wasCaptainCount = new ArrayList<Long>();
        final List<Long> teamValueTotal = new ArrayList<Long>();
        for (int i = 0; i < numPlayers; i++) {
            wasCaptainCount.add(0L);
            teamValueTotal.add(0L);
        }
        final int iterations = factorial(numPlayers);
        
        List<Integer> rsdOrder = getIntList(numPlayers);
        /*
         * iterate over all permutations of rsdOrder.
         * run the algorithm for that permutation.
         */
        while (rsdOrder != null) {
            // TODO: run RSD, analyze results
            rsdOrder = next(rsdOrder);
        }
        
        return new RsdResult(
            wasCaptainCount, teamValueTotal, iterations
        );
    }
    
    public static int factorial(final int n) {
        int fact = 1;
        for (int i = 1; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
    
    private static void testNext(final int n) {
        List<Integer> initial = getIntList(n);
        int count = 1;
        System.out.println(initial);
        initial = next(initial);
        while (initial != null) {
            count++;
            System.out.println(initial);
            initial = next(initial);
        }
        System.out.println("total: " + count);
        if (count != factorial(n)) {
            throw new IllegalStateException();
        }
    }
    
    /*
     * Assumes that "old" list has values 0-(N - 1) in some shuffled order.
     * 
     * Use the Steinhaus-Johnson-Trotter algorithm to iterate over
     * permutations of values from 0 to (N - 1).
     * 
     * https://en.wikipedia.org/wiki/Steinhaus%E2%80%93Johnson%E2%80%93Trotter_algorithm
     * 
     * http://math.stackexchange.com/questions/65923/how-does-one-compute-the-sign-of-a-permutation
     */
    public static List<Integer> next(final List<Integer> old) {
        final List<Integer> x = new ArrayList<Integer>();
        final List<Integer> y = new ArrayList<Integer>();
        final int n = old.size();
        int highestI = -1;
        for (int i = 0; i < n; i++) {
            final boolean isEven = isEvenParity(x, i - 1);
            x.add(old.indexOf(i));
            int yI = -1;
            if (isEven) {
                yI = old.indexOf(i) - 1;
            } else {
                yI = old.indexOf(i) + 1;
            }
            y.add(yI);
            if (yI >= 0 && yI < n) {
                if (old.get(yI) < i) {
                    highestI = i;
                }
            }
        }
        
        if (highestI == -1) {
            return null;
        }
        
        Integer swapItem = old.get(x.get(highestI));
        old.set(x.get(highestI), old.get(y.get(highestI)));
        old.set(y.get(highestI), swapItem);
        return old;
    }
    
    @SuppressWarnings("unused")
    private static void testIsEvenParity() {
        final Integer[] orderArr = {1, 2, 3, 4, 5};
        List<Integer> order = Arrays.asList(orderArr);
        final int maxIndex = 4;
        // expected: true
        System.out.println(isEvenParity(order, maxIndex));
        
        final Integer[] orderArr2 = {1, 2, 3, 5, 4};
        order = Arrays.asList(orderArr2);
        // expected: false
        System.out.println(isEvenParity(order, maxIndex));
        
        final Integer[] orderArr3 = {3, 2, 1};
        order = Arrays.asList(orderArr3);
        final int maxIndex2 = 1;
        // shold be: false
        System.out.println(isEvenParity(order, maxIndex2));
    }
    
    /*
     * Only examines items up through maxIndex.
     * Example: {1, 2, 3, 4, 5}, 
     * maxIndex = 2 -> only examines 1, 2, 3
     * 
     * "order" gives the index of item value 1, then the index
     * of item value 2, etc.
     */
    private static boolean isEvenParity(
        final List<Integer> order, 
        final int maxIndex
    ) {
        if (maxIndex < 1) {
            return true;
        }
        
        int inversions = 0;
        for (int i = 0; i <= maxIndex - 1; i++) {
            for (int j = i + 1; j <= maxIndex; j++) {
                if (order.get(i) > order.get(j)) {
                    inversions++;
                }
            }
        }
        
        return inversions % 2 == 0;
    }
    
    public static List<Integer> getIntList(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            result.add(i);
        }
        
        return result;
    }
    
    private static final class RsdResult {
        private final List<Long> wasCaptainCount;
        private final List<Long> teamValueTotal;
        private final int iterations;
        
        public RsdResult(
            final List<Long> aWasCaptainCount,
            final List<Long> aTeamValueTotal,
            final int aIterations
        ) {
            this.wasCaptainCount = new ArrayList<Long>(aWasCaptainCount);
            this.teamValueTotal = new ArrayList<Long>(aTeamValueTotal);
            this.iterations = aIterations;
        }

        List<Long> getWasCaptainCount() {
            return wasCaptainCount;
        }

        List<Long> getTeamValueTotal() {
            return teamValueTotal;
        }

        int getIterations() {
            return iterations;
        } 
    }
}
