package coalitiongames;

import java.util.ArrayList;
import java.util.List;

abstract class RandomAllocation {
    
    public static SimpleSearchResult randomOptimalSizesAllocation(
        final List<Agent> agents,
        final int kMax
    ) {
        final List<Integer> optimalTeamSizes = 
            RsdUtil.getOptimalTeamSizeRange(agents.size(), kMax);
        return randomAllocation(
            agents, 
            optimalTeamSizes.get(1), 
            optimalTeamSizes.get(0)
        );
    }

    
    public static SimpleSearchResult randomAllocation(
        final List<Agent> agents,
        final int kMax,
        final int kMin
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        final int n = agents.size();
        assert kMax <= n;
        
        // each list holds the indexes of agents on 1 team.
        final List<List<Integer>> allocationAsLists =
            new ArrayList<List<Integer>>();
        final List<Integer> teamSizes = 
            RsdUtil.getRandomTeamSizeList(agents.size(), kMax, kMin);
        
        final List<Integer> shuffledIndexes = 
            RsdUtil.getShuffledNumberList(agents.size());
        int nextAgentIndex = 0;
        for (final Integer teamSize: teamSizes) {
            final List<Integer> team = new ArrayList<Integer>();
            for (int i = 0; i < teamSize; i++) {
                team.add(shuffledIndexes.get(nextAgentIndex + i));
            }
            nextAgentIndex += teamSize;
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
            getAllocationFromLists(agents.size(), allocationAsLists);
        return new SimpleSearchResult(allocation, kMin, kMax, agents, null);
    }
    
    public static List<List<Integer>> getAllocationFromLists(
        final int n,
        final List<List<Integer>> oldLists
    ) {
        if (MipGenerator.DEBUGGING) {
            int totalAgents = 0;
            for (final List<Integer> team: oldLists) {
                totalAgents += team.size();
            }
            if (totalAgents != n) {
                throw new IllegalStateException();
            }
        }
        
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
        for (int i = 0; i < n; i++) {
            for (List<Integer> oldList: oldLists) {
                if (oldList.contains(i)) {
                    List<Integer> iList = new ArrayList<Integer>();
                    for (int j = 0; j < n; j++) {
                        if (oldList.contains(j)) {
                            iList.add(1);
                        } else {
                            iList.add(0);
                        }
                    }
                    assert iList.get(i) == 1;
                    result.add(iList);
                    break;
                }
            }
        }
        
        return result;
    }
}
