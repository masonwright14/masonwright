package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import coalitiongames.PriceWithError.PriceUpdateSource;

public final class SearchResult extends SimpleSearchResult {

    private final List<Double> prices;
    private final List<Double> budgets;
    private final List<Double> error;
    private final double errorSize;
    private final double maxBudget;
    private final List<Double> bestErrorValues;
    private final List<PriceUpdateSource> priceUpdateSources;
    private final int tabuSearchCalls;
    
    public SearchResult(
        final List<Double> aPrices,
        final List<List<Integer>> aAllocation,
        final List<Double> aError,
        final double aErrorSize,
        final double aMaxBudget,
        final List<Agent> aAgents,
        final long aDurationMillis,
        final List<Integer> aRsdOrder,
        final List<Double> aBestErrorValues,
        final List<PriceUpdateSource> aPriceUpdateSources,
        final int aTabuSearchCalls,
        final List<Integer> aCaptainIndexes
    ) {
        super(
            aAllocation,
            -1,
            -1,
            aAgents,
            aRsdOrder,
            aDurationMillis,
            aCaptainIndexes
        );
        
        if (aPrices != null) {
            assert aPrices.size() == aError.size();
        }
        assert aMaxBudget >= MipGenerator.MIN_BUDGET;
        assert aDurationMillis >= 0;
        
        this.prices = new ArrayList<Double>();
        if (aPrices != null) {
            for (double aPrice: aPrices) {
                this.prices.add(aPrice);
            }
        }
        this.budgets = new ArrayList<Double>();
        for (Agent agent: aAgents) {
            this.budgets.add(agent.getBudget());
        }
        this.error = new ArrayList<Double>();
        for (double item: aError) {
            this.error.add(item);
        }
        this.errorSize = aErrorSize;

        this.maxBudget = aMaxBudget;
        
        this.bestErrorValues = new ArrayList<Double>();
        if (aBestErrorValues != null) {
            for (Double bestErrorValue: aBestErrorValues) {
                this.bestErrorValues.add(bestErrorValue);
            }
        }
        this.priceUpdateSources = new ArrayList<PriceUpdateSource>();
        if (aPriceUpdateSources != null) {
            for (PriceUpdateSource priceUpdateSource: aPriceUpdateSources) {
                this.priceUpdateSources.add(priceUpdateSource);
            }
        }

        this.tabuSearchCalls = aTabuSearchCalls;
    }
    
    public SearchResult(
        final List<Double> aPrices,
        final List<List<Integer>> aAllocation,
        final List<Double> aError,
        final double aErrorSize,
        final int aKMin,
        final int aKMax,
        final double aMaxBudget,
        final List<Agent> aAgents,
        final long aDurationMillis,
        final List<Integer> aRsdOrder,
        final List<Double> aBestErrorValues,
        final List<PriceUpdateSource> aPriceUpdateSources,
        final int aTabuSearchCalls,
        final List<Integer> aCaptainIndexes
    ) {
        super(
            aAllocation,
            aKMin,
            aKMax,
            aAgents,
            aRsdOrder,
            aDurationMillis,
            aCaptainIndexes
        );
        
        if (aPrices != null) {
            assert aPrices.size() == aError.size();
        }
        assert aKMin <= aKMax;
        assert aMaxBudget >= MipGenerator.MIN_BUDGET;
        assert aDurationMillis >= 0;
        
        this.prices = new ArrayList<Double>();
        if (aPrices != null) {
            for (double aPrice: aPrices) {
                this.prices.add(aPrice);
            }
        }
        this.budgets = new ArrayList<Double>();
        for (Agent agent: aAgents) {
            this.budgets.add(agent.getBudget());
        }
        this.error = new ArrayList<Double>();
        for (double item: aError) {
            this.error.add(item);
        }
        this.errorSize = aErrorSize;
        this.maxBudget = aMaxBudget;
        
        this.bestErrorValues = new ArrayList<Double>();
        if (aBestErrorValues != null) {
            for (Double bestErrorValue: aBestErrorValues) {
                this.bestErrorValues.add(bestErrorValue);
            }
        }

        this.priceUpdateSources = new ArrayList<PriceUpdateSource>();
        if (aPriceUpdateSources != null) {
            for (PriceUpdateSource priceUpdateSource: aPriceUpdateSources) {
                this.priceUpdateSources.add(priceUpdateSource);
            }
        }

        this.tabuSearchCalls = aTabuSearchCalls;
    }

    public List<Double> getPrices() {
        return prices;
    }

