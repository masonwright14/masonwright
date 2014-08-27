package coalitiongames;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class RsdAllocation {
    
    /*
     * rsdOrder: first item is the index of the first agent to go.
     * second item is the index of second agent to go, etc.
     * example:
     * 1 2 0 -> agent 1 goes, then agent 2, then agent 0.
     */
    public static SimpleSearchResult rsdOptimalSizesAllocation(
        final List<Agent> agents,
        final int kMax,
        final int kMin,
        final List<Integer> rsdOrder
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        final int n = agents.size();
        assert kMax <= n;
        
        final List<Integer> teamSizes = 
            RsdUtil.getOptimalTeamSizeList(agents.size(), kMax);

        assert teamSizes.get(0) >= teamSizes.get(teamSizes.size() - 1);

        return rsdHelper(agents, kMax, kMin, rsdOrder, teamSizes);
    }

    /*
     * rsdOrder: first item is the index of the first agent to go.
     * second item is the index of second agent to go, etc.
     * example:
     * 1 2 0 -> agent 1 goes, then agent 2, then agent 0.
     */
    public static SimpleSearchResult rsdGreedySizesAllocation(
        final List<Agent> agents,
        final int kMax,
        final int kMin,
        final List<Integer> rsdOrder
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        final int n = agents.size();
        assert kMax <= n;
        
        // let first agent choose maximum feasible team size, etc.
        final List<Integer> teamSizes = 
            RsdUtil.getGreedyTeamSizeList(agents.size(), kMax, kMin);
        assert teamSizes.get(0) >= teamSizes.get(teamSizes.size() - 1);
        
        return rsdHelper(agents, kMax, kMin, rsdOrder, teamSizes);
    }
    
    /*
     * rsdOrder: first item is the index of the first agent to go.
     * second item is the index of second agent to go, etc.
     * example:
     * 1 2 0 -> agent 1 goes, then agent 2, then agent 0.
     */
    private static SimpleSearchResult rsdHelper(
        final List<Agent> agents,
        final int kMax,
        final int kMin,
        final List<Integer> rsdOrder,
        final List<Integer> teamSizes
    ) {
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        
        // each list holds the indexes of agents on 1 team.
        final List<List<Integer>> allocationAsLists =
            new ArrayList<List<Integer>>();
        final List<Integer> takenAgentIndexes = new ArrayList<Integer>();
        final List<Integer> captainIndexes = new ArrayList<Integer>();
        for (final Integer teamSize: teamSizes) {
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
            captainIndexes.add(captainIndex);
            final List<Integer> team = new ArrayList<Integer>();
            team.add(captainIndex);
            takenAgentIndexes.add(captainIndex);
            final Agent captain = agents.get(captainIndex);
            for (final UUID aUuid: captain.getAgentIdsHighValueToLow()) {
                boolean isTaken = false;
                for (final Integer takenAgentIndex: takenAgentIndexes) {
                    if (agents.get(takenAgentIndex).getUuid().equals(aUuid)) {
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
                    if (team.size() == teamSize) {
                        break;
                    }
                }
            }
            
            allocationAsLists.add(team);
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
        final long searchDurationMillis = 
            new Date().getTime() - searchStartMillis;
        final double similarity = 
            PreferenceAnalyzer.getMeanPairwiseCosineSimilarity(agents);
        return new SimpleSearchResult(
            allocation, kMin, kMax, agents, 
            rsdOrder, searchDurationMillis, captainIndexes, similarity
        );
    }
}
