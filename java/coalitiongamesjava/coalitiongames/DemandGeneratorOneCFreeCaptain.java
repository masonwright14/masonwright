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
                    teamValue += captain.getValueByUUID(id);
                    teamPrice += prices.get(index);
                }
                teamValues.add(teamValue);
                teamPrices.add(teamPrice);
            }
        }
        
        // list value, price of every other free agent besides captain.
        final List<Double> agentValues = new ArrayList<Double>();
        final List<Double> agentPrices = new ArrayList<Double>();
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != captainIndex) {
                // include this agent, because it's 
                // free and not the dummy agent.
                final double price = prices.get(i);
                final double value = 
                    captain.getValueByUUID(agents.get(i).getUuid());
                agentValues.add(value);
                agentPrices.add(price);
            }
        }
        double meanOtherFreeAgentValue = 0.0;
        if (!agentValues.isEmpty()) {
            for (final double value: agentValues) {
                meanOtherFreeAgentValue += value;
            }
            meanOtherFreeAgentValue /= agentValues.size();
        }
        
        final double budget = captain.getBudget();
        final MipGenCPLEXFreeCaptain mipGen = new MipGenCPLEXFreeCaptain();
        final List<Integer> captainDemand = 
            mipGen.getFreeCaptainDemand(
                teamValues, teamPrices, 
                teamAgentsNeeded, meanOtherFreeAgentValue, 
                agentPrices, budget
            );
        
        int chosenTeamIndex = -1;
        for (int i = 0; i < teamValues.size(); i++) {
            if (captainDemand.get(i) == 1) {
                chosenTeamIndex = i;
                break;
            }
        }
        if (chosenTeamIndex == -1) {
            // empty bundle received. return all 0's, but 1 for self.
            final List<Integer> result = new ArrayList<Integer>();
            for (int i = 0; i < agents.size(); i++) {
                if (i == captainIndex) {
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
        demandedIndexes.add(captainIndex);
        
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
        // captainDemand of teams.size().
        int indexInCaptainDemand = teams.size();
        for (int i = 0; i < agents.size(); i++) {
            if (!EachDraftHelper.isAgentTaken(teams, i) && i != captainIndex) {
                // agent is a free agent other than the dummy agent.
                if (captainDemand.get(indexInCaptainDemand) == 1) {
                    // captain demanded this agent.
                    demandedIndexes.add(i);
                }
                
                indexInCaptainDemand++;
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
}
