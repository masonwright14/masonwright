package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

abstract class MipChecker {
    
    public static boolean isFeasible(
        final List<Double> prices,
        final double budget,
        final int kMin
    ) {
        // prices excludes the self agent,
        // so kMin can be 1 greater than its length
        if (kMin > prices.size() + 1) {
            return false;
        }
        if (kMin <= 1) {
            return true;
        }
        
        final List<Double> myPrices = new ArrayList<Double>(prices);
        Collections.sort(myPrices);
        double minTotal = 0.0;
        // take cheapest (kMin - 1) other agents
        for (int i = 0; i < kMin - 1; i++) {
            minTotal += prices.get(i);
        }
        
        return budget >= minTotal;
    }

    public static boolean checkLpSolution(
        final MipResult solution,
        final List<Double> values,
        final List<Double> prices,
        final double budget,
        final int kMax,
        final int kMin,
        final int iterations
    ) {
        final List<Double> columnValues = solution.getColumnValues();
        
        // check if number of selected agents is in (kMin, kMax)
        int countOnes = 0;
        final double epsilon = 0.00001; // tolerance for floating point
        for (Double columnValue: columnValues) {
            if (Math.abs(columnValue) > epsilon 
                && Math.abs(columnValue - 1.0) > epsilon
            ) {
                System.out.println("Value not in {0, 1}: " + columnValue);
                return false;
            }
            if (Math.abs(columnValue - 1.0) <= epsilon) {
                countOnes++;
            }
        }
        
        if (countOnes < kMin - 1 || countOnes > kMax - 1) {
            System.out.println("Wrong number of ones");
            return false;
        }
        
        double total = 0;
        
        // allow for rounding error in GLPK solver constraints
        final double overBudgetTolerance = 0.001;
        for (int i = 0; i < prices.size(); i++) {
            total += columnValues.get(i) * prices.get(i);
        }
        if (total - overBudgetTolerance > budget) {
            System.out.println("Over budget: " + total);
            System.out.println("Budget: " + budget);
            System.out.println("Total: " + total);
            System.out.println("Prices: " + prices);
            System.out.println("Amounts: " + columnValues);
            return false;
        }
        
        double value = 0.0;
        for (int i = 0; i < values.size(); i++) {
            value += columnValues.get(i) * values.get(i);
        }      
        final double referenceValue = value;
        
        // for "iterations" number of trials, (pick kMax - 1) items 
        // at random and, if the
        // set is affordable, test if it is preferred to the given set.
        final int[] demand = new int[prices.size()];
        for (int iter = 0; iter < iterations; iter++) {
            // pick (kMax - 1) items at random
            // initialize all items to 0, not picked
            for (
                int demandIndex = 0; 
                demandIndex < demand.length; 
                demandIndex++
            ) {
                demand[demandIndex] = 0;
            }
            // pick kMax - 1 items.
            int ones = kMax - 1;
            
            for (int index = 0; index < demand.length; index++) {
                // pick an an item from {0, 1, last - # already picked}
                final int randIndex = 
                    (int) (Math.random() * (demand.length - index));
                // if this number is <= the number of 1's "left" to be picked,
                // count it as drawing a 1, and set the current index to 1.
                // decrement the number of 1's left to pick.
                if (randIndex < ones) {
                    demand[index] = 1;
                    ones--;
                }
            }
            
            // get cost of the random bundle
            total = 0;
            for (int i = 0; i < demand.length; i++) {
                total += demand[i] * prices.get(i);
            }
            // test if bundle is affordable
            if (total <= budget) {
                // get value of the random bundle
                double iterValue = 0.0;
                for (int i = 0; i < demand.length; i++) {
                    iterValue += demand[i] * values.get(i);
                }
                if (iterValue > referenceValue) {
                    System.out.println(
                        "Preferred set: " + Arrays.toString(demand)
                    );
                    System.out.println("Preferred set value: " + iterValue);
                    System.out.println("Reference value: " + referenceValue);
                    return false;
                }
            }
        }
        
        return true;
    }
}
