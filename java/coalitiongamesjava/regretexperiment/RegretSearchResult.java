package regretexperiment;

public final class RegretSearchResult {

    /**
     * Greatest amount of regret found for any agent from
     * truthful reporting,
     * as a fraction of that agent's total utility for all
     * other agents under truthful reporting.
     */
    private final double maxRegretFromTruthFraction;
    
    /**
     * Greatest amount of regret found for any agent from
     * truthful reporting, rounded to the nearest integer,
     * then taken as a fraction of that agent's total utility for all
     * other agents under truthful reporting, where each agent's
     * utility is floored before adding to the total.
     */
    private final double maxRegretFromTruthFractionNoJitter;
    
    /**
     * Index in Agents list of the agent with most regret from truthful
     * reporting found.
     * 
     * -1 if no agent has positive regret.
     */
    private final int mostRegretAgentIndex;
    
    /**
     * Index in random serial order of the agent with most regret
     * found. 0 if the agent with most regret from truthful reporting
     * had the best random order.
     * 
     * -1 if no agent has positive regret.
     */
    private final int mostRegretAgentRsdIndex;
    
    /**
     * How many agents had positive regret of truthful reporting found.
     */
    private final int agentsWithRegretFromTruth;
    
    /**
     * How many agents had regret of truthful reporting > 0.5 found, indicating
     * it was not merely regret over the artificial "jitter" 
     * added to preference values.
     */
    private final int agentsWithRegretFromTruthNoJitter;
    
    /**
     * Mean over all agents, not just those with regret, of the maximum regret
     * found of truthful reporting as a fraction of that agent's total utility
     * for other agents (under truthful reporting). 
     * Note that regrets cannot be negative.
     */
    private final double meanRegretFromTruthFraction;

    /**
     * How many deviations from truthful reporting were tested for each agent.
     */
    private final int deviationsPerAgent;
    
    /**
     * How many agents are in the model.
     */
    private final int numberAgents;
    
    /**
     * Duration of the run in milliseconds.
     */
    private final long durationMillis;
    
    private final int numberOfTeams;

    public RegretSearchResult(
        final double aMaxRegretFromTruthFraction,
        final double aMaxRegretFromTruthFractionNoJitter,
        final int aMostRegretAgentIndex, 
        final int aMostRegretAgentRsdIndex,
        final int aAgentsWithRegretFromTruth,
        final int aAgentsWithRegretFromTruthNoJitter,
        final double aMeanRegretFromTruthFraction, 
        final int aDeviationsPerAgent,
        final int aNumberAgents, 
        final long aDurationMillis,
        final int aNumberOfTeams
    ) {
        this.maxRegretFromTruthFraction = aMaxRegretFromTruthFraction;
        this.maxRegretFromTruthFractionNoJitter = 
            aMaxRegretFromTruthFractionNoJitter;
        this.mostRegretAgentIndex = aMostRegretAgentIndex;
        this.mostRegretAgentRsdIndex = aMostRegretAgentRsdIndex;
        this.agentsWithRegretFromTruth = aAgentsWithRegretFromTruth;
        this.agentsWithRegretFromTruthNoJitter = 
            aAgentsWithRegretFromTruthNoJitter;
        this.meanRegretFromTruthFraction = aMeanRegretFromTruthFraction;
        this.deviationsPerAgent = aDeviationsPerAgent;
        this.numberAgents = aNumberAgents;
        this.durationMillis = aDurationMillis;
        this.numberOfTeams = aNumberOfTeams;
    }
    
    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public double getMaxRegretFromTruthFraction() {
        return maxRegretFromTruthFraction;
    }
    
    public double getMaxRegretFromTruthFractionNoJitter() {
        return maxRegretFromTruthFractionNoJitter;
    }

    public int getMostRegretAgentIndex() {
        return mostRegretAgentIndex;
    }

    public int getMostRegretAgentRsdIndex() {
        return mostRegretAgentRsdIndex;
    }

    public int getAgentsWithRegretFromTruth() {
        return agentsWithRegretFromTruth;
    }

    public int getAgentsWithRegretFromTruthNoJitter() {
        return agentsWithRegretFromTruthNoJitter;
    }

    public double getMeanRegretFromTruthFraction() {
        return meanRegretFromTruthFraction;
    }

    public int getDeviationsPerAgent() {
        return deviationsPerAgent;
    }

    public int getNumberAgents() {
        return numberAgents;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegretSearchResult [maxRegretFromTruthFraction=");
        builder.append(maxRegretFromTruthFraction);
        builder.append(", maxRegretFromTruthFractionNoJitter=");
        builder.append(maxRegretFromTruthFractionNoJitter);
        builder.append(", mostRegretAgentIndex=");
        builder.append(mostRegretAgentIndex);
        builder.append(", mostRegretAgentRsdIndex=");
        builder.append(mostRegretAgentRsdIndex);
        builder.append(", agentsWithRegretFromTruth=");
        builder.append(agentsWithRegretFromTruth);
        builder.append(", agentsWithRegretFromTruthNoJitter=");
        builder.append(agentsWithRegretFromTruthNoJitter);
        builder.append(", meanRegretFromTruthFraction=");
        builder.append(meanRegretFromTruthFraction);
        builder.append(", deviationsPerAgent=");
        builder.append(deviationsPerAgent);
        builder.append(", numberAgents=");
        builder.append(numberAgents);
        builder.append(", durationMillis=");
        builder.append(durationMillis);
        builder.append("]");
        return builder.toString();
    } 
}
