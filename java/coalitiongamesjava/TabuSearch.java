package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;

abstract class TabuSearch {
    
    public static void main(final String[] args) {
        final Integer[] myArr = {7, 2, 1, 11, 12, 13};
        List<Integer> testList = Arrays.asList(myArr);
        System.out.println(getMinMaxPairs(testList));
        
        final Integer[] myArr2 = {1, 2, 3, 4, 11, 14, 15, 16, 17};
        testList = Arrays.asList(myArr2);
        System.out.println(getMinMaxPairs(testList));
    }

    /**
     * convenience method that calls tabuSearch() with default queue length
     * and maximum number of steps.
     * 
     * @param agents a list of all agents with their budgets and preferences
     * @param gammaZ an error function to use for updating prices
     * @param kMax maximum agents per team, including self
     * @param kMin minimum agents per team, including self
     * @return a SearchResult, including an allocation, price vector,
     * and other data
     */
    public static SearchResult tabuSearch(
        final List<Agent> agents,
        final GammaZ gammaZ,
        final int kMax,
        final int kMin
    ) {
        final int defaultQueueLength = 100;
        final int defaultMaxSteps = 100;
        return tabuSearch(
            defaultQueueLength, 
            defaultMaxSteps, 
            agents, 
            gammaZ, 
            kMax, 
            kMin
        );
    }
    
    /**
     * 
     * @param queueLength maximum length of drop-out tabu queue
     * @param maxSteps maximum number of update steps to perform
     * @param agents a list of all agents with their budgets and preferences
     * @param gammaZ an error function to use for updating prices
     * @param kMax maximum agents per team, including self
     * @param kMin minimum agents per team, including self
     * @return a SearchResult, including an allocation, price vector,
     * and other data
     * @return
     */
    public static SearchResult tabuSearch(
        final int queueLength,
        final int maxSteps,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final int kMax,
        final int kMin
    ) {
        assert queueLength > 0;
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        assert gammaZ != null;
        final int n = agents.size();
        assert kMax <= n;
        assert kMax >= kMin;
        assert kMin >= 0;
        assert checkKRange(n, kMin, kMax);
        
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        final double maxPrice = 
            MipGenerator.MIN_BUDGET + MipGenerator.MIN_BUDGET / n;
        final double maxBudget = maxPrice;
        PriceWithError currentNode = 
            getInitialPriceWithError(kMax, kMin, agents, maxPrice, gammaZ);
        PriceWithError bestNode = currentNode;
        
        final Queue<PriceWithError> tabuQueue = 
            new DropOutQueue<PriceWithError>(queueLength);
        if (MipGenerator.DEBUGGING) {
            System.out.println("Best error: " + bestNode.getErrorValue());
        }
        int step = 0;
        // stop searching if no error at best node
        while (bestNode.getErrorValue() > 0.0) {
            // add current node to tabu queue so it won't be revisited
            tabuQueue.add(currentNode);
            step++;
            if (step > maxSteps) {
                break;
            }
            // get neigbors of currentNode, sorted by increasing error
            final List<PriceWithError> sortedNeighbors = 
                NeighborGenerator.sortedNeighbors(
                    currentNode.getPrices(), 
                    currentNode.getError(), 
                    maxPrice, 
                    agents, 
                    gammaZ, 
                    kMax, 
                    kMin
                );
            // check if any neighbor is not already in the tabu queue
            boolean newNeighborFound = false;
            for (final PriceWithError neighbor: sortedNeighbors) {
                // check if neighbor is not in the tabu queue
                if (!tabuQueue.contains(neighbor)) {
                    currentNode = neighbor;
                    // found a neighbor not already in the tabu queue
                    newNeighborFound = true;
                    if (MipGenerator.DEBUGGING) {
                        System.out.println("Step: " + step);
                        System.out.println(
                            "Current error: " + currentNode.getErrorValue()
                        );
                    }
                    if (
                        currentNode.getErrorValue() < bestNode.getErrorValue()
                    ) {
                        bestNode = currentNode;
                        if (MipGenerator.DEBUGGING) {
                            System.out.println(
                                "Best error: " + bestNode.getErrorValue()
                            );
                        }
                    }
                    // stop searching neighbors for best one not in tabu queue
                    break;
                }
            }
            if (!newNeighborFound) {
                // all neighbors are in the tabu queue
                break;
            }
            if (bestNode != currentNode && !tabuQueue.contains(bestNode)) {
                // no better neighbors found in last "queueLength" steps
                break;
            }
        }
        
        // search complete. return best node.
        
        final long searchDurationMillis = 
            new Date().getTime() - searchStartMillis;
        final SearchResult result = new SearchResult(
            bestNode.getPrices(), 
            bestNode.getDemand(), 
            bestNode.getError(), 
            bestNode.getErrorValue(), 
            kMin, 
            kMax, 
            maxBudget, 
            agents,
            searchDurationMillis
        );
        return result;
    }
    
