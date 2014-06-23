package coalitiongames;

import java.util.ArrayList;
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
                    
                    result.add(row);
                    continue;
                }
                
                // agent is taken, its team has room, but it's not the captain
                final List<Integer> row = 
                    DemandGeneratorOneCTakenCaptain.getTakenDummyDemand(
                        agents, prices, teams, finalTeamSizes, maxPrice, i
                    );
                result.add(row);
            } else {
                // agent is free, not on a team.
                
                if (agent.equals(captain)) {
                    // agent is the captain agent.
                    final List<Integer> row = 
                        getFreeCaptainDemand(
                            agents, prices, teams, finalTeamSizes, captain
                        );
                    result.add(row);
                    continue;
                }
                
                // agent is free, and not the captain
                final List<Integer> row =
                    DemandGeneratorOneCTakenCaptain.getFreeDummyDemand(
                        agents, prices, teams, finalTeamSizes, maxPrice, i
                    );
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
     */
    public static List<Double> teamValues(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final List<Agent> agents,
        final Agent captain
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
                    teamValue += captain.getValueByUUID(id);
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
        final Agent captain
    ) {
        final int captainIndex = agents.indexOf(captain);
        final List<Double> agentValues = new ArrayList<Double>();
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != captainIndex) {
                // include this agent, because it's 
                // free and not the dummy agent.
                final double value = 
                    captain.getValueByUUID(agents.get(i).getUuid());
                agentValues.add(value);
            }
        }
        return agentValues;
    }
    
    public static List<Double> otherFreeAgentPrices(
        final List<Agent> agents,
        final List<List<Integer>> teams,
        final List<Double> prices,
        final Agent captain
    ) {
        final int captainIndex = agents.indexOf(captain);
        final List<Double> agentPrices = new ArrayList<Double>();
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != captainIndex) {
                // include this agent, because it's 
                // free and not the dummy agent.
                final double price = prices.get(i);
                agentPrices.add(price);
            }
        }
        
        return agentPrices;
    }
    
    /*
     * -1 if no team chosen
     */
    public static int chosenTeamIndex(
        final int numTeams,
        final List<Integer> captainDemand 
    ) {
        int chosenTeamIndex = -1;
        for (int i = 0; i < numTeams; i++) {
            if (captainDemand.get(i) == 1) {
                chosenTeamIndex = i;
                break;
            }
        }
        return chosenTeamIndex;
    }
    
    public static List<Integer> getChosenFreeAgents(
        final List<List<Integer>> teams,
        final int n,
        final int captainIndex,
        final List<Integer> captainDemand
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        int indexInCaptainDemand = teams.size();
        for (int i = 0; i < n; i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != captainIndex) {
                // agent is a free agent other than the dummy agent.
                if (captainDemand.get(indexInCaptainDemand) == 1) {
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
        
        // first other free agent in "agents" has index in
        // captainDemand of teams.size().
        demandedIndexes.addAll(
            getChosenFreeAgents(
                teams, agents.size(), captainIndex, captainDemand
            )
        );
        
        // demandedIndexes now contains indexes in "agents" of all demanded
        // agents.
        
        return getOwnTeamDemand(agents.size(), demandedIndexes);
    }
}
