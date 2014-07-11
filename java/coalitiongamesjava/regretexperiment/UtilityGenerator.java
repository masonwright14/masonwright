package regretexperiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import coalitiongames.MipGenerator;

public abstract class UtilityGenerator {
    
    public static List<List<Double>> getDeviateUtilityList(
        final int numberOfDeviations,
        final List<Double> truthfulUtilities
    ) {
        final int otherAgentCount = truthfulUtilities.size();
        List<List<Integer>> deviateRankLists = 
            DeviationGenerator.getDeviationLists(
                numberOfDeviations, 
                otherAgentCount
            );
        return getDeviateUtilityList(truthfulUtilities, deviateRankLists);
    }
    
    private static List<List<Double>> getDeviateUtilityList(
        final List<Double> truthfulUtilities,
        final List<List<Integer>> deviateRankLists
    ) {
        final List<List<Double>> result = new ArrayList<List<Double>>();
        for (List<Integer> currentDeviation: deviateRankLists) {
            List<Double> currentUtilities = 
                getDeviateUtilities(truthfulUtilities, currentDeviation);
            if (MipGenerator.DEBUGGING) {
                if (currentUtilities.equals(truthfulUtilities)) {
                    System.out.println("deviation has no change:");
                    System.out.println(currentDeviation);
                    throw new IllegalStateException();
                }
            }
            result.add(currentUtilities);
        }
        
        return result;
    }

    /**
     * 
     * @param oldUtilities has the true utility for the agent,
     * of each other agent.
     * @param deviateRanks holds the values 1 to (N - 1)
     * in a shuffled order, derived by starting with them in order
     * and then swapping some positive number of pairs.
     * deviateRanks represents how the agents' utilities should be
     * rearranged (swapped around) in terms of rank, to generate
     * the result
     * 
     * example:
     * oldUtilities:
     * 4.0 1.0 0.0 5.0 6.0
     * deviateRanks:
     * 1   3   2   4   5 # swap 2nd and 3rd ranked agents
     * ranks:
     * 3   4   5   2   1
     * 
     * result:
     * 5.0 1.0 0.0 4.0 6.0
     * @return
     */
    public static List<Double> getDeviateUtilities(
        final List<Double> oldUtilities,
        final List<Integer> deviateRanks
    ) {
        if (oldUtilities.size() != deviateRanks.size()) {
            throw new IllegalArgumentException();
        }
        
        // tells the rank of each agent in oldUtilities,
        // where 1 is greatest utility and (N - 1) is least
        final List<Integer> ranks = ranks(oldUtilities);
        
        final Double[] resultArr = new Double[oldUtilities.size()];
        for (int rank = 1; rank <= oldUtilities.size(); rank++) {
            // where the agent ranked "rank" is in oldUtilities. 0-based.
            final int oldRankIndex = ranks.indexOf(rank);
            
            // which rank's place to take, for this utility
            final int rankToMoveTo = deviateRanks.indexOf(rank) + 1;            
            // index of the rank to move to in oldUtilities
            final int indexToMoveTo = ranks.indexOf(rankToMoveTo);
            
            // utility with this rank.
            final double utilityForRank = oldUtilities.get(oldRankIndex);
            resultArr[indexToMoveTo] = utilityForRank;
        }
        
        final List<Double> result = Arrays.asList(resultArr);
        if (MipGenerator.DEBUGGING) {
            for (Double value: oldUtilities) {
                if (!result.contains(value)) {
                    throw new IllegalStateException();
                }
            }
        }
        
        if (result.size() != oldUtilities.size()) {
            throw new IllegalStateException();
        }
        
        return result;
    }
    
    /**
     * 
     * @param utilities a list of the utilities for an agent of the other
     * (N - 1) agents, in the order they appear in the "agents" list.
     * @return a list of integers from 1 to (N - 1), where each index
     * i in {0, 1, . . ., N - 2} has the rank (1 best) of the ith agent
     * in the utilities list. 
     * 
     * example: if the ith agent has the
     * 5th greatest utility in "utilities", then the ith value in the
     * result will be 5.
     */
    private static List<Integer> ranks(final List<Double> utilities) {
        if (MipGenerator.DEBUGGING) {
            for (Double value: utilities) {
                final int occurrences = Collections.frequency(utilities, value);
                if (occurrences != 1) {
                    throw new IllegalArgumentException(
                        "duplicate utility found"
                    );
                }
            }
        }
                
        final List<Double> utilitiesCopy = new ArrayList<Double>();
        utilitiesCopy.addAll(utilities);
        // sort values high to low.
        // best rank is 1, which is indexOf the greatest value + 1.
        Collections.sort(utilitiesCopy);
        Collections.reverse(utilitiesCopy);
        
        final List<Integer> result = new ArrayList<Integer>();
        for (int index = 0; index < utilities.size(); index++) {
            final double valueAtIndex = utilities.get(index);
            final int rankOfIndex = utilitiesCopy.indexOf(valueAtIndex) + 1;
            result.add(rankOfIndex);
        }
        
        if (result.size() != utilities.size()) {
            throw new IllegalStateException();
        }
        
        if (MipGenerator.DEBUGGING) {
            for (int i = 1; i <= utilities.size(); i++) {
                if (!result.contains(i)) {
                    throw new IllegalStateException("missing index");
                }
            }
        }
        
        return result;
    }
    
    /********************************************************
     * TESTING
     */
    
    public static void main(final String[] args) {
        // testRanks();
        testGetDeviateUtilties();
    }
    
    /*
     * Should be:
     * 3, 4, 1, 5, 6, 2
     */
    @SuppressWarnings("unused")
    private static void testRanks() {
        final Double[] utilitiesArr = {4.0, 3.0, 6.0, 1.0, 0.0, 5.0};
        final List<Double> utilities = Arrays.asList(utilitiesArr);
        System.out.println(ranks(utilities));
    }
    
    private static void testGetDeviateUtilties() {
        final Double[] utilitiesArr = {4.0, 3.0, 6.0, 1.0, 0.0, 5.0};
        final List<Double> utilities = Arrays.asList(utilitiesArr);
        final Integer[] deviateRanksArr = {1, 2, 3, 4, 5, 6};
        final List<Integer> deviateRanks = Arrays.asList(deviateRanksArr);
        // should be:
        // 4.0, 3.0, 6.0, 1.0, 0.0, 5.0
        System.out.println(getDeviateUtilities(utilities, deviateRanks));
        
        final Integer[] deviateRanksArr2 = {6, 5, 4, 3, 2, 1};
        final List<Integer> deviateRanks2 = Arrays.asList(deviateRanksArr2);
        // should be:
        // 3.0 4.0 0.0 5.0 6.0 1.0
        System.out.println(getDeviateUtilities(utilities, deviateRanks2));

        final Integer[] deviateRanksArr3 = {1, 3, 2, 4, 5, 6};
        final List<Integer> deviateRanks3 = Arrays.asList(deviateRanksArr3);
        // should be:
        // 5.0 3.0 6.0 1.0 0.0 4.0
        System.out.println(getDeviateUtilities(utilities, deviateRanks3));
        
        final Integer[] deviateRanksArr4 = {2, 3, 1, 4, 5, 6};
        final List<Integer> deviateRanks4 = Arrays.asList(deviateRanksArr4);
        // should be:
        // 6.0 3.0 5.0 1.0 0.0 4.0
        System.out.println(getDeviateUtilities(utilities, deviateRanks4));
    }
}
