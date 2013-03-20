package computation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class PostCorrespondenceProblem {
    
    // symbols of the alphabet
    public static final Integer A = 2;
    public static final Integer B = 3;
    
    public static void main(final String[] args) {
        Problem problem = new Problem();
        
        /*
         * Problem: AB/ABAB, B/A, ABA/B, AA/A
         */
        {
            Integer[] top = {A, B};
            Integer[] bottom = {A, B, A, B};
            problem.addDomino(new Domino(top, bottom));
        }
        
        {
            Integer[] top = {B};
            Integer[] bottom = {A};
            problem.addDomino(new Domino(top, bottom));
        }
        
        {
            Integer[] top = {A, B, A};
            Integer[] bottom = {B};
            problem.addDomino(new Domino(top, bottom));
        }
        
        {
            Integer[] top = {A, A};
            Integer[] bottom = {A};
            problem.addDomino(new Domino(top, bottom));
        }
        
        System.out.println(problem + "\n");
        
        final int depth = 6;
        List<Domino> solution = problem.getSolution(depth);
        if (solution == null) {
            System.out.println("No solutions of depth: " + depth);
        } else {
            for (Domino domino: solution) {
                System.out.println(domino);
            }
        }
        
        /*
         * Result: AAAABAB; AA/A, AA/A, B/A, AB/ABAB.
         */
    }
    
    /*
     * Returns true if the tops of the dominos, concatenated,
     * read the same as the bottoms of the dominos.
     */
    public static boolean isMatch(List<Domino> dominos) {
        List<Integer> tops = getSeries(dominos, true);
        List<Integer> bottoms = getSeries(dominos, false);
        
        if (tops.isEmpty()) {
            return false;
        }
        
        if (tops.size() != bottoms.size()) {
            return false;
        }
        
        for (int i = 0; i < tops.size(); i++) {
            if (tops.get(i) != bottoms.get(i)) {
                return false;
            }
        }
        
        return true;
    }
    
    /*
     * returns either the list of top integers, or the list of bottom integers
     * from all the dominos in order
     */
    public static List<Integer> getSeries(final List<Domino> dominos, final boolean isTop) {
        List<Integer> result = new ArrayList<Integer>();
        for (Domino domino: dominos) {
            if (isTop) {
                for (Integer integer: domino.getTop()) {
                    result.add(integer);
                }
            } else {
                for (Integer integer: domino.getBottom()) {
                    result.add(integer);
                }
            }
        }
        
        return result;
    }
    
    private static final class Problem {
        private final List<Domino> dominos;
        
        public Problem() {
            this.dominos = new ArrayList<Domino>();
        }
        
        public void addDomino(final Domino domino) {
            if (!this.dominos.contains(domino)) {
                this.dominos.add(domino);
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Problem [dominos=");
            builder.append(dominos);
            builder.append("]");
            return builder.toString();
        }
        
        /*
         * returns a solution of minimal length, of length up to
         * maxDepth dominos, or null if none exists.
         */
        public List<Domino> getSolution(final int maxDepth) {
            int currentDepth = 1;
            while (currentDepth <= maxDepth) {
		// NB: this does not scale. should get each list individually
		// to solve harder PCP's without running out of memory
                List<List<Domino>> lists = getListsOfLength(currentDepth);
                for (List<Domino> list: lists) {
                    if (isMatch(list)) {
                        return list;
                    }
                }
                
                currentDepth++;
            }
            
            return null;
        }
        
        /*
         * returns a list of lists of dominos, including
         * all possible lists of dominos of length "length."
         * The same domino may appear repeatedly in a list, any number
         * of times up to length.
         */
        public List<List<Domino>> getListsOfLength(final int length) {
            List<List<Domino>> result = new ArrayList<List<Domino>>();
            
            // "next" stores the series of indexes in this.dominos
            // to use in the current list of dominos, initialized
            // to all 0's (the first element in every place).
            int[] next = new int[length];
            for (int i = 0; i < length; i++) {
                next[i] = 0;
            }
            
            do {
                List<Domino> list = new ArrayList<Domino>();
                
                // build the current list by taking the
                // item in this.dominos specified in "next"
                // for each place.
                for (int i = 0; i < length; i++) {
                    list.add(this.dominos.get(next[i]));
                }
                result.add(list);
            } while (increment(next, this.dominos.size() - 1));
            
            return result;
        }
        
        /*
         * increments the array's value in "counting" style, with maxIndex as the highest
         * value allowed in a place.
         * 
         * returns false if at the max.
         * 
         * for example, given ({0, 1, 1}, 1) this method
         * should change the input array to {1, 0, 0} and return true.
         * 
         * ({1, 1, 1}, 1) would return false without changing the array.
         */
        private boolean increment(final int[] arr, final int maxValue) {
            if (isFinished(arr, maxValue)) {
                return false;
            }
            
            // true if there is an addend or carry left to add
            boolean needToAdd = true;
            
            // start adding at the last place
            int currentPlace = arr.length - 1;
            while (needToAdd) {
                // if there will be a carry after adding 1
                if (arr[currentPlace] == maxValue) {
                    // set current place to 0
                    arr[currentPlace] = 0;
                    
                    // move left, and don't change needToAdd
                    currentPlace--;
                } else {
                    // add 1 in current place
                    arr[currentPlace]++;
                    
                    // no need to carry
                    needToAdd = false;
                }
            }
            
            return true;
        }
        
        /*
         * returns true if all places in the array have the maxValue
         */
        private boolean isFinished(final int[] arr, final int maxValue) {
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] != maxValue) {
                    return false;
                }
            }
            
            return true;
        }
    }
    
    private static final class Domino {
        
        private final List<Integer> top;
        private final List<Integer> bottom;
        
        public Domino(final Integer[] aTop, final Integer[] aBottom) {
            this.top = Arrays.asList(aTop);
            this.bottom = Arrays.asList(aBottom);
        }
        
        public List<Integer> getTop() {
            return this.top;
        }
        
        public List<Integer> getBottom() {
            return this.bottom;
        }
        
        @Override
        public int hashCode() {
            int result = 1;
            result = result * 37 + top.hashCode();
            result = result * 11 + bottom.hashCode();
            return result;
        }
        
        // two dominos are equivalent if their lists
        // have the same items
        @Override
        public boolean equals(final Object that) {
            if (that == null) {
                return false;
            }
            
            if (that instanceof Domino) {
                Domino other = (Domino) that;
                if (
                    this.top.size() != other.getTop().size()
                    || this.bottom.size() != other.getBottom().size()
                ) {
                    return false;
                }
                
                for (int i = 0; i < this.top.size(); i++) {
                    if (this.top.get(i) != other.getTop().get(i)) {
                        return false;
                    }
                }
                
                for (int i = 0; i < this.bottom.size(); i++) {
                    if (this.bottom.get(i) != other.getBottom().get(i)) {
                        return false;
                    }
                }
                
                return true;
            }
            
            return false;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Domino [top=");
            builder.append(top);
            builder.append(", bottom=");
            builder.append(bottom);
            builder.append("]");
            return builder.toString();
        }
    }
}
