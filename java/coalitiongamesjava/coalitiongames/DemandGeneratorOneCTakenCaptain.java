package coalitiongames;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DemandGeneratorOneCTakenCaptain {

    /**
     * @param agents includes all agents, some of which already have
     * full or partly full teams, and one of which is the captain.
     * @param prices prices for all agents, in their listed order
     * @param teams list where each list holds the indexes in "agents"
     * of current members of a team.
     * @param finalTeamSizes final size required for each team, in order.
     * @param maxPrice highest price allowable for any agent
     * @param captain the taken agent that is next to choose a new teammate.
     * @return a list of the demand of each agent, in the order the agents
     * are listed. each agent's demand is listed for itself as 1, so each agent
     * has a demanded listed for all N agents, including itself. 
     * hence, the result can be thought of as a square matrix 
     * with 1 along the main diagonal,
     * stored as a list of lists of integers. all integers are in {0, 1}.
     * the sum along any row should be in {kMin, kMin + 1, . . ., kMax}, when
     * the demand for self at matrix[i][i] = 1 is considered.
     */
    public List<List<Integer>> getAggregateDemandTakenCaptain(
        final List<Agent> agents,
        final List<Double> prices,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes, 
        final double maxPrice,
        final Agent captain
    ) {
        assert prices.size() == agents.size();
        final int captainIndex = agents.indexOf(captain);
        // if this method is called, captain should be on a team already.
        assert EachDraftHelper.isAgentTaken(teams, captainIndex);
        
        final int captainTeamIndex = 
            EachDraftHelper.getTeamIndex(teams, agents.indexOf(captain));
        // captain's team should have room left if this method is called.
        assert teams.get(
            captainTeamIndex).size() 
            < finalTeamSizes.get(captainTeamIndex
        );
        
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
        // process Agents in order they are listed
        for (int i = 0; i < agents.size(); i++) {
            final Agent agent = agents.get(i);

            if (EachDraftHelper.isAgentTaken(teams, i)) {
                // agent is already on a team.
                final int teamIndex = EachDraftHelper.getTeamIndex(teams, i);
                if (
                    teams.get(teamIndex).size() == finalTeamSizes.get(teamIndex)
                ) {
                    // agent's team is full already.
                    // don't run MIP, because agent's demand is fixed.
                    // add this agent's current team as its demand.
                    final List<Integer> team = teams.get(teamIndex);
                    final List<Integer> row = new ArrayList<Integer>();
                    for (int j = 0; j < agents.size(); j++) {
                        if (team.contains(j)) {
                            row.add(1);
                        } else {
                            row.add(0);
                        }
                    }
                    
                    result.add(row);
                    continue;
                }
                
                // agent is taken, and its team has room.
                if (agent.equals(captain)) {
                    // agent is the captain agent.
                    
                    final List<Integer> row = 
                        getTakenCaptainDemand(
                            agents, prices, teams, finalTeamSizes, captain
                        );
                    result.add(row);
                    continue;
                }
                
                // agent is taken, its team has room, but it's not the captain
                
                final List<Integer> row = 
                    getTakenDummyDemand(
                        agents, prices, teams, finalTeamSizes, maxPrice, i
                    );
                result.add(row);
            } else {
                // agent is free, and not the captain (captain is taken).
                
                final List<Integer> row =
                    getFreeDummyDemand(
                        agents, prices, teams, finalTeamSizes, maxPrice, i
                    );
                result.add(row);
            }
        }
        
        return result;
    }
    
    
    /*
     * 
-- must demand all agents already on 1 team.
    -- treat each team as a single agent.
    -- team value = sum of agent values
    -- team cost = sum of agent costs
    -- must select exactly 1 team
-- must demand (finalTeamSize - (currentTeamSize + 1)) other free agents
    -- number of variables = # of teams + # of other free agents
-- total cost must be <= budget
-- maximize: total value of team + free agents

how to formulate as an MIP:
-- must take exactly 1 TEAM from a set
    -- each TEAM has a cost and a value
-- given a TEAM, must take f(TEAM) AGENTS from a set
    -- each AGENT has a cost and a value
-- total cost of 1 TEAM and f(TEAM) AGENTS <= BUDGET
-- maximize value(TEAM) + value(AGENTS).

-- how to do this:
   -- each TEAM demand is a column in x.
   -- each AGENT demand is a column in x.
   -- each column is in {0, 1}.
   -- each TEAM and each AGENT has a price and a value.
   -- maximize scalar product of value vector and x. # maximize total value
   -- subject to:
   -- scalar product of price vector and x <= budget. # must stay within budget
   -- sum of x_i's for i in TEAM indexes == 1. # must take exactly 1 TEAM
HOW TO HANDLE VARIABLE TEAM SIZE CONSTRAINT?
Want a rule like 
    "if x_i = 1, sum of x_k's for k in AGENT indexes== 3", 
    "if x_j = 1, sum of x_k's for k in AGENT indexes == 8"
Make a vector that assigns each TEAM index 
the value of (-1 * # of agents needed),
and assigns each AGENT 1.
    example:
    TEAM 1: needs 5 agents
    TEAM 2: needs 3 agents
    TEAM 3: needs 2 agents
            T1  T2  T3  A1 A2 A3 A4 A5
    vector: -5, -3, -2, 1, 1, 1, 1, 1
constraint:
    scalar product of this vector and x == 0.
    we already require that exactly one TEAM have 1, others have 0, in x.
    if exactly one TEAM index is 1, the only way the dot product will be 0
    is if the correct number of agents is demanded.
     */
    /**
     * 
     * @param agents includes all agents not just free agents or taken agents
     * @param prices includes price of each agent
     * @param teams list of lists, where each list is the indexes in "agents"
     * of current members of a team, which may or may not be full
     * @param finalTeamSizes final required team size for each team in order
     * @param maxPrice maximum price allowed in MIP
     * @param dummyIndex index of the current dummy agent, which must be taken
     * (i.e., on a team already)
     * @return a list of values in {0, 1}, of length equal to agents.size(),
     * where 1 indicates that the agent at dummyIndex demands the given agent.
     * result.get(dummyIndex) == 1.
     */
    public static List<Integer> getFreeDummyDemand(
        final List<Agent> agents,
        final List<Double> prices,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes, 
        final double maxPrice,
        final int dummyIndex
    ) {
        // dummy agent must be free, not on a team
        assert !EachDraftHelper.isAgentTaken(teams, dummyIndex);
        // the agent whose demand to get
        final Agent dummyAgent = agents.get(dummyIndex);

        // get value, price, and total agents needed to fill
        // for every team that has room on it. exclude
        // teams that are full already.
        final List<Double> teamValues = new ArrayList<Double>();
        final List<Double> teamPrices = new ArrayList<Double>();
        final List<Integer> teamAgentsNeeded = new ArrayList<Integer>();
        for (int i = 0; i < teams.size(); i++) {
            final int agentsNeeded = 
                finalTeamSizes.get(i) - teams.get(i).size();
            // team should not be over-filled at any time.
            assert agentsNeeded >= 0;
            if (agentsNeeded > 0) {
                // include this team, because it has room.
                teamAgentsNeeded.add(agentsNeeded);
                final List<Integer> team = teams.get(i);
                double teamValue = 0.0;
                double teamPrice = 0.0;
                for (final Integer index: team) {
                    final UUID id = agents.get(index).getUuid();
                    teamValue += dummyAgent.getValueByUUID(id);
                    teamPrice += prices.get(index);
                }
                teamValues.add(teamValue);
                teamPrices.add(teamPrice);
            }
        }
        
        // list value, price of every other free agent besides dummyAgent.
        final List<Double> agentValues = new ArrayList<Double>();
        final List<Double> agentPrices = new ArrayList<Double>();
        
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != dummyIndex) {
                // include this agent, because it's 
                // free and not the dummy agent.
                final double price = prices.get(i);
                final double value = 
                    dummyAgent.getValueByUUID(agents.get(i).getUuid());
                agentValues.add(value);
                agentPrices.add(price);
            }
        }
        
        final double budget = dummyAgent.getBudget();
        
        MipGenCPLEXFreeDummy mipGen = new MipGenCPLEXFreeDummy();
        final List<Integer> dummyDemand = 
            mipGen.getFreeDummyDemand(
                teamValues, teamPrices, 
                teamAgentsNeeded, agentValues, agentPrices, budget
            );
        
        int chosenTeamIndex = -1;
        for (int i = 0; i < teamValues.size(); i++) {
            if (dummyDemand.get(i) == 1) {
                chosenTeamIndex = i;
                break;
            }
        }
        if (chosenTeamIndex == -1) {
            // empty bundle received. return all 0's, but 1 for self.
            final List<Integer> result = new ArrayList<Integer>();
            for (int i = 0; i < agents.size(); i++) {
                if (i == dummyIndex) {
                    result.add(1);
                } else {
                    result.add(0);
                }
            }
            
            return result;
        }
        
        // filled bundle received.
        final List<Integer> demandedIndexes = new ArrayList<Integer>();
        // add self to the demand.
        demandedIndexes.add(dummyIndex);
        
        // add indexes of agents on the demanded team.
        int indexInTeamDemand = 0;
        boolean found = false;
        // find the demanded team.
        for (int i = 0; i < teams.size(); i++) {
            final List<Integer> team = teams.get(i);
            if (team.size() < finalTeamSizes.get(i)) {
                // team has room, so would be included in the MIP
                if (indexInTeamDemand == chosenTeamIndex) {
                    // team is the chosen team.
                    demandedIndexes.addAll(team);
                    found = true;
                    break;
                }
                
                indexInTeamDemand++;
            }
        }
        if (!found) {
            throw new IllegalStateException();
        }
        
        // first other free agent in "agents" has index in
        // dummyDemand of teams.size().
        int indexInDummyDemand = teams.size();
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != dummyIndex) {
                // agent is a free agent other than the dummy agent.
                if (dummyDemand.get(indexInDummyDemand) == 1) {
                    // dummy agent demanded this agent.
                    demandedIndexes.add(i);
                }
                
                indexInDummyDemand++;
            }
        }
        
        // demandedIndexes now contains indexes in "agents" of all demanded
        // agents.
        
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < agents.size(); i++) {
            if (demandedIndexes.contains(i)) {
                result.add(1);
            } else {
                result.add(0);
            }
        }
        
        return result;
    }
        
    /**
     * 
     * @param agents includes all agents not just free agents or taken agents
     * @param prices includes price of each agent
     * @param teams list of lists, where each list is the indexes in "agents"
     * of current members of a team, which may or may not be full
     * @param finalTeamSizes final required team size for each team in order
     * @param maxPrice maximum price allowed in MIP
     * @param dummyIndex index of the current dummy agent, which must be taken
     * (i.e., on a team already)
     * @return list of which agents the dummy agent demands. list
     * will have size agents.size(), one value per agent.
     * each item is in {0, 1}. number of 1's either equals number of agents
     * currently on the dummy agent's team (including self) if no feasible
     * bundle can be afforded, else number of 1's 
     * equals team size in finalTeamSizes
     * of the dummy agent's team.
     */
    public static List<Integer> getTakenDummyDemand(
        final List<Agent> agents,
        final List<Double> prices,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes, 
        final double maxPrice,
        final int dummyIndex
    ) {
        // dummy agent must be on a team already
        assert EachDraftHelper.isAgentTaken(teams, dummyIndex);
        // index in "teams" and "finalTeamSizes" of dummy agent's team
        final int dummyTeamIndex = 
            EachDraftHelper.getTeamIndex(teams, dummyIndex);
        // list of players on dummy agents' partly filled team
        final List<Integer> dummyTeam = teams.get(dummyTeamIndex);
        // the agent whose demand to get
        final Agent dummyAgent = agents.get(dummyIndex);
        // how many agents need to be added to fill the dummy agent's team
        final int agentsNeeded = 
            finalTeamSizes.get(dummyTeamIndex) - dummyTeam.size();
        // team should not be full already.
        assert agentsNeeded > 0;
        
        // only use prices and values of free agents.
        final List<Double> iPrices = new ArrayList<Double>();
        final List<Double> iValues = new ArrayList<Double>();
        for (int j = 0; j < prices.size(); j++) {
            // only include free agents in the MIP
            if (!EachDraftHelper.isAgentTaken(teams, j)) {
                iPrices.add(prices.get(j));
                final UUID id = agents.get(j).getUuid();
                iValues.add(dummyAgent.getValueByUUID(id));
            }
        }
        
        // dummy agent must pay the cost of team's current members.
        double fixedCost = 0.0;
        for (Integer index: dummyTeam) {
            // don't pay for self agent
            if (index != dummyIndex) {
                final UUID id = agents.get(index).getUuid();
                fixedCost += dummyAgent.getValueByUUID(id);
            }
        }
        
        // temporarily remove cost of current teammates from budget
        final double budget = dummyAgent.getBudget() - fixedCost;
        
        // find demand for free agents of the dummy agent.
        // dummy agent will seek to maximize total value of the free agents,
        // given how many are needed, and the budget constraint.
        MipGenerator mipGen = new MipGeneratorCPLEX();
        final MipResult mipResult = mipGen.getLpSolution(
            iValues, iPrices, budget, agentsNeeded, agentsNeeded, maxPrice
        );

        final List<Integer> mipValues = mipResult.getRoundedColumnValues();
        
        // must insert 1 for taken agents on 
        // captain team, 0 for other taken agents.
        for (int i = 0; i < agents.size(); i++) {
            if (EachDraftHelper.isAgentTaken(teams, i)) {
                if (dummyTeam.contains(i)) {
                    mipValues.add(i, 1);
                } else {
                    mipValues.add(i, 0);
                }
            }
        }
        
        return mipValues;
    }
    
    /*
-- must demand each agent already on team. this is a fixed cost.
-- must demand exactly (finalTeamSize - currentTeamSize) free agents
    -- number of variables = # of free agents
-- total cost of free agents must be <= budget - cost of current team
-- maximize: value of highest-value selected free agent

result: list of captain's demand for each agent, including self
     */
    /**
     * @param agents includes all agents not just free agents or taken agents
     * @param prices includes price of each agent
     * @param teams teams list of lists, where 
     * each list is the indexes in "agents"
     * of current members of a team, which may or may not be full
     * @param finalTeamSizes final required team size for each team in order
     * @param captain which agent is the captain that 
     * will choose a teammate to join
     * its team
     * @return list of which agents the captain demands. list
     * will have size agents.size(), one value per agent.
     * each item is in {0, 1}. number of 1's either equals 1 if no feasible
     * bundle can be afforded, else number of 1's equals 
     * team size in finalTeamSizes
     * of the captain's team.
     */
    private static List<Integer> getTakenCaptainDemand(
        final List<Agent> agents,
        final List<Double> prices,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final Agent captain
    ) {
        // index of "agents" of the captain
        final int captainIndex = agents.indexOf(captain);
        // captain must be taken already if this method called
        assert EachDraftHelper.isAgentTaken(teams, captainIndex);
        // index of captain's team in "teams" and "finalTeamSizes"
        final int captainTeamIndex = 
            EachDraftHelper.getTeamIndex(teams, captainIndex);
        // list of indexes in "agents" of players on 
        // captain's team currently, including self
        final List<Integer> captainTeam = teams.get(captainTeamIndex);
        final int agentsNeeded = 
             finalTeamSizes.get(captainTeamIndex) - captainTeam.size();
        // team must not be full if this method is called
        assert agentsNeeded > 0;
        
        // only use prices and values of free agents.
        final List<Double> iPrices = new ArrayList<Double>();
        final List<Double> iValues = new ArrayList<Double>();
        for (int j = 0; j < prices.size(); j++) {
            if (!EachDraftHelper.isAgentTaken(teams, j)) {
                iPrices.add(prices.get(j));
                final UUID id = agents.get(j).getUuid();
                iValues.add(captain.getValueByUUID(id));
            }
        }
        
        // remove cost of captain's current teammates from budget (temporarily)
        double fixedCost = 0.0;
        for (Integer index: captainTeam) {
            if (!agents.get(index).equals(captain)) {
                final UUID id = agents.get(index).getUuid();
                fixedCost += captain.getValueByUUID(id);
            }
        }
        
        final double budget = captain.getBudget() - fixedCost;

        // get list of which free agents the 
        // captain demands, if it can afford any.
        // list will have values in {0, 1} and size = # of free agents left.
        final List<Integer> captainDemand = 
            getTakenCaptainDemandFromFreeAgents(
                iValues, iPrices, budget, agentsNeeded
            );
        
        // must insert 1 for taken agents on captain 
        // team, 0 for other taken agents.
        for (int i = 0; i < agents.size(); i++) {
            if (EachDraftHelper.isAgentTaken(teams, i)) {
                if (captainTeam.contains(i)) {
                    captainDemand.add(i, 1);
                } else {
                    captainDemand.add(i, 0);
                }
            }
        }
        
        return captainDemand;
    }   
    
    /**
     * 
     * @param values contains values of free agents to the captain
     * @param prices contains prices only of free agents
     * @param budget captain's budget after paying for current team's
     * other taken agents
     * @param agentsNeeded how many agents the captain must demand
     * @return list of which free agents the captain demands. list
     * will have size values.size(), one value per free agent.
     * each item is in {0, 1}. number of 1's either equals 0 if no
     * set of size agentsNeeded is affordable, or equals agentsNeeded.
     */
    private static List<Integer> getTakenCaptainDemandFromFreeAgents(
        final List<Double> values,
        final List<Double> prices,
        final double budget,
        final int agentsNeeded
    ) {
        if (!MipChecker.isFeasible(prices, budget, agentsNeeded)) {
            // captain can't afford enough agents
            // return set of all 0's
            final List<Integer> result = new ArrayList<Integer>();
            for (int i = 0; i < values.size(); i++) {
                result.add(0);
            }
            return result;
        }
        
        // captain can afford "agentsNeeded" free agents.

        final List<Integer> greatestValueIndexes =
            getLargesttItemIndexesHighToLowValue(values, values.size());
        int favoriteIndex = -1;
        for (int index: greatestValueIndexes) {
            final List<Double> pricesWithoutI = new ArrayList<Double>(prices);
            pricesWithoutI.remove(index);
            final double budgetMinusIPrice = budget - prices.get(index);
            if (
                MipChecker.isFeasible(
                    pricesWithoutI, budgetMinusIPrice, agentsNeeded - 1
            )) {
                favoriteIndex = index;
                break;
            }
        }
        
        if (favoriteIndex == -1) {
            throw new IllegalStateException(
                "not feasible; should have caught before"
            );
        }
        
        // will take player favoriteIndex and 
        // (agentsNeeded - 1) cheapest other players.
        final List<Integer> team = new ArrayList<Integer>();
        team.add(favoriteIndex);
        
        // need "agentsNeeded" players to consider, in case favorite was one
        // of the cheapest players, because it's already taken.
        final List<Integer> lowestPriceIndexes = 
            getSmallestItemIndexesLowToHighValue(prices, agentsNeeded);
        for (int i = 0; i < lowestPriceIndexes.size(); i++) {
            if (i != favoriteIndex && team.size() < agentsNeeded) {
                team.add(i);
            }
        }
        
        assert team.size() == agentsNeeded;
        
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < values.size(); i++) {
            if (team.contains(i)) {
                result.add(1);
            } else {
                result.add(0);
            }
        }
         return result;
    }
    
    /*
     * returns the indexes in "values" of the "howMany" largest items
     * in "values", from largest to smallest value.
     */
    private static List<Integer> getLargesttItemIndexesHighToLowValue(
        final List<Double> values,
        final int howMany
    ) {
        assert values.size() >= howMany;
        assert howMany > 0;
        
        final List<Integer> result = new ArrayList<Integer>();
        while (result.size() < howMany) {
            double max = Double.MIN_VALUE;
            int maxIndex = -1;
            for (int i = 0; i < values.size(); i++) {
                if (!result.contains(i)) {
                    // don't count a value twice
                    if (values.get(i) >= max) {
                        // include equal values, not just strictly greater ones
                        max = values.get(i);
                        maxIndex = i;
                    }
                }
            }
            
            if (maxIndex == -1) {
                throw new IllegalStateException();
            }
            result.add(maxIndex);
        }
        
        return result;
    }
    
    
    /*
     * returns the indexes in "values" of the "howMany" smallest items
     * in "values", from smallest to largest value.
     */
    private static List<Integer> getSmallestItemIndexesLowToHighValue(
        final List<Double> values,
        final int howMany
    ) {
        assert values.size() >= howMany;
        assert howMany > 0;
        
        final List<Integer> result = new ArrayList<Integer>();
        while (result.size() < howMany) {
            double min = Double.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < values.size(); i++) {
                if (!result.contains(i)) {
                    // don't count a value twice
                    if (values.get(i) <= min) {
                        // include equal values, not just strictly lower ones
                        min = values.get(i);
                        minIndex = i;
                    }
                }
            }
            if (minIndex == -1) {
                throw new IllegalStateException();
            }
            result.add(minIndex);
        }
        return result;
    }
}
