package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public abstract class EachDraftHelper {
    
    public static int getAgentIndexById(
        final List<Agent> agents,
        final UUID uuid
    ) {
        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).getUuid().equals(uuid)) {
                return i;
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    public static int getTeamIndex(
        final List<List<Integer>> teams, 
        final int index
    ) {
        for (int i = 0; i < teams.size(); i++) {
            if (teams.get(i).contains(index)) {
                return i;
            }
        }
        
        throw new IllegalArgumentException("not on a team");
    }
    
    public static boolean isAgentTaken(
        final List<List<Integer>> teams, 
        final List<Agent> agents,
        final UUID uuid
    ) {
        for (List<Integer> team: teams) {
            for (Integer index: team) {
                final Agent agent = agents.get(index);
                if (agent.getUuid().equals(uuid)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static boolean isAgentTaken(
        final List<List<Integer>> teams, 
        final int index
    ) {
        for (List<Integer> team: teams) {
            if (team.contains(index)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static int getFavoriteTeamIndexWithSpace(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
        final List<Agent> agents,
        final Agent selfAgent
    ) {
        final double meanValueOtherRemainingAgents = 
            meanOtherFreeAgentValue(teams, agents, selfAgent);

        double maxTeamValue = -1.0;
        int bestTeamIndex = -1;
        for (int i = 0; i < teams.size(); i++) {
            final List<Integer> currentTeam = teams.get(i);
            final int currentTeamMaxSize = finalTeamSizes.get(i);
            
            // don't consider teams that are already full.
            if (currentTeam.size() == currentTeamMaxSize) {
                continue;
            }
            
            double currentTeamValue = 0.0;
            for (final Integer playerIndex: currentTeam) {
                final UUID playerUUID = agents.get(playerIndex).getUuid();
                final double playerValue = selfAgent.getValueByUUID(playerUUID);
                currentTeamValue += playerValue;
            }
            
            if (currentTeam.size() + 1 < currentTeamMaxSize) {
                // team has room for another agent besides self agent.
                
                final int extraSpaces = 
                    currentTeamMaxSize - (1 + currentTeam.size());
                currentTeamValue += extraSpaces * meanValueOtherRemainingAgents;
            }
            
            if (currentTeamValue > maxTeamValue) {
                maxTeamValue = currentTeamValue;
                bestTeamIndex = i;
            }
        }
        
        assert bestTeamIndex != -1;
        return bestTeamIndex;
    }
    
    private static double meanOtherFreeAgentValue(
        final List<List<Integer>> teams,
        final List<Agent> agents,
        final Agent selfAgent
    ) {
        // find mean value of other agents not yet on a team.
        double untakenOtherAgentsValue = 0.0;
        int untakenOtherAgentCount = 0;
        for (int i = 0; i < agents.size(); i++) {
            // don't count the self agent when evaluating
            // the mean value of other remaining agents.
            if (agents.get(i).equals(selfAgent)) {
                continue;
            }
            if (!EachDraftHelper.isAgentTaken(teams, i)) {
                final UUID currentAgentId = agents.get(i).getUuid();
                untakenOtherAgentsValue += 
                    selfAgent.getValueByUUID(currentAgentId);
                untakenOtherAgentCount++;
            }
        }
        
        double meanValueOtherRemainingAgents = 0.0;
        if (untakenOtherAgentCount > 0) {
            meanValueOtherRemainingAgents =
                untakenOtherAgentsValue / untakenOtherAgentCount;
        }
        
        return meanValueOtherRemainingAgents;
    }
    
    /*****************************************************************
     * TESTING
     */
    
    public static void main(final String[] args) {
        // testMeanOtherFreeAgentValue();
        testGetFavoriteTeamIndexWithSpace();
    }
    
    /*
     * Should be: 12.0
     */
    public static void testMeanOtherFreeAgentValue() {
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
            meanOtherFreeAgentValue(teams, agents, agents.get(selfIndex))
        );
    }
    
    /*
     * Should be: 1. Team 2 is more valuable but has no space.
     */
    private static void testGetFavoriteTeamIndexWithSpace() {
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
        
        final Integer[] teamSizesArr = {4, 5, 5};
        List<Integer> teamSizes = Arrays.asList(teamSizesArr);
        System.out.println(
            getFavoriteTeamIndexWithSpace(
                teams, teamSizes, agents, agents.get(selfIndex)
            )
        );
    }
}
