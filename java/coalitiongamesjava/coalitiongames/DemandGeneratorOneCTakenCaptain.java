package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class DemandGeneratorOneCTakenCaptain {
    
    private static final double TOLERANCE = 0.01;

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
                    final List<Integer> row = 
                        DemandGeneratorOneCFreeCaptain.
                            getOwnTeamDemand(agents.size(), team);
                    
                    assert row.size() == agents.size();
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
                    assert row.size() == agents.size();
                    result.add(row);
                    continue;
                }
                
                // agent is taken, its team has room, but it's not the captain
                
                final List<Integer> row = 
                    getTakenDummyDemand(
                        agents, prices, teams, finalTeamSizes, maxPrice, i
                    );
                assert row.size() == agents.size();
                result.add(row);
            } else {
                // agent is free, and not the captain (captain is taken).
                
                final List<Integer> row =
                    getFreeDummyDemand(
                        agents, prices, teams, finalTeamSizes, maxPrice, i
                    );
                assert row.size() == agents.size();
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
        final List<Double> teamValues = 
            DemandGeneratorOneCFreeCaptain.
                teamValues(teams, finalTeamSizes, agents, dummyAgent);
        final List<Double> teamPrices = 
            DemandGeneratorOneCFreeCaptain.
                teamPrices(teams, finalTeamSizes, prices);
        final List<Integer> teamAgentsNeeded = 
            DemandGeneratorOneCFreeCaptain.
                teamAgentsNeeded(teams, finalTeamSizes);
        
        // list value, price of every other free agent besides dummyAgent.
        final List<Double> agentValues = 
            DemandGeneratorOneCFreeCaptain.
                otherFreeAgentValues(agents, teams, dummyAgent);
        final List<Double> agentPrices = 
            DemandGeneratorOneCFreeCaptain.
                otherFreeAgentPrices(agents, teams, prices, dummyAgent);
        
        assert agentValues.size() + 1 == EachAgentDraftTabu.
            countFreeAgentsLeft(teams, agents.size());
        
        final double budget = dummyAgent.getBudget();
        
        MipGenCPLEXFreeDummy mipGen = new MipGenCPLEXFreeDummy();
        final List<Integer> dummyDemand = 
            mipGen.getFreeDummyDemand(
                teamValues, teamPrices, 
                teamAgentsNeeded, agentValues, agentPrices, budget
            );
        
        final int chosenTeamIndex = 
            DemandGeneratorOneCFreeCaptain.
                chosenTeamIndex(teamValues.size(), dummyDemand);
        if (chosenTeamIndex == -1) {
            // empty bundle received. return all 0's, but 1 for self.
            return DemandGeneratorOneCFreeCaptain.
                getSoloAgentDemand(agents.size(), dummyIndex);
        }
        
        // filled bundle received.
        final List<Integer> demandedIndexes = new ArrayList<Integer>();
        // add self to the demand.
        demandedIndexes.add(dummyIndex);
        
        // add indexes of agents on the demanded team.
        demandedIndexes.addAll(
            DemandGeneratorOneCFreeCaptain.
                getChosenTeamMembers(teams, finalTeamSizes, chosenTeamIndex)
        );
        
        // first other free agent in "agents" has index in
        // dummyDemand of teams.size().
        demandedIndexes.addAll(
            DemandGeneratorOneCFreeCaptain.getChosenFreeAgents(
                teams, agents.size(), finalTeamSizes, dummyIndex, dummyDemand
            )
        );
        
        // demandedIndexes now contains indexes in "agents" of all demanded
        // agents.
        
        final List<Integer> result = DemandGeneratorOneCFreeCaptain.
            getOwnTeamDemand(agents.size(), demandedIndexes);
        
        if (MipGenerator.DEBUGGING) {
            double totalCost = 0.0;
            for (int i = 0; i < result.size(); i++) {
                if (i != dummyIndex) {
                    totalCost += result.get(i) * prices.get(i);
                }
            }
            
            assert totalCost  - TOLERANCE <= budget;
        }
        
        return result;
    }
    
    private static List<Double> allFreeAgentValues(
        final List<Agent> agents,
        final List<List<Integer>> teams,
        final Agent agent
    ) {
        assert EachDraftHelper.isAgentTaken(teams, agents.indexOf(agent));
        final List<Double> values = new ArrayList<Double>();
        for (int j = 0; j < agents.size(); j++) {
            // only include free agents
            if (!EachDraftHelper.isAgentTaken(teams, j)) {
                final UUID id = agents.get(j).getUuid();
                values.add(agent.getValueByUUID(id));
            }
        }
        return values;
    }
    
    private static List<Double> allFreeAgentPrices(
        final List<Agent> agents,
        final List<List<Integer>> teams,
        final List<Double> prices
    ) {
        final List<Double> agentPrices = new ArrayList<Double>();
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i)) {
                agentPrices.add(prices.get(i));
            }
        }
        
        return agentPrices;
    }
    
    private static double teamCost(
        final List<Integer> team,
        final List<Double> prices,
        final int selfIndex
    ) {
        assert team.contains(selfIndex);
        double result = 0.0;
        for (Integer index: team) {
            // don't pay for self agent
            if (index != selfIndex) {
                result += prices.get(index);
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
        // kMin is assumed to include the self agent, but the dummy agent
        // will not demand itself from among the free agents, so add 1
        // to agentsNeeded to get the equivalent kMin.
        final int kMin = agentsNeeded + 1;
        
        // only use prices and values of free agents.
        final List<Double> iPrices = allFreeAgentPrices(agents, teams, prices);
        final List<Double> iValues = 
            allFreeAgentValues(agents, teams, dummyAgent);
        
        // dummy agent must pay the cost of team's current members.
        final double fixedCost = teamCost(dummyTeam, prices, dummyIndex);
        // temporarily remove cost of current teammates from budget
        final double budget = dummyAgent.getBudget() - fixedCost;
        
        // find demand for free agents of the dummy agent.
        // dummy agent will seek to maximize total value of the free agents,
        // given how many are needed, and the budget constraint.
        MipGenerator mipGen = new MipGeneratorCPLEX();
        final MipResult mipResult = mipGen.getLpSolution(
            iValues, iPrices, budget, kMin, kMin, maxPrice
        );

        final List<Integer> mipValues = mipResult.getRoundedColumnValues();
        final List<Integer> result = getDemandTakensInserted(
            mipValues, teams, agents.size(), dummyTeam
        );
        
        if (MipGenerator.DEBUGGING) {
            double totalCost = 0.0;
            for (int i = 0; i < result.size(); i++) {
                if (i != dummyIndex) {
                    totalCost += result.get(i) * prices.get(i);
                }
            }
            
            assert totalCost  - TOLERANCE <= dummyAgent.getBudget();
        }
        
        return result;
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
        // agentsNeeded is how many other agents, NOT including the self agent,
        // are needed to fill the team.
        final int agentsNeeded = 
             finalTeamSizes.get(captainTeamIndex) - captainTeam.size();
        // team must not be full if this method is called
        assert agentsNeeded > 0;
        
        // only use prices and values of free agents.
        final List<Double> iPrices = allFreeAgentPrices(agents, teams, prices);
        final List<Double> iValues = allFreeAgentValues(agents, teams, captain);
        
        // remove cost of captain's current teammates from budget (temporarily)
        final double fixedCost = 
            teamCost(captainTeam, prices, captainIndex);       
        final double budget = captain.getBudget() - fixedCost;

        // get list of which free agents the 
        // captain demands, if it can afford any.
        // list will have values in {0, 1} and size = # of free agents left.
        final List<Integer> captainDemand = 
            getTakenCaptainDemandFromFreeAgents(
                iValues, iPrices, budget, agentsNeeded
            );
        
        // the total number of 1's either equals 0 if no set is affordable,
        // or agentsNeeded.
        assert RsdUtil.getTeamSize(captainDemand) == 0 
            || RsdUtil.getTeamSize(captainDemand) == agentsNeeded;
        
        // must insert 1 for taken agents on captain 
        // team, 0 for other taken agents.
        final List<Integer> result = getDemandTakensInserted(
            captainDemand, teams, agents.size(), captainTeam
        );
        assert result.size() == agents.size();
        
        if (MipGenerator.DEBUGGING) {
            double totalCost = 0.0;
            for (int i = 0; i < result.size(); i++) {
                if (i != captainIndex) {
                    totalCost += result.get(i) * prices.get(i);
                }
            }
            
            assert totalCost  - TOLERANCE <= captain.getBudget();
        }
        
        return result;
    }   
    
    /*
     * Return the given list partialDemand, with a 0 inserted
     * for each taken agent not in myTeam, and a 1 inserted
     * for each taken agent in myTeam.
     */
    private static List<Integer> getDemandTakensInserted(
        final List<Integer> partialDemand,
        final List<List<Integer>> teams,
        final int n,
        final List<Integer> myTeam
    ) {
        final List<Integer> result = new ArrayList<Integer>(partialDemand);
        // must insert 1 for taken agents on 
        // own team, 0 for other taken agents.
        for (int i = 0; i < n; i++) {
            if (EachDraftHelper.isAgentTaken(teams, i)) {
                if (myTeam.contains(i)) {
                    result.add(i, 1);
                } else {
                    result.add(i, 0);
                }
            }
        }
        
        return result;
    }
    
    public static List<Integer> zerosList(
        final int len
    ) {
        assert len > 0;
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < len; i++) {
            result.add(0);
        }
        return result;
    }
    
    /**
     * 
     * @param values contains values of free agents to the captain
     * @param prices contains prices only of free agents
     * @param budget captain's budget after paying for current team's
     * other taken agents
     * @param agentsNeeded how many agents the captain must demand,
     * not including itself, unless it can't afford this many
     * (in which case it demands 0 others).
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
        // kMin = other agents demanded + 1, in MipChecker.isFeasible().
        final int kMin = agentsNeeded + 1;
        if (!MipChecker.isFeasible(prices, budget, kMin)) {
            // captain can't afford enough agents
            // return set of all 0's
            return zerosList(values.size());
        }
        
        // captain can afford "agentsNeeded" free agents.

        final List<Integer> greatestValueIndexes =
            getLargestItemIndexesHighToLowValue(values, values.size());
        int favoriteIndex = -1;
        for (int index: greatestValueIndexes) {
            final List<Double> pricesWithoutI = new ArrayList<Double>(prices);
            pricesWithoutI.remove(index);
            final double budgetMinusIPrice = budget - prices.get(index);
            if (
                MipChecker.isFeasible(
                    pricesWithoutI, budgetMinusIPrice, kMin - 1
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
            final int itemIndex = lowestPriceIndexes.get(i);
            if (itemIndex != favoriteIndex && team.size() < agentsNeeded) {
                team.add(itemIndex);
            }
        }

        assert team.size() == agentsNeeded;
        final List<Integer> result = DemandGeneratorOneCFreeCaptain.
            getOwnTeamDemand(values.size(), team);
        
        if (MipGenerator.DEBUGGING) {
            double totalCost = 0.0;
            for (int i = 0; i < result.size(); i++) {
                totalCost += prices.get(i) * result.get(i);
            }
            
            assert totalCost  - TOLERANCE <= budget;
        }
        
        return result;
    }
    
    /*
     * returns the indexes in "values" of the "howMany" largest items
     * in "values", from largest to smallest value.
     */
    private static List<Integer> getLargestItemIndexesHighToLowValue(
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
        
        // first result must be at least as great in value as last.
        assert values.get(result.get(0)) 
            >= values.get(result.get(result.size() - 1));
        
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
        
        // first result must be at least as low in value as last.
        assert values.get(result.get(0)) 
            <= values.get(result.get(result.size() - 1));
        
        return result;
    }
    
    /*****************************************************************
     * TESTING
     */
    
    public static void main(final String[] args) {
        // testAllFreeAgentValues();
        // testAllFreeAgentPrices();
        // testTeamCost();
        // testZerosList();
        // testGetDemandTakensInserted();
        // testGetLargestItemIndexesHighToLowValue();
        // testGetSmallestItemIndexesLowToHighValue();
        // testGetTakenCaptainDemandFromFreeAgents();
        // testGetTakenDemand(false, true);
        // testGetAgentDemand(false, false);
        // testGetAgentDemand(true, false);
        testGetAggregateDemandTakenCaptain();
    }
    
    /*
     * Should be: [10.0, 11.0, 12.0, 13.0]
     */
    @SuppressWarnings("unused")
    private static void testAllFreeAgentValues() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        
        final Double[] captainValuesArr = 
    {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0};
        List<Double> captainValues = Arrays.asList(captainValuesArr);
        
        final int countAgents = 14;
        List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = DemandProblemGenerator.getUuids(countAgents);
        
        final int selfIndex = 0;
        for (int i = 0; i < countAgents; i++) {
            List<Double> values = new ArrayList<Double>();
            if (i == selfIndex) {
                values.addAll(captainValues);
            } else {
                for (int j = 1; j < countAgents; j++) {
                    values.add(1.0);
                }
            }

            final List<UUID> subsetList = 
                DemandProblemGenerator.getUuidsWithout(uuids, i);
            final int id = i;
            final double budget = 105.0;
            agents.add(
                new Agent(values, subsetList, budget, id, uuids.get(i))
            );
        }
        
        System.out.println(
            allFreeAgentValues(agents, teams, agents.get(selfIndex))
        );
    }
    
    /*
     * Should be: [11.0, 12.0, 13.0, 14.0]
     */
    @SuppressWarnings("unused")
    private static void testAllFreeAgentPrices() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        
        final int countAgents = 14;
        List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = DemandProblemGenerator.getUuids(countAgents);
        
        for (int i = 0; i < countAgents; i++) {
            List<Double> values = new ArrayList<Double>();            
            final List<UUID> subsetList = 
                DemandProblemGenerator.getUuidsWithout(uuids, i);
            final int id = i;
            final double budget = 105.0;
            agents.add(
                new Agent(values, subsetList, budget, id, uuids.get(i))
            );
        }
        
        final Double[] agentPricesArr = 
    {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0};
        List<Double> agentPrices = Arrays.asList(agentPricesArr);
        
        System.out.println(
            allFreeAgentPrices(
                agents, teams, agentPrices
            )
        );
    }
    
    /*
     * Should be: 10.0
     */
    @SuppressWarnings("unused")
    private static void testTeamCost() {
        final Integer[] teamArr = {0, 1, 4, 6};
        List<Integer> team = Arrays.asList(teamArr);
        final Double[] pricesArr = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0};
        List<Double> prices = Arrays.asList(pricesArr);
        final int selfIndex = 4;
        System.out.println(teamCost(team, prices, selfIndex));
    }
    
    /*
     * Should be: [0, 0, 0, 0, 0]
     */
    @SuppressWarnings("unused")
    private static void testZerosList() {
        final int count = 5;
        System.out.println(zerosList(count));
    }
    
    /*
     * Should be: [1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0]
     */
    @SuppressWarnings("unused")
    private static void testGetDemandTakensInserted() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        
        final int totalAgents = 14;
        
        final Integer[] myTeamArr = {0, 1, 2, 10, 11};
        List<Integer> myTeam = Arrays.asList(myTeamArr);
        
        // partial demand is just for free agents.
        // these are 10, 11, 12, 13.
        // {1, 1, 0, 0}.
        final Integer[] partialDemandArr = {1, 1, 0, 0};
        List<Integer> partialDemand = Arrays.asList(partialDemandArr);
        
        System.out.println(
            getDemandTakensInserted(partialDemand, teams, totalAgents, myTeam)
        );
    }
    
    @SuppressWarnings("unused")
    private static void testGetLargestItemIndexesHighToLowValue() {
        final Double[] valuesArr = {2.0, 1.0, 2.0, 4.0, 3.0};
        List<Double> values = Arrays.asList(valuesArr);
        final int howMany = 3;
        // should be: [3, 4, 2]
        System.out.println(
            getLargestItemIndexesHighToLowValue(values, howMany)
        );
        
        final int howMany2 = 4;
        // should be: [3, 4, 2, 0]
        System.out.println(
            getLargestItemIndexesHighToLowValue(values, howMany2)
        );
    }
    
    @SuppressWarnings("unused")
    private static void testGetSmallestItemIndexesLowToHighValue() {
        final Double[] valuesArr = {2.0, 1.0, 2.0, 4.0, 3.0};
        List<Double> values = Arrays.asList(valuesArr);
        final int howMany = 2;
        // should be: [1, 2]
        System.out.println(
            getSmallestItemIndexesLowToHighValue(values, howMany)
        );
        
        final int howMany2 = 3;
        // should be: [1, 2, 0]
        System.out.println(
            getSmallestItemIndexesLowToHighValue(values, howMany2)
        );
    }
    
    @SuppressWarnings("unused")
    private static void testGetTakenCaptainDemandFromFreeAgents() {
        final Double[] valueArr = {1.0, 2.0, 3.0, 4.0};
        List<Double> values = Arrays.asList(valueArr);
        final Double[] priceArr = {9.0, 9.5, 10.0, 10.5};
        List<Double> prices = Arrays.asList(priceArr);
        final double budget = 15.0;
        final int agentsNeeded = 2;
        // should be: [0, 0, 0, 0]
        System.out.println(
            getTakenCaptainDemandFromFreeAgents(
                values, prices, budget, agentsNeeded
            )
        );
        
        final double budget2 = 25.0;
        // should be: [1, 0, 0, 1]
        // most valuable affordable agent, and cheapest other agent
        System.out.println(
            getTakenCaptainDemandFromFreeAgents(
                values, prices, budget2, agentsNeeded
            )
        );
    }
    
    /*
     * If isFree, should be: [0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1]
     * includes most valuable team: {5, 6, 7, 8, 9}
     * also includes 2 more agents, self {11} and most valuable other agent {13}
     * 
     * If isCaptain, should be: [1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1]
     * includes team so far: {0, 1, 2}
     * also includes 2 more free agents, the most desired one
     * and the cheapest other agent.
     * 
     * If !isCaptain, should be: [1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1]
     * includes team so far: {0, 1, 2}
     * also includes 2 more free agents, the two most desired.
     */
    @SuppressWarnings("unused")
    private static void testGetAgentDemand(
        final boolean isFree,
        final boolean isCaptain
    ) {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        final Integer[] teamSizesArr = {5, 2, 7};
        List<Integer> teamSizes = Arrays.asList(teamSizesArr);
        
        final Double[] captainValuesArr = 
        {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0};
        List<Double> captainValues = Arrays.asList(captainValuesArr);
        
        final int countAgents = 14;
        List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = DemandProblemGenerator.getUuids(countAgents);
        
        final int takenIndex = 0;
        final int freeIndex = 10;
        int selfIndex = takenIndex;
        if (isFree) {
            selfIndex = freeIndex;
        }
        for (int i = 0; i < countAgents; i++) {
            List<Double> values = new ArrayList<Double>();
            if (i == selfIndex) {
                values.addAll(captainValues);
            } else {
                for (int j = 1; j < countAgents; j++) {
                    values.add(1.0);
                }
            }

            final List<UUID> subsetList = 
                DemandProblemGenerator.getUuidsWithout(uuids, i);
            final int id = i;
            final double budget = 105.0;
            agents.add(
                new Agent(values, subsetList, budget, id, uuids.get(i))
            );
        }
        
        final double maxPrice = 105.0;
        final Double[] agentPricesArr = 
    {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0};
        List<Double> agentPrices = Arrays.asList(agentPricesArr);
        if (isFree) {
            System.out.println(
                getFreeDummyDemand(
                    agents, agentPrices, teams, teamSizes, maxPrice, selfIndex
                )
            );
            
            return;
        }
        
        if (isCaptain) {
            System.out.println(
                getTakenCaptainDemand(
                    agents, agentPrices, teams, teamSizes, agents.get(selfIndex)
                )
            );
        } else {
            System.out.println(
                getTakenDummyDemand(
                    agents, agentPrices, teams, teamSizes, maxPrice, selfIndex
            ));
        }
    }
    
    /*
     * Should be:
1 1 1 0 0 0 0 0 0 0 1 0 0 1 
1 1 1 0 0 0 0 0 0 0 0 0 1 1 
1 1 1 0 0 0 0 0 0 0 0 0 1 1 
0 0 0 1 1 0 0 0 0 0 0 0 0 0 
0 0 0 1 1 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 1 0 0 1 
0 0 0 0 0 1 1 1 1 1 0 1 0 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 

    Captain chooses current team, plus most valuable affordable agent
    and cheapest other agents to fill team.
    Other taken agents choose current team and most valuable affordable
    free agents to fill team.
    Free agents choose most valuable team and most valuable affordable
    other free agents to fill team.
     */
    private static void testGetAggregateDemandTakenCaptain() {
        Integer[] team1Arr = {0, 1, 2};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {3, 4};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {5, 6, 7, 8, 9};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        final Integer[] teamSizesArr = {5, 2, 7};
        List<Integer> teamSizes = Arrays.asList(teamSizesArr);
        
        final Double[] captainValuesArr = 
        {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0};
        List<Double> captainValues = Arrays.asList(captainValuesArr);
        
        final int countAgents = 14;
        List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = DemandProblemGenerator.getUuids(countAgents);
        
        final int selfIndex = 0;
        for (int i = 0; i < countAgents; i++) {
            List<Double> values = new ArrayList<Double>();
            values.addAll(captainValues);

            final List<UUID> subsetList = 
                DemandProblemGenerator.getUuidsWithout(uuids, i);
            final int id = i;
            final double budget = 200.0;
            agents.add(
                new Agent(values, subsetList, budget, id, uuids.get(i))
            );
        }
        
        final Double[] agentPricesArr = 
    {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0};
        List<Double> agentPrices = Arrays.asList(agentPricesArr);
        DemandGeneratorOneCTakenCaptain demandGen = 
            new DemandGeneratorOneCTakenCaptain();
        final double maxPrice = 105.0;
        Util.printDemandAsMatrix(
            demandGen.getAggregateDemandTakenCaptain(
                agents, agentPrices, teams, 
                teamSizes, maxPrice, agents.get(selfIndex)
            )
        );
    }
}
