package coalitiongames;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SimpleSearchResult {

    private final List<List<Integer>> allocation;
    private int kMin;
    private int kMax;
    private final List<Agent> agents;
    
    /*
     * rsdOrder: first item is the index of the first agent to go.
     * second item is the index of second agent to go, etc.
     * example:
     * 1 2 0 -> agent 1 goes, then agent 2, then agent 0.
     */
    private final List<Integer> rsdOrder; // can be null
    private final long durationMillis;
    private final List<Integer> captainIndexes;
    
    private final double meanPairwiseCosineSimilarity;
    
    public SimpleSearchResult(
        final List<List<Integer>> aAllocation,
        final int aKMin,
        final int aKMax,
        final List<Agent> aAgents,
        final List<Integer> aRsdOrder,
        final long aDurationMillis,
        final List<Integer> aCaptainIndexes,
        final double aMeanPairwiseCosineSimilarity
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
        
        this.durationMillis = aDurationMillis;
        this.captainIndexes = new ArrayList<Integer>();
        if (aCaptainIndexes != null) {
            this.captainIndexes.addAll(aCaptainIndexes);
        }
        this.meanPairwiseCosineSimilarity = aMeanPairwiseCosineSimilarity;
    }

    public final List<List<Integer>> getAllocation() {
        return allocation;
    }

    public final int getkMin() {
        return kMin;
    }

    public final int getkMax() {
        return kMax;
    }
    
    public final double getMeanPairwiseCosineSimilarity() {
        return this.meanPairwiseCosineSimilarity;
    }

    public final List<Agent> getAgents() {
        return agents;
    }
    
    public final int getNumberOfTeams() {
        int total = 0;
        // keep track of which agents you've already seen on
        // a team. assume each agent is on exactly 1 team.
        final List<Boolean> found = new ArrayList<Boolean>();
        for (int i = 0; i < this.agents.size(); i++) {
            found.add(false);
        }
        for (final List<Integer> team: this.allocation) {
            int firstOneIndex = team.indexOf(1);
            if (!found.get(firstOneIndex)) {
                // this team has not been seen yet.
                total++;
                for (int i = 0; i < team.size(); i++) {
                    if (team.get(i) == 1) {
                        found.set(i, true);
                    }
                }
            }
        }
        
        return total;
    }

    public final List<Integer> getRsdOrder() {
        return rsdOrder;
    }
    
    /**
     * @return a list of the turn number (0-based) of each agent.
     * for example, item 0 is the turn number of the first agent in
     * the agents list, item 1 is the turn number of the next agent
     * in the agents list, etc.
     */
    public final List<Integer> getRsdIndexes() {
        if (this.rsdOrder == null) {
            return null;
        }
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < agents.size(); i++) {
            result.add(this.rsdOrder.indexOf(i));
        }
        return result;
    }
    
    public final long getDurationMillis() {
        return this.durationMillis;
    }
    
    public final List<Integer> getTeamSizesWithSelf() {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < agents.size(); i++) {
            final List<Integer> team = this.allocation.get(i);
            int total = 0;
            for (Integer item: team) {
                total += item;
            }
            result.add(total);
        }
        
        return result;
    }
    
    public final List<Double> getFractionsOfTotalUtility() {
        final List<Double> result = new ArrayList<Double>();
        final List<Double> teamUtilities = getTeamUtilities();
        final List<Double> totalUtilities = getTotalUtilities();
        for (int i = 0; i < agents.size(); i++) {
            if (totalUtilities.get(i) <= 0) {
                result.add(0.0);
            } else {
                result.add(teamUtilities.get(i) / totalUtilities.get(i));
            }
        }
        return result;
    }
    
    public final List<Double> getFractionsOfTotalUtilityNoJitter() {
        final List<Double> result = new ArrayList<Double>();
        final List<Integer> teamUtilities = getTeamUtilitiesNoJitter();
        final List<Integer> totalUtilities = getTotalUtilitiesNoJitter();
        for (int i = 0; i < agents.size(); i++) {
            if (totalUtilities.get(i) <= 0) {
                result.add(0.0);
            } else {
                result.add(
                    ((double) teamUtilities.get(i)) / totalUtilities.get(i)
                );
            }
        }
        return result;
    }
    
    public final List<Double> getMeanTeammateUtilities() {
        final List<Double> result = new ArrayList<Double>();
        final List<Double> totalTeamUtilities = getTeamUtilities();
        final List<Integer> teamSizes = getTeamSizesWithSelf();
        for (int i = 0; i < this.agents.size(); i++) {
            if (teamSizes.get(i) == 1) {
                result.add(-1.0);
            } else {
                result.add(totalTeamUtilities.get(i) / (teamSizes.get(i) - 1));
            }
        }
        return result;
    }
    
    public final List<Double> getMeanTeammateUtilitiesNoJitter() {
        final List<Double> result = new ArrayList<Double>();
        final List<Integer> totalTeamUtilities = getTeamUtilitiesNoJitter();
        final List<Integer> teamSizes = getTeamSizesWithSelf();
        for (int i = 0; i < this.agents.size(); i++) {
            if (teamSizes.get(i) == 1) {
                result.add(-1.0);
            } else {
                result.add(
                    totalTeamUtilities.get(i) / (teamSizes.get(i) - 1.0)
                );
            }
        }
        return result;
    }
    
    public final List<Double> getTeamUtilities() {
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i);
            double total = 0.0;
            for (int j = 0; j < agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgentUuid = this.agents.get(j).getUuid();
                    total += agent.getValueByUUID(otherAgentUuid);
                }
            }
            
            result.add(total);
        }
        
        return result;
    }
    
    public final List<Integer> getTeamUtilitiesNoJitter() {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i);
            int total = 0;
            for (int j = 0; j < agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgentUuid = this.agents.get(j).getUuid();
                    total += 
                        (int) Math.floor(agent.getValueByUUID(otherAgentUuid));
                }
            }
            
            result.add(total);
        }
        
        return result;
    }
    
    public final List<Integer> sumsOfReversedRanks() {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < this.agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i); 
            
            final List<Double> reverseSortedValues = 
                new ArrayList<Double>(agent.getValues());
            // keep sortedValues in reverse order.
            Collections.sort(reverseSortedValues);
            
            int sumOfReversedRanks = 0;
            for (int j = 0; j < this.agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgenUuid = this.agents.get(j).getUuid();
                    final double otherAgentValue = 
                        agent.getValueByUUID(otherAgenUuid);
                    // ranks should be 1-based, not 0-based, so add 1.
                    final int otherAgentRank = 
                        1 + reverseSortedValues.indexOf(otherAgentValue);
                    sumOfReversedRanks += otherAgentRank;
                }
            }
            
            result.add(sumOfReversedRanks);
        }
        
        return result;
    }
    
    public final List<Double> sumsOfReversedRanksNoJitter() {
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < this.agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i); 
            
            final List<Integer> reverseSortedValues = new ArrayList<Integer>();
            for (final Double item: agent.getValues()) {
                reverseSortedValues.add((int) Math.floor(item));
            }
            // keep sortedValues in reverse order (low to high)
            Collections.sort(reverseSortedValues);
            
            double sumOfReversedRanks = 0.0;
            for (int j = 0; j < this.agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgenUuid = this.agents.get(j).getUuid();
                    final int otherAgentValueFloor = 
                        (int) Math.floor(agent.getValueByUUID(otherAgenUuid));
                    // ranks should be 1-based, not 0-based, so add 1.
                    final int otherAgentRankFirst = 
                        1 + reverseSortedValues.indexOf(otherAgentValueFloor);
                    final int otherAgentRankLast = 
                        1 + reverseSortedValues.
                            lastIndexOf(otherAgentValueFloor);
                    sumOfReversedRanks += 
                        (otherAgentRankFirst + otherAgentRankLast) / 2.0;
                }
            }
            
            result.add(sumOfReversedRanks);
        }
        
        return result;
    }
    
    public final List<Double> meanTeammateRanks() {
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < this.agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i);
            
            final List<Double> sortedValues = 
                new ArrayList<Double>(agent.getValues());
            Collections.sort(sortedValues);
            Collections.reverse(sortedValues);
            
            int totalRank = 0;
            int teamSize = 0;
            for (int j = 0; j < this.agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgenUuid = this.agents.get(j).getUuid();
                    final double otherAgentValue = 
                        agent.getValueByUUID(otherAgenUuid);
                    // ranks should be 1-based, not 0-based, so add 1.
                    final int otherAgentRank = 
                        1 + sortedValues.indexOf(otherAgentValue);
                    totalRank += otherAgentRank;
                    teamSize++;
                }
            }
            
            if (teamSize == 0) {
                result.add(-1.0);
            } else {
                result.add(((double) totalRank) / teamSize);
            }   
        }
        
        return result;
    }
    
    /*
     * For each agent, report the mean rank for that agent
     * for all the (N - 1) other agents.
     */
    public final List<Double> getMeanAgentRanksNoJitter() {
        final List<Double> result = new ArrayList<Double>();
        final int countOtherAgents = agents.size() - 1;
        for (final Agent currentAgent: agents) {
            double currentAgentTotal = 0.0;
            for (final Agent otherAgent: agents) {
                if (!currentAgent.equals(otherAgent)) {
                    final double rankToOtherAgent = 
                        getRankOfAgentNoJitter(
                            otherAgent, // ranking agent
                            currentAgent // ranked agent
                        );
                    currentAgentTotal += rankToOtherAgent;
                }
            }
            final double currentAgentMean = 
                currentAgentTotal / countOtherAgents;
            result.add(currentAgentMean);
        }
        
        assert result.size() == agents.size();
        return result;
    }
    
    private static double getRankOfAgentNoJitter(
        final Agent rankingAgent, 
        final Agent rankedAgent
    ) {
        assert !rankingAgent.equals(rankedAgent);
        final UUID rankedAgentId = rankedAgent.getUuid();
        
        final List<Integer> sortedValues = new ArrayList<Integer>();
        for (final Double item: rankingAgent.getValues()) {
            sortedValues.add((int) Math.floor(item));
        }
        Collections.sort(sortedValues);
        Collections.reverse(sortedValues);
        
        final int rankedAgentValueFloor = 
            (int) Math.floor(rankingAgent.getValueByUUID(rankedAgentId));
        // ranks should be 1-based, not 0-based, so add 1.
        final int rankedAgentFirstRank = 
            1 + sortedValues.indexOf(rankedAgentValueFloor);
        final int rankedAgentLastRank = 
            1 + sortedValues.lastIndexOf(rankedAgentValueFloor);
        // set rank of item to be mean of 
        // first occurrence rank and last occurrence rank,
        // because ties are possible.    
        return (rankedAgentFirstRank + rankedAgentLastRank) / 2.0;
    }
    
    public final List<Double> meanTeammateRanksNoJitter() {
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < this.agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i);
            
            final List<Integer> sortedValues = new ArrayList<Integer>();
            for (final Double item: agent.getValues()) {
                sortedValues.add((int) Math.floor(item));
            }
            Collections.sort(sortedValues);
            Collections.reverse(sortedValues);
            
            double totalRank = 0.0;
            int teamSize = 0;
            for (int j = 0; j < this.agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgentUuid = this.agents.get(j).getUuid();
                    final double otherAgentValue = 
                        agent.getValueByUUID(otherAgentUuid);
                    final int otherAgentValueFloor = 
                        (int) Math.floor(otherAgentValue);
                    // ranks should be 1-based, not 0-based, so add 1.
                    final int otherAgentFirstRank = 
                        1 + sortedValues.indexOf(otherAgentValueFloor);
                    final int otherAgentLastRank = 
                        1 + sortedValues.lastIndexOf(otherAgentValueFloor);
                    // set rank of item to be mean of 
                    // first occurrence rank and last occurrence rank,
                    // because ties are possible.
                    totalRank += 
                        (otherAgentFirstRank + otherAgentLastRank) / 2.0;
                    teamSize++;
                }
            }
            
            if (teamSize == 0) {
                result.add(-1.0);
            } else {
                result.add(totalRank / teamSize);
            }   
        }
        
        return result;
    }
    
    public final List<Integer> favTeammateRanks() {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < this.agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i);
            
            final List<Double> sortedValues = 
                new ArrayList<Double>(agent.getValues());
            Collections.sort(sortedValues);
            Collections.reverse(sortedValues);
            
            int bestRank = Integer.MAX_VALUE;
            for (int j = 0; j < this.agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgenUuid = this.agents.get(j).getUuid();
                    final double otherAgentValue = 
                        agent.getValueByUUID(otherAgenUuid);
                    // ranks should be 1-based, not 0-based, so add 1.
                    final int otherAgentRank = 
                        1 + sortedValues.indexOf(otherAgentValue);
                    if (otherAgentRank < bestRank) {
                        bestRank = otherAgentRank;
                    }
                }
            }
            
            if (bestRank == Integer.MAX_VALUE) {
                result.add(-1);
            } else {
                result.add(bestRank);
            }
        }
        return result;
    }
    
    public final List<Double> favTeammateRanksNoJitter() {
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < this.agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i);
            
            final List<Integer> sortedValues = new ArrayList<Integer>();
            for (final Double item: agent.getValues()) {
                sortedValues.add((int) Math.floor(item));
            }
            Collections.sort(sortedValues);
            Collections.reverse(sortedValues);
            
            double bestRank = Double.MAX_VALUE;
            for (int j = 0; j < this.agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgenUuid = this.agents.get(j).getUuid();
                    final double otherAgentValue = 
                        agent.getValueByUUID(otherAgenUuid);
                    final int otherAgentValueFloor = 
                        (int) Math.floor(otherAgentValue);
                    // ranks should be 1-based, not 0-based, so add 1.
                    final int otherAgentFirstRank = 
                        1 + sortedValues.indexOf(otherAgentValueFloor);
                    final int otherAgentLastRank = 
                        1 + sortedValues.lastIndexOf(otherAgentValueFloor);
                    final double otherAgentRank = 
                        (otherAgentFirstRank + otherAgentLastRank) / 2.0;
                    if (otherAgentRank < bestRank) {
                        bestRank = otherAgentRank;
                    }
                }
            }
            
            // if greater than max value / 2, assume it's uninitialized.
            // do instead of testing for equality to MAX_VALUE
            if (bestRank > Double.MAX_VALUE / 2.0) {
                result.add(-1.0);
            } else {
                result.add(bestRank);
            }
        }
        return result;
    }
    
    public final List<Integer> leastFavTeammateRanks() {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < this.agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i);
            
            final List<Double> sortedValues = 
                new ArrayList<Double>(agent.getValues());
            Collections.sort(sortedValues);
            Collections.reverse(sortedValues);
            
            int worstRank = 0;
            for (int j = 0; j < this.agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgenUuid = this.agents.get(j).getUuid();
                    final double otherAgentValue = 
                        agent.getValueByUUID(otherAgenUuid);
                    // ranks should be 1-based, not 0-based, so add 1.
                    final int otherAgentRank = 
                        1 + sortedValues.indexOf(otherAgentValue);
                    if (otherAgentRank > worstRank) {
                        worstRank = otherAgentRank;
                    }
                }
            }
            
            if (worstRank == 0) {
                result.add(-1);
            } else {
                result.add(worstRank);
            }
        }
        return result;
    }
    
    public final List<Double> leastFavTeammateRanksNoJitter() {
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < this.agents.size(); i++) {
            final Agent agent = this.agents.get(i);
            final List<Integer> team = this.allocation.get(i);
            
            final List<Integer> sortedValues = new ArrayList<Integer>();
            for (final Double item: agent.getValues()) {
                sortedValues.add((int) Math.floor(item));
            }
            Collections.sort(sortedValues);
            Collections.reverse(sortedValues);
            
            double worstRank = 0.0;
            for (int j = 0; j < this.agents.size(); j++) {
                if (i != j && team.get(j) == 1) {
                    final UUID otherAgenUuid = this.agents.get(j).getUuid();
                    final double otherAgentValue = 
                        agent.getValueByUUID(otherAgenUuid);
                    final int otherAgentValueFloor = 
                        (int) Math.floor(otherAgentValue);
                    // ranks should be 1-based, not 0-based, so add 1.
                    final int otherAgentFirstRank = 
                        1 + sortedValues.indexOf(otherAgentValueFloor);
                    final int otherAgentLastRank = 
                        1 + sortedValues.lastIndexOf(otherAgentValueFloor);
                    final double otherAgentRank = 
                        (otherAgentFirstRank + otherAgentLastRank) / 2.0;
                    if (otherAgentRank > worstRank) {
                        worstRank = otherAgentRank;
                    }
                }
            }
            
            final double testLessThanOne = 0.5;
            if (worstRank < testLessThanOne) {
                result.add(-1.0);
            } else {
                result.add(worstRank);
            }
        }
        return result;
    }
    
    public final List<Integer> isCaptain() {
        final List<Integer> result = new ArrayList<Integer>();
        if (captainIndexes.isEmpty()) {
            for (int i = 0; i < agents.size(); i++) {
                result.add(-1);
            }
        } else {
            for (int i = 0; i < agents.size(); i++) {
                if (captainIndexes.contains(i)) {
                    result.add(1);
                } else {
                    result.add(0);
                }
            }
        }

        return result;
    }
    
    public final List<Double> getEnvyAmounts() {
        final List<Double> result = new ArrayList<Double>();
        final List<Double> teamUtilities = getTeamUtilities();
        for (int i = 0; i < getAgents().size(); i++) {
            final double bestOtherTeamUtility = 
                getBestOtherTeamUtility(i);
            final double envy = bestOtherTeamUtility - teamUtilities.get(i);
            if (envy < 0) {
                result.add(0.0);
            } else {
                result.add(envy);
            }
        }
        
        return result;
    }
    
    public final List<Integer> getEnvyAmountsNoJitter() {
        final List<Integer> result = new ArrayList<Integer>();
        final List<Integer> teamUtilitiesNoJitter = getTeamUtilitiesNoJitter();
        for (int i = 0; i < getAgents().size(); i++) {
            final int bestOtherTeamUtilityNoJitter =
                getBestOtherTeamUtilityNoJitter(i);
            final int envy = 
                bestOtherTeamUtilityNoJitter - teamUtilitiesNoJitter.get(i);
            if (envy < 0) {
                result.add(0);
            } else {
                result.add(envy);
            }
        }
        
        return result;
    }
    
    public final List<Double> getEnvyAmountsMinusSingleGood() {
        final List<Double> result = new ArrayList<Double>();
        final List<Double> teamUtilities = getTeamUtilities();
        for (int i = 0; i < getAgents().size(); i++) {
            final double bestOtherTeamUtilityMinusSingleGood = 
                getBestOtherTeamUtilityMinusSingleGood(i);
            final double envy = bestOtherTeamUtilityMinusSingleGood 
                - teamUtilities.get(i);
            if (envy < 0) {
                result.add(0.0);
            } else {
                result.add(envy);
            }
        }
        
        return result;        
    }
    
    public final List<Integer> getEnvyAmountsMinusSingleGoodNoJitter() {
        final List<Integer> result = new ArrayList<Integer>();
        final List<Integer> teamUtilitiesNoJitter = getTeamUtilitiesNoJitter();
        for (int i = 0; i < getAgents().size(); i++) {
            final int bestOtherTeamUtilityMinusSingleGoodNoJitter =
                    getBestOtherTeamUtilityMinusSingleGoodNoJitter(i);
            final int envy = 
                    bestOtherTeamUtilityMinusSingleGoodNoJitter 
                    - teamUtilitiesNoJitter.get(i);
            if (envy < 0) {
                result.add(0);
            } else {
                result.add(envy);
            }
        }
        
        return result;        
    }
    
    /*
     * For each agent, report the mean utility for that agent
     * from all the (N - 1) other agents.
     */
    public final List<Double> getMeanAgentUtilitiesNoJitter() {
        final List<Double> result = new ArrayList<Double>();
        final int countOtherAgents = agents.size() - 1;
        for (final Agent currentAgent: agents) {
            double currentAgentTotal = 0.0;
            final UUID currentAgentId = currentAgent.getUuid();
            // get value of this agent to all other agents
            for (final Agent otherAgent: agents) {
                if (!currentAgent.equals(otherAgent)) {
                    currentAgentTotal += 
                        Math.floor(otherAgent.getValueByUUID(currentAgentId));
                }
            }
            final double currentAgentMean = 
                currentAgentTotal / countOtherAgents;
            result.add(currentAgentMean);
        }
        
        assert result.size() == agents.size();
        return result;
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
        builder.append(", \ndurationMillis=");
        builder.append(durationMillis);
        builder.append(", \nrsdIndexes=");
        builder.append(getRsdIndexes());
        builder.append(", \nisCaptain=");
        builder.append(isCaptain());
        builder.append("]");
        return builder.toString();
    }
    
    /**
     * @param agentIndex index in this.agents of the Agent to
     * consider
     * @return the sum of values, based on the Agent's values
     * parameter, of the agents included on the team of
     * highest value for this agent from this.allocation,
     * excluding the Agent's own team; but any team in this.allocation
     * of size kMax has its lowest-utility member excluded from the
     * total, based on the idea that the Agent cannot demand a
     * team of size (kMax + 1).
     */
    private double getBestOtherTeamUtility(final int agentIndex) {
        final Agent agent = getAgents().get(agentIndex);
        double maxTeamUtility = 0.0;
        // examine every team
        for (final List<Integer> team: getAllocation()) {
            // only consider teams that don't include this Agent.
            if (team.get(agentIndex) == 0) {
                
                double currentTeamUtility = 0.0;
                // keep track of the utility of the least valued
                // agent on the other team, in case the team's
                // size is kMax and this agent must be excluded.
                double lowestAgentUtility = Double.MAX_VALUE;
                for (int i = 0; i < team.size(); i++) {
                    // only add value for agents on the team.
                    if (team.get(i) == 1) {
                        final UUID agentId = getAgents().get(i).getUuid();
                        final double currentAgentUtility = 
                            agent.getValueByUUID(agentId);
                        currentTeamUtility += currentAgentUtility;
                        if (currentAgentUtility < lowestAgentUtility) {
                            lowestAgentUtility = currentAgentUtility;
                        }
                    }
                }
                
                final int teamSize = getTeamSize(team);
                // if the team size is kMax and the team has some agent,
                // then take back the value of the least valued agent.
                if (teamSize == getkMax()) {
                    currentTeamUtility -= lowestAgentUtility;
                } 
                
                // keep track of the best team's utility.
                if (currentTeamUtility > maxTeamUtility) {
                    maxTeamUtility = currentTeamUtility;
                }
            }
        }
        
        return maxTeamUtility;
    }
    
    private int getBestOtherTeamUtilityNoJitter(final int agentIndex) {
        final Agent agent = getAgents().get(agentIndex);
        int maxTeamUtility = 0;
        // examine every team
        for (final List<Integer> team: getAllocation()) {
            // only consider teams that don't include this Agent.
            if (team.get(agentIndex) == 0) {
                
                int currentTeamUtility = 0;
                // keep track of the utility of the least valued
                // agent on the other team, in case the team's
                // size is kMax and this agent must be excluded.
                int lowestAgentUtility = Integer.MAX_VALUE;
                for (int i = 0; i < team.size(); i++) {
                    // only add value for agents on the team.
                    if (team.get(i) == 1) {
                        final UUID agentId = getAgents().get(i).getUuid();
                        final int currentAgentUtility = (int) Math.floor(
                            agent.getValueByUUID(agentId));
                        currentTeamUtility += currentAgentUtility;
                        if (currentAgentUtility < lowestAgentUtility) {
                            lowestAgentUtility = currentAgentUtility;
                        }
                    }
                }
                
                final int teamSize = getTeamSize(team);
                // if the team size is kMax,
                // then take back the value of the least valued agent.
                if (teamSize == getkMax()) {
                    currentTeamUtility -= lowestAgentUtility;
                } 
                
                // keep track of the best team's utility.
                if (currentTeamUtility > maxTeamUtility) {
                    maxTeamUtility = currentTeamUtility;
                }
            }
        }
        
        return maxTeamUtility;        
    }
    
    private double getBestOtherTeamUtilityMinusSingleGood(
        final int agentIndex
    ) {
        final Agent agent = getAgents().get(agentIndex);
        double maxTeamUtility = 0.0;
        // examine every team
        for (final List<Integer> team: getAllocation()) {
            // only consider teams that don't include this Agent.
            if (team.get(agentIndex) == 0) {
                
                double currentTeamUtility = 0.0;
                // keep track of the utility of the least valued
                // agent on the other team, in case the team's
                // size is kMax and this agent must be excluded.
                double lowestAgentUtility = Double.MAX_VALUE;
                // keep track of highest agent utility, because
                // this will be taken away from the team's total
                // utility. we assume the team has at least 2 agents
                // and all utilities are distinct, so it won't be
                // the same as the lowestTeamUtility.
                double highestAgentUtility = 0.0;
                for (int i = 0; i < team.size(); i++) {
                    // only add value for agents on the team.
                    if (team.get(i) == 1) {
                        final UUID agentId = getAgents().get(i).getUuid();
                        final double currentAgentUtility = 
                            agent.getValueByUUID(agentId);
                        currentTeamUtility += currentAgentUtility;
                        if (currentAgentUtility < lowestAgentUtility) {
                            lowestAgentUtility = currentAgentUtility;
                        }
                        if (currentAgentUtility > highestAgentUtility) {
                            highestAgentUtility = currentAgentUtility;
                        }
                    }
                }
                
                final int teamSize = getTeamSize(team);
                // if the team size is kMax,
                // then take back the value of the least valued agent.
                if (teamSize == getkMax()) {
                    currentTeamUtility -= lowestAgentUtility;
                }
                
                currentTeamUtility -= highestAgentUtility;
                
                // keep track of the best team's utility.
                if (currentTeamUtility > maxTeamUtility) {
                    maxTeamUtility = currentTeamUtility;
                }
            }
        }
        
        return maxTeamUtility;
    }
    
    private int getBestOtherTeamUtilityMinusSingleGoodNoJitter(
        final int agentIndex
    ) {
        final Agent agent = getAgents().get(agentIndex);
        int maxTeamUtility = 0;
        // examine every team
        for (final List<Integer> team: getAllocation()) {
            // only consider teams that don't include this Agent.
            if (team.get(agentIndex) == 0) {
                
                final List<Integer> values = new ArrayList<Integer>();
                for (int i = 0; i < team.size(); i++) {
                    // only add value for agents on the team.
                    if (team.get(i) == 1) {
                        final UUID agentId = getAgents().get(i).getUuid();
                        final int currentAgentUtility = (int) Math.floor(
                            agent.getValueByUUID(agentId));
                        values.add(currentAgentUtility);
                    }
                }
                
                // sorted from low to high.
                Collections.sort(values);
                
                final int teamSize = getTeamSize(team);
                // if the team size is kMax and the team has some agent,
                // then take back the value of the least valued agent.
                if (teamSize == getkMax()) {
                    values.remove(0);
                } 
                
                // remove most valued agent
                values.remove(values.size() - 1);
                
                int total = 0;
                for (Integer value: values) {
                    total += value;
                }
                
                if (total > maxTeamUtility) {
                    maxTeamUtility = total;
                }
            }
        }
        
        return maxTeamUtility;        
    }
    
    private int getTeamSize(final List<Integer> team) {
        int result = 0;
        for (final Integer item: team) {
            result += item;
        }
        return result;
    }
    
    private List<Double> getTotalUtilities() {
        final List<Double> result = new ArrayList<Double>();
        for (Agent agent: this.agents) {
            double total = 0.0;
            for (final Double value: agent.getValues()) {
                total += value;
            }
            result.add(total);
        }
        
        return result;
    }
    
    private List<Integer> getTotalUtilitiesNoJitter() {
        final List<Integer> result = new ArrayList<Integer>();
        for (Agent agent: this.agents) {
            int total = 0;
            for (final Double value: agent.getValues()) {
                total += (int) Math.floor(value);
            }
            result.add(total);
        }
        
        return result;
    }
    
    /*****************************************************************
     * TESTING
     */
    
    public static void main(final String[] args) {
        testNumberOfTeamsAndTeamSizes();
        testRsdIndexes();
        testFractionsOfTotalUtility();
        testTeammateRanks();
        testIsCaptain();
    }
    
    private static void testNumberOfTeamsAndTeamSizes() {
        final int n = 5;
        List<Agent> agents = new ArrayList<Agent>();
        List<Double> values = new ArrayList<Double>();
        List<UUID> uuids = new ArrayList<UUID>();
        for (int i = 0; i < n; i++) {
            Agent agent = new Agent(values, uuids, 0, 0, null);
            agents.add(agent);
        }
        
        List<List<Integer>> allocation = new ArrayList<List<Integer>>();
        Integer[] arr1 = {1, 0, 0, 0, 0};
        List<Integer> t1 = Arrays.asList(arr1);
        Integer[] arr2 = {0, 1, 1, 0, 0};
        List<Integer> t2 = Arrays.asList(arr2);
        List<Integer> t3 = Arrays.asList(arr2);
        Integer[] arr4 = {0, 0, 0, 1, 1};
        List<Integer> t4 = Arrays.asList(arr4);
        List<Integer> t5 = Arrays.asList(arr4);
        allocation.add(t1);
        allocation.add(t2);
        allocation.add(t3);
        allocation.add(t4);
        allocation.add(t5);
        
        SimpleSearchResult ssr = new SimpleSearchResult(
            allocation, 
            1, 
            n, 
            agents, 
            null, 
            0,
            null,
            0.0
        );
        // should be 3
        System.out.println(ssr.getNumberOfTeams());
        
        // should be 1, 2, 2, 2, 2
        System.out.println(ssr.getTeamSizesWithSelf());
    }
    
    private static void testRsdIndexes() {
        final int n = 5;
        List<Agent> agents = new ArrayList<Agent>();
        List<Double> values = new ArrayList<Double>();
        List<UUID> uuids = new ArrayList<UUID>();
        for (int i = 0; i < n; i++) {
            Agent agent = new Agent(values, uuids, 0, 0, null);
            agents.add(agent);
        }
        
        List<List<Integer>> allocation = new ArrayList<List<Integer>>();
        final Integer[] rsdOrderArr = {3, 1, 0, 2, 4};
        List<Integer> rsdOrder = Arrays.asList(rsdOrderArr);
        
        SimpleSearchResult ssr = new SimpleSearchResult(
            allocation, 
            1, 
            n, 
            agents, 
            rsdOrder, 
            0,
            null,
            0.0
        );
        // should be: 2, 1, 3, 0, 4
        System.out.println(ssr.getRsdIndexes());
    }
    
    private static void testFractionsOfTotalUtility() {
        final int n = 6;
        List<Agent> agents = new ArrayList<Agent>();
        
        final Double[] valuesArr = {0.5, 1.5, 2.5, 3.5, 4.5, 5.5};
        List<Double> values = Arrays.asList(valuesArr);
        List<UUID> uuids = new ArrayList<UUID>();
        
        for (int i = 0; i < n; i++) {
            uuids.add(UUID.randomUUID());
        }
        
        for (int i = 0; i < n; i++) {
            List<Double> myValues = new ArrayList<Double>(values);
            myValues.remove(i);
            List<UUID> myUuids = new ArrayList<UUID>(uuids);
            myUuids.remove(i);
            Agent agent = new Agent(myValues, myUuids, 0, 0, uuids.get(i));
            agents.add(agent);
        }
        
        List<List<Integer>> allocation = new ArrayList<List<Integer>>();
        Integer[] arr1 = {1, 0, 0, 0, 0, 0};
        List<Integer> t1 = Arrays.asList(arr1);
        Integer[] arr2 = {0, 1, 1, 0, 0, 0};
        List<Integer> t2 = Arrays.asList(arr2);
        List<Integer> t3 = Arrays.asList(arr2);
        Integer[] arr4 = {0, 0, 0, 1, 1, 1};
        List<Integer> t4 = Arrays.asList(arr4);
        List<Integer> t5 = Arrays.asList(arr4);
        List<Integer> t6 = Arrays.asList(arr4);
        allocation.add(t1);
        allocation.add(t2);
        allocation.add(t3);
        allocation.add(t4);
        allocation.add(t5);
        allocation.add(t6);
        
        SimpleSearchResult ssr = new SimpleSearchResult(
            allocation, 
            1, 
            n, 
            agents, 
            null, 
            0,
            null,
            0.0
        );
        // original utilities: 0.5 1.5 2.5 3.5 4.5 5.5
        // original utilities no jitter: 0 1 2 3 4 5
        // should be 0 2.5 1.5 10.0 9.0 8.0
        System.out.println("team utilities: " + ssr.getTeamUtilities());
        // should be 0 2 1 9 8 7
        System.out.println(
            "team utilities no jitter: " + ssr.getTeamUtilitiesNoJitter()
        );
        // should be 17.5 16.5 15.5 14.5 13.5 12.5
        System.out.println("total utilities: " + ssr.getTotalUtilities());
        // should be 15 14 13 12 11 10
        System.out.println(
            "total utilities no jitter: " + ssr.getTotalUtilitiesNoJitter()
        );
        // should be 0/17.5 2.5/16.5 1.5/15.5 10.0/14.5 9.0/13.5 8.0/12.5
        System.out.println(
            "fractions of total utility: " + ssr.getFractionsOfTotalUtility()
        );
        // should be 0/15 2/14 1/13 9/12 8/11 7/10
        System.out.println(
            "fractions of total utility no jitter: " 
            + ssr.getFractionsOfTotalUtilityNoJitter()
        );
        // should be -1 2.5 1.5 5.0 4.5 4.0
        System.out.println(
            "mean teammate utilities: " + ssr.getMeanTeammateUtilities()
        );
        // should be -1 2 1 4.5 4 3.5
        System.out.println(
            "mean teammate utilities no jitter: " 
            + ssr.getMeanTeammateUtilitiesNoJitter()
        );
        // should be 13.5
        System.out.println(
            "best other team utility: " + ssr.getBestOtherTeamUtility(0)
        );
        // should be 12
        System.out.println(
            "best other team utility: " + ssr.getBestOtherTeamUtilityNoJitter(0)
        );
        // should be 8.0
        System.out.println(
            "best other team utility minus single good: " 
            + ssr.getBestOtherTeamUtilityMinusSingleGood(0)
        );
        // should be 7
        System.out.println(
            "best other team utility minus single good no jitter: " 
            + ssr.getBestOtherTeamUtilityMinusSingleGoodNoJitter(0)
        );
        // should be 13.5 11 12 0 0 0
        System.out.println("envy amount: " + ssr.getEnvyAmounts());
        // should be 12 10 11 0 0 0
        System.out.println(
            "envy amount no jitter: " + ssr.getEnvyAmountsNoJitter()
        );
        // should be 8 5.5 6.5 0 0 0
        System.out.println(
            "envy amount minus single good: " 
            + ssr.getEnvyAmountsMinusSingleGood()
        );
        // should be 7 5 6 0 0 0
        System.out.println(
            "envy amount minus single good no jitter: " 
            + ssr.getEnvyAmountsMinusSingleGoodNoJitter()
        );
    }
    
    private static void testIsCaptain() {
        final int n = 5;
        List<Agent> agents = new ArrayList<Agent>();
        List<Double> values = new ArrayList<Double>();
        List<UUID> uuids = new ArrayList<UUID>();
        for (int i = 0; i < n; i++) {
            Agent agent = new Agent(values, uuids, 0, 0, null);
            agents.add(agent);
        }
        
        List<List<Integer>> allocation = new ArrayList<List<Integer>>();
        Integer[] arr1 = {1, 0, 0, 0, 0};
        List<Integer> t1 = Arrays.asList(arr1);
        Integer[] arr2 = {0, 1, 1, 0, 0};
        List<Integer> t2 = Arrays.asList(arr2);
        List<Integer> t3 = Arrays.asList(arr2);
        Integer[] arr4 = {0, 0, 0, 1, 1};
        List<Integer> t4 = Arrays.asList(arr4);
        List<Integer> t5 = Arrays.asList(arr4);
        allocation.add(t1);
        allocation.add(t2);
        allocation.add(t3);
        allocation.add(t4);
        allocation.add(t5);
        
        final Integer[] captainsArr = {1, 3};
        List<Integer> captains = Arrays.asList(captainsArr);
        
        SimpleSearchResult ssr = new SimpleSearchResult(
            allocation, 
            1, 
            n, 
            agents, 
            null, 
            0,
            captains,
            0.0
        );
        // should be: 0 1 0 1 0
        System.out.println("is captain: " + ssr.isCaptain());
    }
    
    private static void testTeammateRanks() {
        final int n = 6;
        final List<Agent> myAgents = new ArrayList<Agent>();
        
        final Double[] valuesArr = {0.5, 1.5, 4.1, 4.2, 4.3, 4.5};
        List<Double> values = Arrays.asList(valuesArr);
        List<UUID> uuids = new ArrayList<UUID>();
        
        for (int i = 0; i < n; i++) {
            uuids.add(UUID.randomUUID());
        }
        
        for (int i = 0; i < n; i++) {
            List<Double> myValues = new ArrayList<Double>(values);
            myValues.remove(i);
            List<UUID> myUuids = new ArrayList<UUID>(uuids);
            myUuids.remove(i);
            Agent agent = new Agent(myValues, myUuids, 0, 0, uuids.get(i));
            myAgents.add(agent);
        }
        
        List<List<Integer>> myAllocation = new ArrayList<List<Integer>>();
        Integer[] arr1 = {1, 0, 0, 0, 0, 0};
        List<Integer> t1 = Arrays.asList(arr1);
        Integer[] arr2 = {0, 1, 1, 0, 0, 0};
        List<Integer> t2 = Arrays.asList(arr2);
        List<Integer> t3 = Arrays.asList(arr2);
        Integer[] arr4 = {0, 0, 0, 1, 1, 1};
        List<Integer> t4 = Arrays.asList(arr4);
        List<Integer> t5 = Arrays.asList(arr4);
        List<Integer> t6 = Arrays.asList(arr4);
        myAllocation.add(t1);
        myAllocation.add(t2);
        myAllocation.add(t3);
        myAllocation.add(t4);
        myAllocation.add(t5);
        myAllocation.add(t6);
        
        final SimpleSearchResult ssr = new SimpleSearchResult(
            myAllocation, 
            1, 
            n, 
            myAgents, 
            null, 
            0,
            null,
            0.0
        );
        // ranks: 6 5 4 3 2 1
        // ranks no jitter: 6 5 2.5 2.5 2.5 2.5
        // should be: -1 4 4 1.5 1.5 1.5
        System.out.println("mean teammate ranks: " + ssr.meanTeammateRanks());
        // should be: -1 2.5 4 2.0 2.0 2.0
        System.out.println(
            "mean teammate ranks no jitter: " + ssr.meanTeammateRanksNoJitter()
        );
        // should be: -1 4 4 1 1 1
        System.out.println(
            "favorite teammate ranks: " + ssr.favTeammateRanks()
        );
        // should be: -1 2.5 4 2 2 2
        System.out.println(
            "favorite teammate ranks no jitter: " 
            + ssr.favTeammateRanksNoJitter()
        );
        // should be: -1 2.5 4 2 2 2
        System.out.println(
            "least favorite teammate ranks: " + ssr.leastFavTeammateRanks()
        );
        // should be: -1 2.5 4 2 2 2
        System.out.println(
            "least favorite teammate ranks no jitter: " 
            + ssr.leastFavTeammateRanksNoJitter()
        );
        // should be: 0 2 2 9 9 9
        System.out.println(
            "sum of reversed ranks: " + ssr.sumsOfReversedRanks()
        );
        // should be: 0 3.5 2 8 8 8
        System.out.println(
            "sum of reversed ranks no jitter: " 
            + ssr.sumsOfReversedRanksNoJitter()
        );
    }
}