    public List<Double> getBudgets() {
        return budgets;
    }
    
    /*
     * 0-based ranks, so highest budget has rank 0, lowest has rank (N - 1).
     * Assumes no 2 budgets are equal.
     */
    public List<Integer> getBudgetRanks() {
        final List<Integer> result = new ArrayList<Integer>();
        final List<Double> sortedBudgets = new ArrayList<Double>(this.budgets);
        Collections.sort(sortedBudgets);
        Collections.reverse(sortedBudgets);
        for (int i = 0; i < sortedBudgets.size(); i++) {
            final double ithAgentBudget = this.budgets.get(i);
            final int ithBudgetRank = sortedBudgets.indexOf(ithAgentBudget);
            if (ithAgentBudget == -1) {
                throw new IllegalStateException();
            }
            result.add(ithBudgetRank);
        }
        
        if (result.size() != this.budgets.size()) {
            throw new IllegalStateException();

        }
        for (int i = 0; i < result.size(); i++) {
            if (!result.contains(i)) {
                throw new IllegalStateException();
            }
        }
        
        return result;
    }

    public List<Double> getError() {
        return error;
    }

    public double getErrorSize() {
        return errorSize;
    }

    public double getMaxBudget() {
        return maxBudget;
    }
    
    public List<Double> getBestErrorValues() {
        return this.bestErrorValues;
    }
    
    public List<PriceUpdateSource> getPriceUpdateSources() {
        return this.priceUpdateSources;
    }
    
    public int getTabuSearchCalls() {
        return tabuSearchCalls;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SearchResult \n[prices=");
        builder.append(prices);
        builder.append(", \nbudgets=");
        builder.append(budgets);
        builder.append(", \nallocation=\n");
        builder.append(Util.demandAsMatrix(getAllocation()));
        builder.append("\n, error=");
        builder.append(error);
        builder.append(", \nerrorSize=");
        builder.append(errorSize);
        builder.append(", \nkMin=");
        builder.append(getkMin());
        builder.append(", \nkMax=");
        builder.append(getkMax());
        builder.append(", \nteamSizes=");
        builder.append(getTeamSizesWithSelf());        
        builder.append(", \nmaxBudget=");
        builder.append(maxBudget);
        builder.append(", \nagents=");
        builder.append(getAgents());
        builder.append(", \ndurationMillis=");
        builder.append(getDurationMillis());  
        builder.append(", \nrsdOrder=");
        builder.append(getRsdOrder()); 
        builder.append(", \nbestErrorValues=");
        builder.append(bestErrorValues); 
        builder.append(", \npriceUpdateSources=");
        builder.append(priceUpdateSources); 
        builder.append(", \ntabuSearchCalls=");
        builder.append(tabuSearchCalls);
        builder.append(", \nisCaptain=");
        builder.append(isCaptain());
        builder.append("]");
        return builder.toString();
    }
    
    /*****************************************************************
     * TESTING
     */
    
    public static void main(final String[] args) {
        testBudgetRanks();
    }
    
    private static void testBudgetRanks() {
        final int n = 5;
        List<Double> prices = new ArrayList<Double>();
        List<List<Integer>> allocation = new ArrayList<List<Integer>>();
        List<Double> error = new ArrayList<Double>();
        List<Agent> agents = new ArrayList<Agent>();
        List<Double> values = new ArrayList<Double>();
        List<UUID> uuids = new ArrayList<UUID>();
        final double[] budgets = {3, 1, 2, 5, 4};
        for (int i = 0; i < n; i++) {
            Agent agent = new Agent(values, uuids, budgets[i], 0, null);
            agents.add(agent);
        }
        
        /*
         *         final List<Double> aPrices,
        final List<List<Integer>> aAllocation,
        final List<Double> aError,
        final double aErrorSize,
        final int aKMin,
        final int aKMax,
        final double aMaxBudget,
        final List<Agent> aAgents,
        final long aDurationMillis,
        final List<Integer> aRsdOrder,
        final List<Double> aBestErrorValues,
        final List<PriceUpdateSource> aPriceUpdateSources,
        final int aTabuSearchCalls,
        final List<Integer> aCaptainIndexes
         */
        
        final int maxBudget = 200;
        SearchResult sr = new SearchResult(
            prices,
            allocation, 
            error,
            0,
            1, 
            n, 
            maxBudget,
            agents, 
            0,
            null,
            null,
            null,
            0,
            null
        );
        // should be: 2 4 3 0 1
        System.out.println(sr.getBudgetRanks());
    }
}
