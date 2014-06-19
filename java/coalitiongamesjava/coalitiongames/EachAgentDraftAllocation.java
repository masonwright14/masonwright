package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class EachAgentDraftAllocation {

    
    public static SimpleSearchResult eachAgentDraftAllocation(
        final List<Agent> agents,
        final int kMax,
        final List<Integer> rsdOrder
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        final int n = agents.size();
        assert kMax <= n;
        
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        
        final List<Integer> finalTeamSizes = 
            RsdUtil.getOptimalTeamSizeList(agents.size(), kMax);

        // make it so first agent to pick has the smaller team size, etc.
        Collections.reverse(finalTeamSizes);
        assert finalTeamSizes.get(0) 
            <= finalTeamSizes.get(finalTeamSizes.size() - 1);

        // each list holds the indexes of agents on 1 team.
        final List<List<Integer>> teams =
            new ArrayList<List<Integer>>();
        final List<Integer> captainIndexes = new ArrayList<Integer>();
        // add 1 empty list for each team.
        // then add the captain to this empty list.
        for (int i = 0; i < finalTeamSizes.size(); i++) {
            teams.add(new ArrayList<Integer>());
            final int currentCaptainIndex = rsdOrder.get(i);
            captainIndexes.add(currentCaptainIndex);
            teams.get(i).add(currentCaptainIndex);
        }
        
        for (int i = 0; i < rsdOrder.size(); i++) {
            final int currentAgentIndex = rsdOrder.get(i);        
            final Agent currentAgent = agents.get(currentAgentIndex);
            if (EachDraftHelper.isAgentTaken(teams, currentAgentIndex)) {
                // agent is already on a team 
                // (either captain, or already chosen).
                final int teamIndex = 
                    EachDraftHelper.getTeamIndex(teams, currentAgentIndex);
                final int maxSizeForTeam = finalTeamSizes.get(teamIndex);
                if (teams.get(teamIndex).size() < maxSizeForTeam) {
                    // the team has room to add an agent.
                    final List<UUID> idsHighToLowValue = 
                        currentAgent.getAgentIdsHighValueToLow();
                    for (final UUID id: idsHighToLowValue) {
                        if (!EachDraftHelper.isAgentTaken(teams, agents, id)) {
                            final int indexToChoose = 
                                EachDraftHelper.getAgentIndexById(agents, id);
                            teams.get(teamIndex).add(indexToChoose);
                            break;
                        }
                    }
                }
            } else {
                // agent is not on a team.
                // add self to favorite other team that has room
                final int favoriteTeamIndex = 
                    getFavoriteTeamIndexWithSpace(
                        teams, finalTeamSizes, agents, currentAgent
                    );
                teams.get(favoriteTeamIndex).add(currentAgentIndex);
            }
        }
        
        if (MipGenerator.DEBUGGING) {
            int totalAgents = 0;
            for (final List<Integer> team: teams) {
                totalAgents += team.size();
            }
            if (totalAgents != agents.size()) {
                throw new IllegalStateException();
            }
        }
        
        final List<List<Integer>> allocation = 
            RandomAllocation.getAllocationFromLists(
                agents.size(), 
                teams
            );
        final int kMin = finalTeamSizes.get(0);
        final long searchDurationMillis = 
            new Date().getTime() - searchStartMillis;
        final double similarity = 
            PreferenceAnalyzer.getMeanPairwiseCosineSimilarity(agents);
        return new SimpleSearchResult(
            allocation, kMin, kMax, agents, 
            rsdOrder, searchDurationMillis, captainIndexes,
            similarity
        );
    }
    
    private static int getFavoriteTeamIndexWithSpace(
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes,
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
}
