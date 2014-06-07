package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public final class SearchResult {

    private final List<Double> prices;
    private final List<Double> budgets;
    private final List<List<Integer>> allocation;
    private final List<Double> error;
    private final double errorSize;
    private final int kMin;
    private final int kMax;
    private final double maxBudget;
    private final List<Agent> agents;
    
    public SearchResult(
        final List<Double> aPrices,
        final List<List<Integer>> aAllocation,
        final List<Double> aError,
        final double aErrorSize,
        final int aKMin,
        final int aKMax,
        final double aMaxBudget,
       final List<Agent> aAgents
    ) {
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

    public double getMaxBudget() {
        return maxBudget;
    }

    public List<Agent> getAgents() {
        return agents;
    }
    
    public String toStringWithoutAgents() {
        StringBuilder builder = new StringBuilder();
        builder.append("SearchResult [prices=");
        builder.append(prices);
        builder.append(", budgets=");
        builder.append(budgets);
        builder.append(", allocation=");
        builder.append(allocation);
        builder.append(", error=");
        builder.append(error);
        builder.append(", errorSize=");
        builder.append(errorSize);
        builder.append(", kMin=");
        builder.append(kMin);
        builder.append(", kMax=");
        builder.append(kMax);
        builder.append(", maxBudget=");
        builder.append(maxBudget);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SearchResult [prices=");
        builder.append(prices);
        builder.append(", budgets=");
        builder.append(budgets);
        builder.append(", allocation=");
        builder.append(allocation);
        builder.append(", error=");
        builder.append(error);
        builder.append(", errorSize=");
        builder.append(errorSize);
        builder.append(", kMin=");
        builder.append(kMin);
        builder.append(", kMax=");
        builder.append(kMax);
        builder.append(", maxBudget=");
        builder.append(maxBudget);
        builder.append(", agents=");
        builder.append(agents);
        builder.append("]");
        return builder.toString();
    }
}
