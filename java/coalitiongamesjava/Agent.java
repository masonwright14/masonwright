package coalitiongames;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Agent {
    
    private final int id;

    /**
     * Includes only values for the other agents, not the self agent.
     * Thus, if there are N agents, there should be (N - 1) items.
     */
    private final List<Double> values;
    
    /**
     * Should be same length as values, and in same order. Used
     * to indicate which agent corresponds to which value.
     */
    private final List<UUID> agentIdsForValues;
    
    private final double budget;
    
    private final UUID uuid;
    
    public Agent(
        final List<Double> aValues,
        final List<UUID> aAgentIdsForValues,
        final double aBudget,
        final int aId,
        final UUID aUuid
    ) {
        this.values = new ArrayList<Double>();
        for (double aValue: aValues) {
            this.values.add(aValue);
        }
        this.agentIdsForValues = new ArrayList<UUID>();
        for (UUID currentId: aAgentIdsForValues) {
            this.agentIdsForValues.add(currentId);
        }
        
        this.budget = aBudget;
        this.id = aId;
        this.uuid = aUuid;
    }
    
    public List<Double> getValues() {
        return this.values;
    }
    
    public List<UUID> getAgentIdsForValues() {
        return this.agentIdsForValues;
    }
    
    public double getBudget() {
        return this.budget;
    }
    
    public int getId() {
        return this.id;
    }
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    /*
     * Use only the UUID to test for equality,
     * so that Agents with subsetted "values"
     * can be marked as equal.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime;
        if (uuid != null) {
            result += uuid.hashCode();
        }
        return result;
    }

    /*
     * Use only the UUID to test for equality,
     * so that Agents with subsetted "values"
     * can be marked as equal.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Agent other = (Agent) obj;
        if (uuid == null) {
            if (other.uuid != null) {
                return false;
            }
        } else if (!uuid.equals(other.uuid)) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Agent [id=");
        builder.append(id);
        builder.append(", \nvalues=");
        builder.append(values);
        builder.append(", \nbudget=");
        builder.append(budget);
        builder.append("]");
        return builder.toString();
    }
}
