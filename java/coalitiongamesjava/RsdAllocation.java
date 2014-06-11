package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

abstract class RsdAllocation {
    
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

        // make it so first agent to pick has the smaller team size, etc.
        Collections.reverse(teamSizes);
        assert teamSizes.get(0) <= teamSizes.get(teamSizes.size() - 1);

        return rsdHelper(agents, kMax, kMin, rsdOrder, teamSizes);
    }

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
    
    private static SimpleSearchResult rsdHelper(
        final List<Agent> agents,
        final int kMax,
        final int kMin,
        final List<Integer> rsdOrder,
        final List<Integer> teamSizes
    ) {
        // each list holds the indexes of agents on 1 team.
        final List<List<Integer>> allocationAsLists =
            new ArrayList<List<Integer>>();
        final List<Integer> takenAgentIndexes = new ArrayList<Integer>();
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
        return new SimpleSearchResult(allocation, kMin, kMax, agents, rsdOrder);
    }
}
