package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public final class DemandGeneratorOneCFreeCaptain {

    /**
     * @param agents includes all agents, some of which already have
     * full or partly full teams, and one of which is the captain.
     * @param prices prices for all agents, in their listed order
     * @param teams list where each list holds the indexes in "agents"
     * of current members of a team.
     * @param finalTeamSizes final size required for each team, in order.
     * @param maxPrice highest price allowable for any agent
     * @param captain the taken agent that is next to choose a team to join.
     * @return a list of the demand of each agent, in the order the agents
     * are listed. each agent's demand is listed for itself as 1, so each agent
     * has a demanded listed for all N agents, including itself. 
     * hence, the result can be thought of as a square matrix 
     * with 1 along the main diagonal,
     * stored as a list of lists of integers. all integers are in {0, 1}.
     * the sum along any row should be in {kMin, kMin + 1, . . ., kMax}, when
     * the demand for self at matrix[i][i] = 1 is considered.
     */
    public List<List<Integer>> getAggregateDemandFreeCaptain(
        final List<Agent> agents,
        final List<Double> prices,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes, 
        final double maxPrice,
        final Agent captain
    ) {
        assert prices.size() == agents.size();
        final int captainIndex = agents.indexOf(captain);
        // if this method is called, captain should be free, not on a team.
        assert !EachDraftHelper.isAgentTaken(teams, captainIndex);

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
                        getOwnTeamDemand(agents.size(), team);
                    
                    assert row.size() == agents.size();
                    result.add(row);
                    continue;
                }
                
                // agent is taken, its team has room, but it's not the captain
                final List<Integer> row = 
                    DemandGeneratorOneCTakenCaptain.getTakenDummyDemand(
                        agents, prices, teams, finalTeamSizes, maxPrice, i
                    );
                assert row.size() == agents.size();
                result.add(row);
            } else {
                // agent is free, not on a team.
                
                if (agent.equals(captain)) {
                    // agent is the captain agent.
                    final List<Integer> row = 
                        getFreeCaptainDemand(
                            agents, prices, teams, finalTeamSizes, captain
                        );
                    assert row.size() == agents.size();
                    result.add(row);
                    continue;
                }
                
                // agent is free, and not the captain
                final List<Integer> row =
                    DemandGeneratorOneCTakenCaptain.getFreeDummyDemand(
                        agents, prices, teams, finalTeamSizes, maxPrice, i
                    );
                assert row.size() == agents.size();
                result.add(row);
            }
        }        
        
        return result;
    }
    
    /**
     * @param teams
     * @param finalTeamSizes
     * @return skip over teams that are full, so the list
     * may not be as long as finalTeamSizes.
     */
    public static List<Integer> teamAgentsNeeded(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < teams.size(); i++) {
            final int agentsNeeded = 
                finalTeamSizes.get(i) - teams.get(i).size();
            assert agentsNeeded >= 0;
            if (agentsNeeded > 0) {
                result.add(agentsNeeded);
            }
        }
        return result;
    }
    
    /**
     * @param teams
     * @param finalTeamSizes
     * @return skip over teams that are full, so the list
     * may not be as long as finalTeamSizes.
     * get sum of values of players on each partial team.
     */
    public static List<Double> teamValues(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final List<Agent> agents,
        final Agent selfAgent
    ) {
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < teams.size(); i++) {
            final int agentsNeeded = 
                finalTeamSizes.get(i) - teams.get(i).size();
            if (agentsNeeded > 0) {
                double teamValue = 0.0;
                final List<Integer> team = teams.get(i);
                for (final Integer index: team) {
                    final UUID id = agents.get(index).getUuid();
                    teamValue += selfAgent.getValueByUUID(id);
                }
                result.add(teamValue);
            }
        }
        return result;
    }
    
    /**
     * @param teams
     * @param finalTeamSizes
     * @return skip over teams that are full, so the list
     * may not be as long as finalTeamSizes.
     */
    public static List<Double> teamPrices(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final List<Double> prices
    ) {
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < teams.size(); i++) {
            final int agentsNeeded = 
                finalTeamSizes.get(i) - teams.get(i).size();
            if (agentsNeeded > 0) {
                double teamPrice = 0.0;
                final List<Integer> team = teams.get(i);
                for (final Integer index: team) {
                    teamPrice += prices.get(index);
                }
                result.add(teamPrice);
            }
        }
        return result;
    }
    
    public static List<Double> otherFreeAgentValues(
        final List<Agent> agents,
        final List<List<Integer>> teams,
        final Agent selfAgent
    ) {
        assert !EachDraftHelper.isAgentTaken(teams, agents.indexOf(selfAgent));
        final int selfIndex = agents.indexOf(selfAgent);
        final List<Double> agentValues = new ArrayList<Double>();
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != selfIndex) {
                // include this agent, because it's 
                // free and not the dummy agent.
                final double value = 
                    selfAgent.getValueByUUID(agents.get(i).getUuid());
                agentValues.add(value);
            }
        }
        return agentValues;
    }
    
    public static List<Double> otherFreeAgentPrices(
        final List<Agent> agents,
        final List<List<Integer>> teams,
        final List<Double> prices,
        final Agent selfAgent
    ) {
        assert !EachDraftHelper.isAgentTaken(teams, agents.indexOf(selfAgent));
        final int selfIndex = agents.indexOf(selfAgent);
        final List<Double> agentPrices = new ArrayList<Double>();
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != selfIndex) {
                // include this agent, because it's 
                // free and not the dummy agent.
                final double price = prices.get(i);
                agentPrices.add(price);
            }
        }
        
        return agentPrices;
    }
    
    /*
     * -1 if no team chosen.
     * 
     * Assumes that selfDemand has a 0 or 1 for each team
     * that is demanded, followed by a 0 or 1 for each agent
     * that is demanded.
     */
    public static int chosenTeamIndex(
        final int numTeams,
        final List<Integer> selfDemand
    ) {
        if (MipGenerator.DEBUGGING) {
            for (Integer item: selfDemand) {
                if (item != 0 && item != 1) {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        int chosenTeamIndex = -1;
        for (int i = 0; i < numTeams; i++) {
            if (selfDemand.get(i) == 1) {
                chosenTeamIndex = i;
                break;
            }
        }
        return chosenTeamIndex;
    }
    
    /*
     * Assumes that selfDemand has a 0 or 1 for each team
     * with space left, followed by a 0 or 1 for each other free agent.
     * 
     * Only teams with space and free agents are included.
     */
    public static List<Integer> getChosenFreeAgents(
        final List<List<Integer>> teams,
        final int totalAgentsInModel,
        final List<Integer> finalTeamSizes,
        final int selfIndex,
        final List<Integer> selfDemand
    ) {
        // agent should be free also if this method is called.
        assert !EachDraftHelper.isAgentTaken(teams, selfIndex);
        if (MipGenerator.DEBUGGING) {
            for (Integer item: selfDemand) {
                if (item != 0 && item != 1) {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        final int otherFreeAgentCount = 
            EachAgentDraftTabu.
                countFreeAgentsLeft(teams, totalAgentsInModel) - 1;
        final int teamsWithSpaceCount = 
            EachAgentDraftTabu.countTeamsWithSpace(teams, finalTeamSizes);
        assert selfDemand.size() 
            == teamsWithSpaceCount + otherFreeAgentCount;
        final List<Integer> result = new ArrayList<Integer>();
        int indexInCaptainDemand = teamsWithSpaceCount;
        for (int i = 0; i < totalAgentsInModel; i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != selfIndex) {
                // agent is a free agent other than the dummy agent.
                if (selfDemand.get(indexInCaptainDemand) == 1) {
                    // captain demanded this agent.
                    result.add(i);
                }
                
                indexInCaptainDemand++;
            }
        }
        return result;
    }
    
    /*
     * chosenTeamIndex is only based on list of teams that are not full,
     * not among "teams" list of all teams.
     */
    public static List<Integer> getChosenTeamMembers(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final int chosenTeamIndex
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        int indexInTeamDemand = 0;
        // find the demanded team.
        for (int i = 0; i < teams.size(); i++) {
            final List<Integer> team = teams.get(i);
            if (team.size() < finalTeamSizes.get(i)) {
                // team has room, so would be included in the MIP
                if (indexInTeamDemand == chosenTeamIndex) {
                    // team is the chosen team.
                    result.addAll(team);
                    return result;
                }
                
                indexInTeamDemand++;
            }
        }
        
        throw new IllegalStateException("not found");
    }
    
    public static List<Integer> getSoloAgentDemand(
        final int n,
        final int selfIndex
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) {
            if (i == selfIndex) {
                result.add(1);
            } else {
                result.add(0);
            }
        }
        return result;
    }
    
    /**
     * @param n total agent count
     * @param team agents demanded
     * @return 1 for each item in team, 0 for others
     */
    public static List<Integer> getOwnTeamDemand(
        final int n,
        final List<Integer> team
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int j = 0; j < n; j++) {
            if (team.contains(j)) {
                result.add(1);
            } else {
                result.add(0);
            }
        }
        return result;
    }
    
    private static double meanOtherFreeAgentValue(
        final List<Double> otherFreeAgentValues
    ) {
        double meanOtherFreeAgentValue = 0.0;
        if (!otherFreeAgentValues.isEmpty()) {
            for (final double value: otherFreeAgentValues) {
                assert value >= 0.0;
                meanOtherFreeAgentValue += value;
            }
            meanOtherFreeAgentValue /= otherFreeAgentValues.size();
        }
        return meanOtherFreeAgentValue;
    }
    
    /*
-- must demand all agents on a team that has room.
-- must demand exactly (finalTeamSize - (teamSize + 1)) free agents
    -- number of variables = # of free agents + # of teams
-- total cost of team and agents must be <= budget
-- maximize: value of selected team

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
     * will choose a team to join
     * @return list of which agents the captain demands. list
     * will have size agents.size(), one value per agent.
     * each item is in {0, 1}. number of 1's either equals 1 if no feasible
     * bundle can be afforded, else number of 1's equals 
     * size in finalTeamSizes of the team the captain decides to join.
     */
    private static List<Integer> getFreeCaptainDemand(
        final List<Agent> agents,
        final List<Double> prices,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final Agent captain
    ) {
        // index of "agents" of the captain
        final int captainIndex = agents.indexOf(captain);
        // captain must be free, not on a team if this method called
        assert !EachDraftHelper.isAgentTaken(teams, captainIndex);        
        // get value, price, and total agents needed to fill
        // for every team that has room on it. exclude
        // teams that are full already.
        final List<Double> teamValues = 
            teamValues(teams, finalTeamSizes, agents, captain);
        final List<Double> teamPrices = 
            teamPrices(teams, finalTeamSizes, prices);
        final List<Integer> teamAgentsNeeded = 
            teamAgentsNeeded(teams, finalTeamSizes);

        // list value, price of every other free agent besides captain.
        final List<Double> agentValues = 
            otherFreeAgentValues(agents, teams, captain);
        final List<Double> agentPrices = 
            otherFreeAgentPrices(agents, teams, prices, captain);

        final double meanOtherFreeAgentValue = 
            meanOtherFreeAgentValue(agentValues);
        
        final double budget = captain.getBudget();
        final MipGenCPLEXFreeCaptain mipGen = new MipGenCPLEXFreeCaptain();
        final List<Integer> captainDemand = 
            mipGen.getFreeCaptainDemand(
                teamValues, teamPrices, 
                teamAgentsNeeded, meanOtherFreeAgentValue, 
                agentPrices, budget
            );
        
        final int chosenTeamIndex = 
            chosenTeamIndex(teamValues.size(), captainDemand);
        if (chosenTeamIndex == -1) {
            // empty bundle received. return all 0's, but 1 for self.           
            return getSoloAgentDemand(agents.size(), captainIndex);
        }
        
        // filled bundle received.
        final List<Integer> demandedIndexes = new ArrayList<Integer>();
        // add self to the demand.
        demandedIndexes.add(captainIndex);
        
        // add indexes of agents on the demanded team.
        demandedIndexes.addAll(
            getChosenTeamMembers(teams, finalTeamSizes, chosenTeamIndex)
        );
        
        // set of demanded agents only includes agents other than the self
        // agent, and only includes other free agents.
        // set of demanded teams only includes teams with space.
        
        // first other free agent in "agents" has index in
        // captainDemand of teams.size().
        demandedIndexes.addAll(
            getChosenFreeAgents(
                teams, agents.size(), 
                finalTeamSizes, captainIndex, captainDemand
            )
        );
        
        // demandedIndexes now contains indexes in "agents" of all demanded
        // agents.
        
        final List<Integer> result = 
            getOwnTeamDemand(agents.size(), demandedIndexes);
        
        if (MipGenerator.DEBUGGING) {
            double totalCost = 0.0;
            for (int i = 0; i < result.size(); i++) {
                if (i != captainIndex) {
                    totalCost += result.get(i) * prices.get(i);
                }
            }
            
            final double tolerance = 0.01;
            assert totalCost  - tolerance <= budget;
        }
        
        return result;
    }
    
    /*****************************************************************
     * TESTING
     */
    
    public static void main(final String[] args) {
        // testTeamAgentsNeeded();
        // testTeamValues();
        // testTeamPrices();
        // testOtherFreeAgentValues();
        // testOtherFreeAgentPrices();
        // testChosenTeamIndex();
        // testChosenFreeAgents();
        // testGetChosenTeamMembers();
        // testGetSoloAgentDemand();
        // testGetOwnTeamDemand();
        // testMeanOtherFreeAgentValue();
        // testGetFreeCaptainDemand();
        testGetAggregateDemandFreeCaptain();
    }
    
    /*
     * Should be [2, 5] because full second team
     * is skipped.
     */
    @SuppressWarnings("unused")
    private static void testTeamAgentsNeeded() {
        final Integer[] team1Arr = {1, 2, 3};
        List<Integer> team1 = Arrays.asList(team1Arr);
        final Integer[] team2Arr = {4, 5};
        List<Integer> team2 = Arrays.asList(team2Arr);
        final Integer[] team3Arr = {6, 7, 8, 9, 10};
        List<Integer> team3 = Arrays.asList(team3Arr);
        List<List<Integer>> teams = new ArrayList<List<Integer>>();
        teams.add(team1);
        teams.add(team2);
        teams.add(team3);

        final Integer[] teamSizesArr = {5, 2, 10};
        List<Integer> teamSizes = Arrays.asList(teamSizesArr);
        System.out.println(teamAgentsNeeded(teams, teamSizes));
    }
    
    /*
     * should be [11.0, 12.0, 13.0]
     */
    @SuppressWarnings("unused")
    private static void testOtherFreeAgentValues() {
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
        
        final int selfIndex = 10;
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
            otherFreeAgentValues(agents, teams, agents.get(selfIndex))
        );
    }
    
    /*
     * Should be [6.0, 40.0]
     */
    @SuppressWarnings("unused")
    private static void testTeamValues() {
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
        
        final int selfIndex = 10;
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
            teamValues(teams, teamSizes, agents, agents.get(selfIndex))
        );
    }
    
    /*
     * should be [11.0, 12.0, 13.0]
     */
    @SuppressWarnings("unused")
    private static void testOtherFreeAgentPrices() {
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
        
        final  Double[] captainValuesArr = 
    {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0};
        List<Double> captainValues = Arrays.asList(captainValuesArr);
        
        final int countAgents = 14;
        List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = DemandProblemGenerator.getUuids(countAgents);
        
        final int selfIndex = 10;
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
        
        final Double[] agentPricesArr = 
    {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0};
        List<Double> agentPrices = Arrays.asList(agentPricesArr);
        
        System.out.println(
            otherFreeAgentPrices(
                agents, teams, agentPrices, agents.get(selfIndex)
            )
        );
    }
    
    // should be [6.0, 40.0]
    @SuppressWarnings("unused")
    private static void testTeamPrices() {
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
        
        final Double[] agentPricesArr = 
    {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0};
        List<Double> agentPrices = Arrays.asList(agentPricesArr);
        
        System.out.println(teamPrices(teams, teamSizes, agentPrices));
    }
    
    /*
     * Should be 2, because this is the demanded team index.
     */
    @SuppressWarnings("unused")
    private static void testChosenTeamIndex() {
        Integer[] selfDemandArr = {0, 0, 1, 0, 1, 1, 0, 0};
        List<Integer> selfDemand = Arrays.asList(selfDemandArr);
        final int numTeams = 3;
        System.out.println(chosenTeamIndex(numTeams, selfDemand));
    }
    
    // should return [11]
    @SuppressWarnings("unused")
    private static void testChosenFreeAgents() {
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
        
        final int countAgents = 14;
        List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = DemandProblemGenerator.getUuids(countAgents);
        
        for (int i = 0; i < countAgents; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < countAgents; j++) {
                values.add(1.0);
            }
            final List<UUID> subsetList = 
                DemandProblemGenerator.getUuidsWithout(uuids, i);
            final int id = i;
            final double budget = 105.0;
            agents.add(
                new Agent(values, subsetList, budget, id, uuids.get(i))
            );
        }
        
        final int selfIndex = 10;
        // self demands: team {0, 1, 2}, self, and other free agent 11.
        // non-full team demand: team0 of {team0, team2}
        // other free agent demand: free agents {fa11} of {fa11, fa12, fa13}.
        // demand: {1, 0, 1, 1, 0, 0}
        Integer[] selfDemandArr = {1, 0, 1, 0, 0};
        List<Integer> selfDemand = Arrays.asList(selfDemandArr);
        
        System.out.println(
            getChosenFreeAgents(
                teams, countAgents, teamSizes, selfIndex, selfDemand
            )
        );
    }
    
    /*
     * Should return [5, 6, 7, 8, 9] because team [3, 4]
     * is skipped, since it is full.
     */
    @SuppressWarnings("unused")
    private static void testGetChosenTeamMembers() {
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
        
        final int chosenTeamIndex = 1;
        System.out.println(
            getChosenTeamMembers(
                teams, teamSizes, chosenTeamIndex
            )
        );
    }
    
    /*
     * Should be: [0, 0, 1, 0, 0]
     */
    @SuppressWarnings("unused")
    private static void testGetSoloAgentDemand() {
        final int n = 5;
        final int selfIndex = 2;
        System.out.println(getSoloAgentDemand(n, selfIndex));
    }
    
    /*
     * Should be: [1, 0, 0, 1, 0, 0, 0, 1, 1, 0]
     */
    @SuppressWarnings("unused")
    private static void testGetOwnTeamDemand() {
        final int n = 10;
        final Integer[] teamArr = {0, 3, 7, 8};
        List<Integer> team = Arrays.asList(teamArr);
        System.out.println(getOwnTeamDemand(n, team));
    }
    
    // should be 1.6
    @SuppressWarnings("unused")
    private static void testMeanOtherFreeAgentValue() {
        final Double[] valuesArr = {1.0, 0.0, 0.5, 2.0, 4.5};
        List<Double> values = Arrays.asList(valuesArr);
        System.out.println(meanOtherFreeAgentValue(values));
    }
    
    /*
     * should be: [0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0]
     */
    @SuppressWarnings("unused")
    private static void testGetFreeCaptainDemand() {
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
        
        final int selfIndex = 10;
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
        
        final Double[] agentPricesArr = 
    {1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0};
        List<Double> agentPrices = Arrays.asList(agentPricesArr);
        
        System.out.println(
            getFreeCaptainDemand(
                agents, agentPrices, teams, teamSizes, agents.get(selfIndex)
            )
        );
    }
    
    /*
     * Should be:
1 1 1 0 0 0 0 0 0 0 0 0 1 1 
1 1 1 0 0 0 0 0 0 0 0 0 1 1 
1 1 1 0 0 0 0 0 0 0 0 0 1 1 
0 0 0 1 1 0 0 0 0 0 0 0 0 0 
0 0 0 1 1 0 0 0 0 0 0 0 0 0 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 1 1 0 0 
0 0 0 0 0 1 1 1 1 1 0 1 0 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 
0 0 0 0 0 1 1 1 1 1 0 0 1 1 

    Captain chooses most valuable team, plus lowest price other agents
    to fill that team.
     */
    private static void testGetAggregateDemandFreeCaptain() {
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
        
        final int selfIndex = 10;
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
        DemandGeneratorOneCFreeCaptain demandGen = 
            new DemandGeneratorOneCFreeCaptain();
        final double maxPrice = 105.0;
        Util.printDemandAsMatrix(
            demandGen.getAggregateDemandFreeCaptain(
                agents, agentPrices, teams, 
                teamSizes, maxPrice, agents.get(selfIndex)
            )
        );
    }
}
