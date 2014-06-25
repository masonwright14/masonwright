package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class MipChecker {
    
    /**
     * 
     * @param prices price of each agent, other than the self agent
     * @param budget budget of the self agent
     * @param kMin minimum number of agents per team, including
     * the self agent
     * @return whether any bundle of size kMin (including the self
     * agent, which has no cost, so (kMin - 1) other agents) is
     * affordable given the budget.
     */
    public static boolean isFeasible(
        final List<Double> prices,
        final double budget,
        final int kMin
    ) {
        // not feasible even if kMin is 0, if budget is negative.
        if (budget < 0.0) {
            return false;
        }
        
        // prices excludes the self agent,
        // so kMin can be 1 greater than its length
        if (kMin > prices.size() + 1) {
            return false;
        }
        if (kMin <= 1) {
            return true;
        }
        
        final List<Double> myPrices = new ArrayList<Double>(prices);
        // sorted from lowest to highest
        Collections.sort(myPrices);
        assert myPrices.get(0) <= myPrices.get(myPrices.size() - 1);
        double minTotal = 0.0;
        // take cheapest (kMin - 1) other agents
        for (int i = 0; i < kMin - 1; i++) {
            minTotal += myPrices.get(i);
        }
        
        final double tolerance = 0.001;
        return budget - tolerance >= minTotal;
    }
    
    public static boolean checkFreeDummyLpSolution(
        final List<Integer> solution,
        final List<Double> teamValues,
        final List<Double> teamPrices,
        final List<Integer> teamAgentsNeeded,
        final List<Double> agentValues,
        final List<Double> agentPrices,
        final double budget,
        final int iterations
    ) {
        final int otherFreeAgentsLeft = agentPrices.size();
        final int teamsLeft = teamValues.size();
        assert solution.size() == teamsLeft + otherFreeAgentsLeft;
        
        int countTeamsSelected = 0;
        int myTeamIndex = -1;
        for (int i = 0; i < teamsLeft; i++) {
            if (solution.get(i) != 0 && solution.get(i) != 1) {
                System.out.println("wrong demand for team");
                return false;
            }
            countTeamsSelected += solution.get(i);
            if (solution.get(i) == 1) {
                myTeamIndex = i;
            }
        }
        if (countTeamsSelected != 1) {
            System.out.println("wrong number of teams");
            return false;
        }
        
        assert myTeamIndex != -1;
        final int otherAgentsNeeded = teamAgentsNeeded.get(myTeamIndex) - 1;
        
        int otherFreeAgentsSelected = 0;
        for (int i = teamsLeft; i < solution.size(); i++) {
            if (solution.get(i) != 0 && solution.get(i) != 1) {
                System.out.println("wrong demand for agent");
                return false;
            }
            otherFreeAgentsSelected += solution.get(i);
        }
        if (otherFreeAgentsSelected != otherAgentsNeeded) {
            System.out.println("Took wrong number of free agents");
            System.out.println("solution: " + solution);
            System.out.println("team agents needed:" + teamAgentsNeeded);
            System.out.println("other agents needed: " + otherAgentsNeeded);
            System.out.println(
                "other free agents selected: " + otherFreeAgentsSelected
            );
            System.out.println("team index: " + myTeamIndex);
            return false;
        }
        
        final double overBudgetTolerance = 0.01;
        double total = 0.0;
        for (int i = 0; i < teamPrices.size(); i++) {
            total += solution.get(i) * teamPrices.get(i);
        }
        for (int i = teamPrices.size(); i < solution.size(); i++) {
            total += solution.get(i) * agentPrices.get(i - teamPrices.size());
        }
        if (total - overBudgetTolerance > budget) {
            System.out.println("Over budget: " + total);
            System.out.println("Budget: " + budget);
            System.out.println("Total: " + total);
            return false;
        }
        
        double value = 0.0;
        for (int i = 0; i < teamValues.size(); i++) {
            value += solution.get(i) * teamValues.get(i);
        }      
        for (int i = teamValues.size(); i < solution.size(); i++) {
            value += solution.get(i) * agentValues.get(i - teamValues.size());
        }
        final double referenceValue = value;
        
        final int[] demand = new int[agentPrices.size()];
        final Random rand = new Random();
        for (int iter = 0; iter < iterations; iter++) {
            final int iterTeamIndex = rand.nextInt(teamsLeft);
            final int iterOtherAgentsNeeded = 
                teamAgentsNeeded.get(iterTeamIndex) - 1;
            
            // pick iterOtherAgentsNeeded items at random
            // initialize all items to 0, not picked
            for (
                int demandIndex = 0; 
                demandIndex < demand.length; 
                demandIndex++
            ) {
                demand[demandIndex] = 0;
            }
            // pick iterOtherAgentsNeeded items.
            int ones = iterOtherAgentsNeeded;
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
            total += teamPrices.get(iterTeamIndex);
            for (int i = 0; i < demand.length; i++) {
                total += demand[i] * agentPrices.get(i);
            }
            // test if bundle is affordable
            if (total <= budget) {
                // get value of the random bundle
                double iterValue = 0.0;
                iterValue += teamValues.get(iterTeamIndex);
                for (int i = 0; i < demand.length; i++) {
                    iterValue += demand[i] * agentValues.get(i);
                }                
                if (iterValue - overBudgetTolerance > referenceValue) {
                    System.out.println("Preferred team: " + iterTeamIndex);
                    System.out.println(
                        "Preferred agents: " + Arrays.toString(demand)
                    );
                    System.out.println("Reference set: " + solution);
                    System.out.println("Preferred set value: " + iterValue);
                    System.out.println("Reference value: " + referenceValue);
                    System.out.println("Cost: " + total);
                    System.out.println("Budget: " + budget);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public static boolean checkFreeCaptainLpSolution(
        final List<Integer> solution,
        final List<Double> teamValues,
        final List<Double> teamPrices,
        final List<Integer> teamAgentsNeeded,
        final double meanOtherFreeAgentValue,
        final List<Double> agentPrices,
        final double budget,
        final int iterations
    ) {
       final int otherFreeAgentsLeft = agentPrices.size();
       final int teamsLeft = teamValues.size();
       assert solution.size() == teamsLeft + otherFreeAgentsLeft;
       
       int countTeamsSelected = 0;
       int myTeamIndex = -1;
       for (int i = 0; i < teamsLeft; i++) {
           if (solution.get(i) != 0 && solution.get(i) != 1) {
               System.out.println("wrong demand for team");
               return false;
           }
           countTeamsSelected += solution.get(i);
           if (solution.get(i) == 1) {
               myTeamIndex = i;
           }
       }
       if (countTeamsSelected != 1) {
           System.out.println("wrong number of teams");
           return false;
       }
       
       assert myTeamIndex != -1;
       final int otherAgentsNeeded = teamAgentsNeeded.get(myTeamIndex) - 1;
       
       int otherFreeAgentsSelected = 0;
       for (int i = teamsLeft; i < solution.size(); i++) {
           if (solution.get(i) != 0 && solution.get(i) != 1) {
               System.out.println("wrong demand for agent");
               return false;
           }
           otherFreeAgentsSelected += solution.get(i);
       }
       if (otherFreeAgentsSelected != otherAgentsNeeded) {
           System.out.println("Took wrong number of free agents");
           System.out.println("solution: " + solution);
           System.out.println("team agents needed:" + teamAgentsNeeded);
           System.out.println("other agents needed: " + otherAgentsNeeded);
           System.out.println(
               "other free agents selected: " + otherFreeAgentsSelected
           );
           System.out.println("team index: " + myTeamIndex);
           return false;
       }
       
       final double overBudgetTolerance = 0.01;
       double total = 0.0;
       for (int i = 0; i < teamPrices.size(); i++) {
           total += solution.get(i) * teamPrices.get(i);
       }
       for (int i = teamPrices.size(); i < solution.size(); i++) {
           total += solution.get(i) * agentPrices.get(i - teamPrices.size());
       }
       if (total - overBudgetTolerance > budget) {
           System.out.println("Over budget: " + total);
           System.out.println("Budget: " + budget);
           System.out.println("Total: " + total);
           return false;
       }
       
       double value = 0.0;
       for (int i = 0; i < teamValues.size(); i++) {
           value += solution.get(i) * teamValues.get(i);
       }      
       value += meanOtherFreeAgentValue * otherAgentsNeeded;
       final double referenceValue = value;
       
       final int[] demand = new int[agentPrices.size()];
       Random rand = new Random();
       for (int iter = 0; iter < iterations; iter++) {
           final int iterTeamIndex = rand.nextInt(teamsLeft);
           final int iterOtherAgentsNeeded = 
               teamAgentsNeeded.get(iterTeamIndex) - 1;
           
           // pick iterOtherAgentsNeeded items at random
           // initialize all items to 0, not picked
           for (
               int demandIndex = 0; 
               demandIndex < demand.length; 
               demandIndex++
           ) {
               demand[demandIndex] = 0;
           }
           // pick iterOtherAgentsNeeded items.
           int ones = iterOtherAgentsNeeded;
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
           total += teamPrices.get(iterTeamIndex);
           for (int i = 0; i < demand.length; i++) {
               total += demand[i] * agentPrices.get(i);
           }
           // test if bundle is affordable
           if (total <= budget) {
               // get value of the random bundle
               double iterValue = 0.0;
               iterValue += teamValues.get(iterTeamIndex);
               iterValue += meanOtherFreeAgentValue * iterOtherAgentsNeeded;
               if (iterValue - overBudgetTolerance > referenceValue) {
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
        final double overBudgetTolerance = 0.01;
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
                if (iterValue - overBudgetTolerance > referenceValue) {
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
