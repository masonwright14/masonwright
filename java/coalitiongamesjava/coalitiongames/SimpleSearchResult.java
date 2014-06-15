package coalitiongames;
import java.util.ArrayList;
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

    public SimpleSearchResult(
        final List<List<Integer>> aAllocation,
        final int aKMin,
        final int aKMax,
        final List<Agent> aAgents,
        final List<Integer> aRsdOrder,
        final long aDurationMillis,
        final List<Integer> aCaptainIndexes
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
            
            final double tolerance = 0.1;
            if (Double.MAX_VALUE - tolerance > bestRank) {
                result.add(-1.0);
            } else {
                result.add(bestRank);
            }
        }
        return result;
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
    
    private int getTeamSize(final List<Integer> team) {
        int result = 0;
        for (final Integer item: team) {
            result += item;
        }
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
        builder.append(", \nisCaptain=");
        builder.append(isCaptain());
        builder.append("]");
        return builder.toString();
    }
}
