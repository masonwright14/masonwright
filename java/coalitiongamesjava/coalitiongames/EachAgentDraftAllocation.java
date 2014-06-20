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
                    EachDraftHelper.getFavoriteTeamIndexWithSpace(
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
}