    /**
     * 
     * @param n total count of agents, NOT excluding the self agent
     * @param kMin proposed minimum agents per team, including self
     * @param kMax proposed maximum agents per tema, including self
     * @return true if n agents can be split into teams of size
     * in {kMin, kMin + 1, . . ., kMax}.
     * this is true if kMin divides n, kMax divides n,
     * or n \ kMin != n \ kMax.
     */
    public static boolean checkKRange(
        final int n,
        final int kMin,
        final int kMax
    ) {
        if (kMin < 0 || kMin > kMax || kMax > n) {
            throw new IllegalArgumentException();
        }
        
        return (kMin < 2) || (n % kMin == 0) 
            || (n % kMax == 0) || (n / kMin != n / kMax);
    }
    
    /**
     * @param legalSizes a list of all legal values, as integers
     * @return a list of lists, where each sublist is of length 2. each sublist
     * contains a first item that is the minimum of a range, and a second item
     * that is a maximum of the same range, possibly equal to the minimum.
     * when all the ranges in the sublists are
     * combined, their union over the integers 
     * produces the original list without
     * duplicates.
     * example input: {7, 2, 1, 11, 12, 13}
     * example output:
     * { {1, 2}, {7, 7}, {11, 13} }.
     * Note that 7 appears as the minimum and maximum of one list.
     * 12 does not appear explicitly, but it is between 11 and 13.
     * 
     * This function is used to produce 1 subset membership constraint 
     * for an MIP, by breaking the MIP with 1 subset membership constraint 
     * into a list of MIP's with linear constraints, one for each subset,
     * and returning the MIP result with the greatest objective
     * value.
     */
    public static List<List<Integer>> getMinMaxPairs(
        final List<Integer> legalSizes
    ) {
        assert legalSizes != null;
        assert !legalSizes.isEmpty();
        
        if (MipGenerator.DEBUGGING) {
            for (Integer legalSize: legalSizes) {
                if (legalSize <= 0) {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        // sort sizes ascending.
        Collections.sort(legalSizes);
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
        // index of the minimum value of the next range
        int minIndex = 0;
        while (minIndex < legalSizes.size()) {
            final int currentMin = legalSizes.get(minIndex);
            int tempMax = currentMin;
            // iterate over later items in the sorted legalSizes, until the next
            // higher value is not 1 greater than the previous value.
            int maxIndex = minIndex + 1;
            while (
                maxIndex < legalSizes.size() 
                && legalSizes.get(maxIndex) == tempMax + 1
            ) {
                tempMax = legalSizes.get(maxIndex);
                maxIndex++;
            }
            
            final List<Integer> newList = new ArrayList<Integer>();
            newList.add(currentMin);
            newList.add(tempMax);
            result.add(newList);
            
            // maxIndex has already been incremented after the last update, 
            // so there is no need to increment it again here.
            minIndex = maxIndex;
        }
        
        return result;
    }
    
    /**
     * @param kMax maximum agents per team, including self
     * @param kMin minimum agents per team, including self
     * @param agents a list of all agents with their budgets and preferences
     * @param maxPrice maximum price an agent can be given
     * @param gammaZ used to evaluate error of a given allocation
     * @return a price vector for the agents, along with the aggregate
     * demand it induces, the error according to gammaZ of that demand,
     * and the l2-norm of this error.
     */
    private static PriceWithError getInitialPriceWithError(
        final int kMax,
        final int kMin,
        final List<Agent> agents,
        final double maxPrice,
        final GammaZ gammaZ
    ) {
        final List<Double> prices = new ArrayList<Double>();
        final double basePrice = MipGenerator.MIN_BUDGET / kMax;
        for (int i = 1; i <= agents.size(); i++) {
            prices.add(basePrice);
        }
        final List<List<Integer>> aggregateDemand = 
            DemandGenerator.getAggregateDemand(
                agents, 
                prices, 
                kMax, 
                kMin, 
                maxPrice
            );
        final List<Double> errorDemand = 
            gammaZ.z(aggregateDemand, prices, kMax, maxPrice);
        final double error = DemandAnalyzer.errorSizeDouble(errorDemand);
        return new PriceWithError(prices, errorDemand, aggregateDemand, error);
    }
}
