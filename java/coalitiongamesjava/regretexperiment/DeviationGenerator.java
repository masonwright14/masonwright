package regretexperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DeviationGenerator {
    
    public static void main(final String[] args) {
        // testPoisson();
        // testGetIntList();
        // testGetSwapIndex();
        // testSwap();
        // testGetDeviationList();
        testGetDeviationLists();
    }
    
    /*
     * the returned list is guaranteed not to contain any duplicate 
     * deviations.
     */
    public static List<List<Integer>> getDeviationLists(
        final int numberOfDeviations,
        final int otherAgentCount
    ) {
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
        // failureCount holds the number of times a generated deviation
        // was not usable because it was already in the list to return.
        int failureCount = 0;
        final int maxFailures = 10000;
        while (result.size() < numberOfDeviations) {
            if (failureCount > maxFailures) {
                throw new IllegalStateException(
                    "can't generate enough deviations"
                );
            }
            
            final List<Integer> deviation = getDeviationList(otherAgentCount);
            // don't add the same deviation more than once to the result
            if (result.contains(deviation)) {
                failureCount++;
            } else {
                result.add(deviation);
            }
        }
        return result;
    }
    
    /**
     * Returns a permutation of the integers from 1 to otherAgentCount.
     * The integers start out in ascending sorted order.
     * A random number of pairs to swap is generated from a Poisson
     * distribution plus 1, where lambda == 1.
     * For each pair to swap, the first index to swap is chosen 
     * such that each value x's probability of being chosen is
     * equal to (otherAgentCount + 1 - x) * 2 / 
     *  (otherAgentCount) (otherAgentCount + 1).
     *  that is, 1 has otherAgentCount chances to be picked,
     *  otherAgentCount has 1 chance to be picked, etc.
     * 
     * the distance away to swap with is chosen from a Poisson
     * distribution of lambda == 2, plus 1.
     * 
     * the direction of the second agent to swap (better or worse rank)
     * is chosen with Pr == 0.5.
     * 
     * If there is some value in {1, . . ., otherAgentCount} at the
     * correct rank, swap with it. Else if there is any other value
     * in the proper direction, swap with it. Else swap with the
     * appropriate value in the other direction, if any. Else,
     * swap with the furthest agent in the other direction.
     * 
     * Example:
     * There are 5 other agents.
     * Start with: {1, 2, 3, 4, 5}
     * First agent to swap: #1
     * Distance away to swap: 5 places
     * Direction to swap: better rank.
     * There is no better rank to swap with.
     * There is no rank 6 to swap with, so swap with rank 5.
     * Result: {5, 2, 3, 4, 1}.
     * 
     * @param otherAgentCount how many other agents there
     * are in the model, whose utilities must be reported.
     * one less than total number of agents.
     * @return a list of the integers from 1 to otherAgentCount,
     * representing a permutation of the agent's preference order.
     * 
     * Example:
     * agent's truthful utilities for 4 other agents:
     * 4.0 1.0 5.0 0.0
     * agent's implied 1-based truthful ranks for the 4 other agents:
     * 2   3   1   4
     * an example result from this method, derived by swapping
     * the first and second-ranked agents. note that the list is
     * a permutation in utility rank order, not the order of the
     * agents list:
     * 2   1   3   4
     * the implied resulting reported utilities:
     * 5.0 1.0 4.0 0.0
     * this is derived by swapping the utilities of the 1st- and 2nd-
     * highest agents.
     */
    public static List<Integer> getDeviationList(
        final int otherAgentCount
    ) {
        final int numberOfSwaps = getNumberOfSwaps();
        final List<Integer> result = getIntList(otherAgentCount);
        final double half = 0.5;
        for (int i = 0; i < numberOfSwaps; i++) {
            int firstSwapIndex = getSwapIndex(otherAgentCount);
            final int distance = getSwapDistance();
            final boolean swapWithBetter = Math.random() < half;
            if (swapWithBetter) {
                // try to swap with better index
                final int betterIndex = firstSwapIndex - distance;
                if (betterIndex < 1) {
                    // can't swap this far apart
                    if (firstSwapIndex == 1) {
                        // can't swap with better index
                        final int worseIndex = firstSwapIndex + distance;
                        if (worseIndex > otherAgentCount) {
                            // can't make the swap. swap with last index.
                            swap(
                                result, firstSwapIndex - 1, otherAgentCount - 1
                            );
                        } else {
                            // make the swap
                            swap(result, firstSwapIndex - 1, worseIndex - 1);
                        }
                    } else {
                        // swap with first agent
                        swap(result, firstSwapIndex - 1, 0);
                    }
                } else {
                    // make the swap
                    swap(result, firstSwapIndex - 1, betterIndex - 1);
                }
            } else {
                // try to swap with worse index
                final int worseIndex = firstSwapIndex + distance;
                if (worseIndex > otherAgentCount) {
                    // can't swap this far apart
                    if (firstSwapIndex == otherAgentCount) {
                        // can't swap with worse index
                        final int betterIndex = firstSwapIndex - distance;
                        if (betterIndex < 1) {
                            // can't make the swap. swap with best index.
                            swap(result, firstSwapIndex - 1, 0);
                        } else {
                            // make the swap
                            swap(result, firstSwapIndex - 1, betterIndex - 1);
                        }
                    } else {
                        // swap with last agent
                        swap(result, firstSwapIndex - 1, otherAgentCount - 1);
                    }
                } else {
                    // make the swap
                    swap(result, firstSwapIndex - 1, worseIndex - 1);
                }
            }
        }
        
        return result;
    }
    
    private static void swap(
        final List<Integer> myList,
        final int aIndex,
        final int bIndex
    ) {
        if (
            aIndex < 0 || aIndex >= myList.size() 
            || bIndex < 0 || bIndex >= myList.size()
        ) {
            throw new IllegalArgumentException();
        }
        final int a = myList.get(aIndex);
        myList.set(aIndex, myList.get(bIndex));
        myList.set(bIndex, a);
    }
    
    /*
     * The index to swap is chosen 
     * such that each value x's probability of being chosen is
     * equal to (otherAgentCount + 1 - x) * 2 / 
     *  (otherAgentCount) (otherAgentCount + 1).
     *  that is, 1 has otherAgentCount chances to be picked,
     *  otherAgentCount has 1 chance to be picked, etc.
     *  
     *  Result will be in {1, 2, . . ., otherAgentCount}.
     */
    private static int getSwapIndex(final int otherAgentCount) {
        final double sumOfRanks = 
            otherAgentCount * (otherAgentCount + 1) / 2.0;
        final double randomDraw = Math.random() * sumOfRanks;
        double cumulative = 0.0;
        for (int rank = 1; rank <= otherAgentCount; rank++) {
            cumulative += otherAgentCount + 1 - rank;
            if (randomDraw < cumulative) {
                return rank;
            }
        }
        
        final double tolerance = 0.001;
        assert Math.abs(cumulative - sumOfRanks) < tolerance;
        
        return otherAgentCount;
    }
    
    private static int getSwapDistance() {
        return 1 + poisson(2);
    }
    
    private static List<Integer> getIntList(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException();
        }
        
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 1; i <= n; i++) {
            result.add(i);
        }
        
        return result;
    }
    
    private static int getNumberOfSwaps() {
        return 1 + poisson(1);
    }
    
    private static int poisson(final double lambda) {
        assert lambda >= 0.0;
        final double eToNegLambda = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
          k++;
          p *= Math.random();
        } while (p > eToNegLambda);

        return k - 1;
    }
    
    /********************************************************
     * TESTING
     */
    
    private static void testGetDeviationLists() {
        final int otherAgents = 19;
        final int deviationNumber = 100;
        final List<List<Integer>> deviations = 
            getDeviationLists(deviationNumber, otherAgents);
        if (deviations.size() != deviationNumber) {
            throw new IllegalStateException();
        }
        
        for (final List<Integer> deviation: deviations) {
            final int occurrences = 
                Collections.frequency(deviations, deviation);
            if (occurrences != 1) {
                throw new IllegalStateException("duplicate deviation found");
            }
            System.out.println(deviation);
        }
    }
    
    @SuppressWarnings("unused")
    private static void testGetDeviationList() {
        final int otherAgents = 19;
        final int iterations = 20;
        for (int i = 0; i < iterations; i++) {
            System.out.println(getDeviationList(otherAgents));
        }
    }
    
    @SuppressWarnings("unused")
    private static void testSwap() {
        final int otherAgents = 19;
        final List<Integer> myList = getIntList(otherAgents);
        final int firstValue = 2;
        final int secondValue = 5;
        swap(myList, firstValue - 1, secondValue - 1);
        System.out.println(myList);
    }
    
    @SuppressWarnings("unused")
    private static void testGetSwapIndex() {
        final int otherAgents = 19;
        final int trials = 1000;
        final List<Integer> swapIndexes = new ArrayList<Integer>();
        for (int i = 0; i < trials; i++) {
            final int swapIndex = getSwapIndex(otherAgents);
            if (swapIndex < 1 || swapIndex > otherAgents) {
                throw new IllegalStateException();
            }
            swapIndexes.add(swapIndex);
        }
        
        for (int rank = 1; rank <= otherAgents; rank++) {
            final int occurrences = Collections.frequency(swapIndexes, rank);
            final double frequency = ((double) occurrences) / trials;
            System.out.println(rank + ": " + frequency);
        }
    }
    
    @SuppressWarnings("unused")
    private static void testGetIntList() {
        final int otherAgents = 19;
        System.out.println(getIntList(otherAgents));
    }
    
    @SuppressWarnings("unused")
    private static void testPoisson() {
        final List<Integer> values = new ArrayList<Integer>();
        final int count = 1000;
        final double lambda = 2.0;
        for (int i = 0; i < count; i++) {
            final int newValue = poisson(lambda);
            if (newValue < 0) {
                throw new IllegalArgumentException();
            }
            
            values.add(poisson(lambda));
        }
        System.out.println("mean: " + mean(values));
        System.out.println("should be: " + lambda);
        System.out.println("variance: " + variance(values));
        System.out.println("should be: " + lambda);
    }
    
    private static double mean(final List<Integer> aList) {
        if (aList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        
        double result = 0.0;
        for (Integer i: aList) {
            result += i;
        }
        return result / aList.size();
    }
    
    private static double variance(final List<Integer> aList) {
        final double mean = mean(aList);
        double result = 0.0;
        for (Integer i: aList) {
            final double diff = i - mean;
            result += diff * diff;
        }
        return result / aList.size();
    }
}
