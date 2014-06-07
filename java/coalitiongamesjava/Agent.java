package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public final class Agent {
    
    private final int id;

    /**
     * Includes only values for the other agents, not the self agent.
     * Thus, if there are N agents, there should be (N - 1) items.
     */
    private final List<Double> values;
    
    private final double budget;
    
    public Agent(
        final List<Double> aValues,
        final double aBudget,
        final int aId
    ) {
        this.values = new ArrayList<Double>();
        for (double aValue: aValues) {
            this.values.add(aValue);
        }
        
        this.budget = aBudget;
        
        this.id = aId;
    }
    
    public List<Double> getValues() {
        return this.values;
    }
    
    public double getBudget() {
        return this.budget;
    }
    
    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Agent [id=");
        builder.append(id);
        builder.append(", values=");
        builder.append(values);
        builder.append(", budget=");
        builder.append(budget);
        builder.append("]");
        return builder.toString();
    }
}
