package coalitiongames;

import java.util.ArrayList;
import java.util.List;

import coalitiongames.PriceWithError.PriceUpdateSource;

public final class SearchResult {

    private final List<Double> prices;
    private final List<Double> budgets;
    private final List<List<Integer>> allocation;
    private final List<Double> error;
    private final double errorSize;
    private int kMin;
    private int kMax;
    private final double maxBudget;
    private final List<Agent> agents;
    private final long durationMillis;
    private final List<Integer> rsdOrder; // can be null
    private final List<Double> bestErrorValues;
    private final List<PriceUpdateSource> priceUpdateSources;
    private List<Integer> teamSizes;
    
    public SearchResult(
        final List<Double> aPrices,
        final List<List<Integer>> aAllocation,
        final List<Double> aError,
        final double aErrorSize,
        final List<Integer> aTeamSizes,
        final double aMaxBudget,
        final List<Agent> aAgents,
        final long aDurationMillis,
        final List<Integer> aRsdOrder,
        final List<Double> aBestErrorValues,
        final List<PriceUpdateSource> aPriceUpdateSources
    ) {
        assert aPrices.size() == aError.size();
        assert aTeamSizes != null && !aTeamSizes.isEmpty();
        assert aMaxBudget >= MipGenerator.MIN_BUDGET;
        assert aDurationMillis > 0;
        
        this.prices = new ArrayList<Double>();
        for (double aPrice: aPrices) {
            this.prices.add(aPrice);
        }
        this.budgets = new ArrayList<Double>();
        for (Agent agent: aAgents) {
            this.budgets.add(agent.getBudget());
        }
        this.allocation = new ArrayList<List<Integer>>();
        for (List<Integer> row: aAllocation) {
            List<Integer> newRow = new ArrayList<Integer>();
            for (int item: row) {
                newRow.add(item);
            }
            this.allocation.add(newRow);
        }
        this.error = new ArrayList<Double>();
        for (double item: aError) {
            this.error.add(item);
        }
        this.errorSize = aErrorSize;
        this.teamSizes = new ArrayList<Integer>();
        for (Integer teamSize: aTeamSizes) {
            this.teamSizes.add(teamSize);
        }
        this.maxBudget = aMaxBudget;
        this.agents = new ArrayList<Agent>();
        for (Agent agent: aAgents) {
            List<Double> values = new ArrayList<Double>();
            for (double value: agent.getValues()) {
                values.add(value);
            }
            Agent newAgent = 
                new Agent(values, agent.getBudget(), agent.getId());
            this.agents.add(newAgent);
        }
        this.durationMillis = aDurationMillis;
        if (aRsdOrder != null) {
            this.rsdOrder = new ArrayList<Integer>();
            for (Integer item: aRsdOrder) {
                this.rsdOrder.add(item);
            }
        } else {
            this.rsdOrder = null;
        }
        
        this.bestErrorValues = new ArrayList<Double>();
        for (Double bestErrorValue: aBestErrorValues) {
            this.bestErrorValues.add(bestErrorValue);
        }
        this.priceUpdateSources = new ArrayList<PriceUpdateSource>();
        for (PriceUpdateSource priceUpdateSource: aPriceUpdateSources) {
            this.priceUpdateSources.add(priceUpdateSource);
        }
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
        final List<PriceUpdateSource> aPriceUpdateSources
    ) {
        assert aPrices.size() == aError.size();
        assert aKMin <= aKMax;
        assert aMaxBudget >= MipGenerator.MIN_BUDGET;
        assert aDurationMillis > 0;
        
        this.prices = new ArrayList<Double>();
        for (double aPrice: aPrices) {
            this.prices.add(aPrice);
        }
        this.budgets = new ArrayList<Double>();
        for (Agent agent: aAgents) {
            this.budgets.add(agent.getBudget());
        }
        this.allocation = new ArrayList<List<Integer>>();
        for (List<Integer> row: aAllocation) {
            List<Integer> newRow = new ArrayList<Integer>();
            for (int item: row) {
                newRow.add(item);
            }
            this.allocation.add(newRow);
        }
        this.error = new ArrayList<Double>();
        for (double item: aError) {
            this.error.add(item);
        }
        this.errorSize = aErrorSize;
        this.kMin = aKMin;
        this.kMax = aKMax;
        this.maxBudget = aMaxBudget;
        this.agents = new ArrayList<Agent>();
        for (Agent agent: aAgents) {
            List<Double> values = new ArrayList<Double>();
            for (double value: agent.getValues()) {
                values.add(value);
            }
            Agent newAgent = 
                new Agent(values, agent.getBudget(), agent.getId());
            this.agents.add(newAgent);
        }
        this.durationMillis = aDurationMillis;
        if (aRsdOrder != null) {
            this.rsdOrder = new ArrayList<Integer>();
            for (Integer item: aRsdOrder) {
                this.rsdOrder.add(item);
            }
        } else {
            this.rsdOrder = null;
        }
        
        this.bestErrorValues = new ArrayList<Double>();
        for (Double bestErrorValue: aBestErrorValues) {
            this.bestErrorValues.add(bestErrorValue);
        }
        this.priceUpdateSources = new ArrayList<PriceUpdateSource>();
        for (PriceUpdateSource priceUpdateSource: aPriceUpdateSources) {
            this.priceUpdateSources.add(priceUpdateSource);
        }
    }

    public List<Double> getPrices() {
        return prices;
    }

    public List<Double> getBudgets() {
        return budgets;
    }

    public List<List<Integer>> getAllocation() {
        return allocation;
    }

    public List<Double> getError() {
        return error;
    }

    public double getErrorSize() {
        return errorSize;
    }

    public int getkMin() {
        return kMin;
    }

    public int getkMax() {
        return kMax;
    }
    
    public List<Integer> getTeamSizes() {
        return this.teamSizes;
    }

    public double getMaxBudget() {
        return maxBudget;
    }

    public List<Agent> getAgents() {
        return agents;
    }
    
    public long getDurationMillis() {
        return this.durationMillis;
    }
    
    public List<Integer> getRsdOrder() {
        return this.rsdOrder;
    }
    
    public List<Double> getBestErrorValues() {
        return this.bestErrorValues;
    }
    
    public List<PriceUpdateSource> getPriceUpdateSources() {
        return this.priceUpdateSources;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SearchResult \n[prices=");
        builder.append(prices);
        builder.append(", \nbudgets=");
        builder.append(budgets);
        builder.append(", \nallocation=");
        builder.append(allocation);
        builder.append(", \nerror=");
        builder.append(error);
        builder.append(", \nerrorSize=");
        builder.append(errorSize);
        builder.append(", \nkMin=");
        builder.append(kMin);
        builder.append(", \nkMax=");
        builder.append(kMax);
        builder.append(", \nteamSizes=");
        builder.append(teamSizes);        
        builder.append(", \nmaxBudget=");
        builder.append(maxBudget);
        builder.append(", \nagents=");
        builder.append(agents);
        builder.append(", \ndurationMillis=");
        builder.append(durationMillis);  
        builder.append(", \nrsdOrder=");
        builder.append(rsdOrder); 
        builder.append(", \nbestErrorValues=");
        builder.append(bestErrorValues); 
        builder.append(", \npriceUpdateSources=");
        builder.append(priceUpdateSources); 
        builder.append("]");
        return builder.toString();
    }
}
