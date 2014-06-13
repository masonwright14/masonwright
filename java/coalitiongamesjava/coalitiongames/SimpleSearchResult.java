package coalitiongames;
import java.util.ArrayList;
import java.util.List;


public final class SimpleSearchResult {

    private final List<List<Integer>> allocation;
    private int kMin;
    private int kMax;
    private final List<Agent> agents;
    private final List<Integer> rsdOrder; // can be null

    public SimpleSearchResult(
        final List<List<Integer>> aAllocation,
        final int aKMin,
        final int aKMax,
        final List<Agent> aAgents,
        final List<Integer> aRsdOrder
    ) {
        this.allocation = new ArrayList<List<Integer>>();
        for (List<Integer> row: aAllocation) {
            List<Integer> newRow = new ArrayList<Integer>();
            for (int item: row) {
                newRow.add(item);
            }
            this.allocation.add(newRow);
        }
        this.kMax = aKMax;
        this.kMin = aKMin;
        this.agents = new ArrayList<Agent>();
        for (Agent agent: aAgents) {
            List<Double> values = new ArrayList<Double>();
            for (double value: agent.getValues()) {
                values.add(value);
            }
            Agent newAgent = 
                new Agent(
                    values, agent.getAgentIdsForValues(),  
                    agent.getBudget(), agent.getId(), agent.getUuid()
                );
            this.agents.add(newAgent);
        }
        if (aRsdOrder != null) {
            this.rsdOrder = new ArrayList<Integer>();
            for (Integer item: aRsdOrder) {
                this.rsdOrder.add(item);
            }
        } else {
            this.rsdOrder = null;
        }
    }

    public List<List<Integer>> getAllocation() {
        return allocation;
    }

    public int getkMin() {
        return kMin;
    }

    public int getkMax() {
        return kMax;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<Integer> getRsdOrder() {
        return rsdOrder;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleSearchResult [allocation=\n");
        builder.append(Util.demandAsMatrix(allocation));
        builder.append("\n, kMin=");
        builder.append(kMin);
        builder.append(", kMax=");
        builder.append(kMax);
        builder.append(", agents=");
        builder.append(agents);
        builder.append(", rsdOrder=");
        builder.append(rsdOrder);
        builder.append("]");
        return builder.toString();
    }
}
