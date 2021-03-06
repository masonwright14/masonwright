package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class DraftAllocation {

    /*
     * rsdOrder: first item is the index of the first agent to go.
     * second item is the index of second agent to go, etc.
     * example:
     * 1 2 0 -> agent 1 goes, then agent 2, then agent 0.
     */
    public static SimpleSearchResult draftAllocation(
        final List<Agent> agents,
        final int kMax,
        final List<Integer> rsdOrder
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        final int n = agents.size();
        assert kMax <= n;
        
        final List<Integer> teamSizes = 
            RsdUtil.getOptimalTeamSizeList(agents.size(), kMax);

        // make it so first agent to pick has the smaller team size, etc.
        Collections.reverse(teamSizes);
        assert teamSizes.get(0) <= teamSizes.get(teamSizes.size() - 1);
        
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();

        // each list holds the indexes of agents on 1 team.
        final List<List<Integer>> allocationAsLists =
            new ArrayList<List<Integer>>();
        // holds the index of the captain of each team, in order.
        final List<Integer> captainIndexes = new ArrayList<Integer>();
        final List<Integer> takenAgentIndexes = new ArrayList<Integer>();
                
        // pick team captains:
        // 1. pick next team captain, based on RSD order of remaining players
        // repeat until all captains are selected.
        for (int i = 0; i < teamSizes.size(); i++) {
            int captainIndex = -1;
            for (Integer indexRsd: rsdOrder) {
                if (!takenAgentIndexes.contains(indexRsd)) {
                    captainIndex = indexRsd;
                    break;
                }
            }
            if (captainIndex == -1) {
                throw new IllegalStateException();
            }
            final List<Integer> team = new ArrayList<Integer>();
            allocationAsLists.add(team);
            team.add(captainIndex);
            takenAgentIndexes.add(captainIndex);
            captainIndexes.add(captainIndex);
        }
        
        boolean isReverse = false;
        // while not all agents allocated
        while (
            allocationAsLists.get(allocationAsLists.size() - 1).size() 
            < teamSizes.get(teamSizes.size() - 1)
        ) {
            if (isReverse) {
                for (int i = teamSizes.size() - 1; i >= 0; i--) {
                    final List<Integer> team = allocationAsLists.get(i);
                    final int maxTeamSize = teamSizes.get(i);
                    final Agent captain = agents.get(captainIndexes.get(i));
                    addAgentToTeamAndTakenAgentsIfRoom(
                        team, maxTeamSize, captain, agents, takenAgentIndexes
                    );
                }
            } else {
                for (int i = 0; i < teamSizes.size(); i++) {
                    final List<Integer> team = allocationAsLists.get(i);
                    final int maxTeamSize = teamSizes.get(i);
                    final Agent captain = agents.get(captainIndexes.get(i));
                    addAgentToTeamAndTakenAgentsIfRoom(
                        team, maxTeamSize, captain, agents, takenAgentIndexes
                    );
                }
            }
            
            isReverse = !isReverse;
        }
        
        if (MipGenerator.DEBUGGING) {
            int totalAgents = 0;
            for (final List<Integer> team: allocationAsLists) {
                totalAgents += team.size();
            }
            if (totalAgents != agents.size()) {
                throw new IllegalStateException();
            }
        }
            
        final List<List<Integer>> allocation = 
            RandomAllocation.getAllocationFromLists(
                agents.size(), 
                allocationAsLists
            );
        final int kMin = teamSizes.get(0);
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
    
    private static void addAgentToTeamAndTakenAgentsIfRoom(
        final List<Integer> team,
        final int maxTeamSize,
        final Agent captain,
        final List<Agent> agents,
        final List<Integer> takenAgentIndexes
    ) {
        // if the team can hold another agent
        if (maxTeamSize > team.size()) {
            for (final UUID aUuid: captain.getAgentIdsHighValueToLow()) {
                boolean isTaken = false;
                for (final Integer takenAgentIndex: takenAgentIndexes) {
                    if (
                        agents.get(takenAgentIndex).getUuid().equals(aUuid)
                    ) {
                        isTaken = true;
                        break;
                    }
                }
                if (!isTaken) {
                    int index = -1;
                    for (int i = 0; i < agents.size(); i++) {
                        if (agents.get(i).getUuid().equals(aUuid)) {
                            index = i;
                            break;
                        }
                    }
                    assert index != -1;
                    team.add(index);
                    takenAgentIndexes.add(index);
                    
                    // take only 1 more agent
                    break;
                }
            }
        }
    }
}
