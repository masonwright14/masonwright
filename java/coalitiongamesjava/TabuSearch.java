package coalitiongames;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

abstract class TabuSearch {

    public static SearchResult tabuSearch(
        final List<Agent> agents,
        final GammaZ gammaZ,
        final int kMax,
        final int kMin
    ) {
        final int defaultQueueLength = 100;
        return tabuSearch(defaultQueueLength, agents, gammaZ, kMax, kMin);
    }
    
    public static SearchResult tabuSearch(
        final int queueLength,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final int kMax,
        final int kMin
    ) {
        assert queueLength > 0;
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        assert gammaZ != null;
        assert kMax >= kMin;
        assert kMin >= 0;
        assert checkKRange(agents.size(), kMin, kMax);
        
        final int n = agents.size();
        final double maxPrice = 
            MipGenerator.MIN_BUDGET + MipGenerator.MIN_BUDGET / n;
        final double maxBudget = 
            MipGenerator.MIN_BUDGET 
            + MipGenerator.MIN_BUDGET / n;
        PriceWithError currentNode = 
            getInitialPriceWithError(n, kMax, kMin, agents, maxPrice, gammaZ);
        PriceWithError bestNode = currentNode;
        
        final Queue<PriceWithError> tabuQueue = 
            new DropOutQueue<PriceWithError>(queueLength);
        while (bestNode.getErrorValue() > 0.0) {
            tabuQueue.add(currentNode);
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
                if (!tabuQueue.contains(neighbor)) {
                    currentNode = neighbor;
                    // found a neighbor not already in the tabu queue
                    newNeighborFound = true;
                    if (
                        currentNode.getErrorValue() < bestNode.getErrorValue()
                    ) {
                        bestNode = currentNode;
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
                
        final SearchResult result = new SearchResult(
            bestNode.getPrices(), 
            bestNode.getDemand(), 
            bestNode.getError(), 
            bestNode.getErrorValue(), 
            kMin, 
            kMax, 
            maxBudget, 
            agents
        );
        return result;
    }
    
    /*
     * n should be the total agent count, not "total agents - 1"
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
    
    private static PriceWithError getInitialPriceWithError(
        final int n,
        final int kMax,
        final int kMin,
        final List<Agent> agents,
        final double maxPrice,
        final GammaZ gammaZ
    ) {
        final List<Double> prices = new ArrayList<Double>();
        final double basePrice = MipGenerator.MIN_BUDGET / kMax;
        for (int i = 1; i <= n; i++) {
            prices.add(basePrice);
        }
        List<List<Integer>> aggregateDemand = 
            DemandGenerator.getAggregateDemand(
                agents, 
                prices, 
                kMax, 
                kMin, 
                maxPrice
            );
        List<Double> errorDemand = 
            gammaZ.z(aggregateDemand, prices, kMax, maxPrice);
        double error = DemandAnalyzer.errorSizeDouble(errorDemand);
        return new PriceWithError(prices, errorDemand, aggregateDemand, error);
    }
}
