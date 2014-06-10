package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class RsdUtil {

    /**
     * @param oldPriceList the price list including all agent prices
     * @param agentIndex the index of the self agent
     * @param indexesToRemove the indexes of other agent to be removed from
     * the price list
     * @return the price list, without the price of the self agent or any
     * of the agents whose indexes are in agentIndex.
     */
    public static List<Double> getPriceListWithout(
        final List<Double> oldPriceList, 
        final int agentIndex, 
        final List<Integer> indexesToRemove
    ) {
        // self index should not be in indexesToRemove, because indexesToRemove
        // should only include indexes of agents already assigned to teams,
        // and the self agent is the next agent 
        // to act as "captain," so it should not be on a team yet.
        assert !indexesToRemove.contains(agentIndex);
        
        final List<Double> result = new ArrayList<Double>();
        for (Double originalItem: oldPriceList) {
            result.add(originalItem);
        }
        
        final List<Integer> myIndexesToRemove = new ArrayList<Integer>();
        for (final Integer indexToRemove: indexesToRemove) {
            myIndexesToRemove.add(indexToRemove);
        }
        
        // remove the self agent price also
        myIndexesToRemove.add(agentIndex);
        // sort the indexes increasing
        Collections.sort(myIndexesToRemove);
        
        // iterate from last index (highest) to first
        for (int i = myIndexesToRemove.size() - 1; i >= 0; i--) {
            // get the current agent index
            final int indexToRemove = myIndexesToRemove.get(i);
            // remove the price for this agent
            result.remove(indexToRemove);
        }
        
        return result; 
    }
    
    /**
     * @param oldValueList values of other agents, not including the self agent
     * @param agentIndex index of the self agent
     * @param indexesToRemove indexes of the agents to remove. 
     * self agent index is not a member of this list.
     * @return
     */
    public static List<Double> getValueListWithout(
        final List<Double> oldValueList, 
        final int agentIndex, 
        final List<Integer> indexesToRemove
    ) {
        // self index should not be in indexesToRemove, because indexesToRemove
        // should only include indexes of agents already assigned to teams,
        // and the self agent is the next agent to 
        // act as "captain," so it should not be on a team yet.
        assert !indexesToRemove.contains(agentIndex);
        
        final List<Double> result = new ArrayList<Double>();
        for (Double originalItem: oldValueList) {
            result.add(originalItem);
        }
        // sort the indexes increasing
        Collections.sort(indexesToRemove);
        
        // iterate from last index (highest) to first
        for (int i = indexesToRemove.size() - 1; i >= 0; i--) {
            // get the current agent index
            final int indexToRemove = indexesToRemove.get(i);
            if (indexToRemove > agentIndex) {
                // agent's own index is already absent from the list,
                // because the agent does not list a value for itself,
                // so take the index 1 before
                result.remove(indexToRemove - 1);
            } else {
                // indexToRemove must be less than agentIndex, 
                // because we won't be asked to remove the agent's own index.
                result.remove(indexToRemove);
            }
        }
        
        return result;
    }
    
    /**
     * @param size how many entries to have int the resulting list
     * @return a shuffled list of integers from 0 to (size - 1).
     * that is, the list has all integers in {0, 1, . . . (size - 1)}
     * but in uniformly random order.
     * This method may be used to produce a random serial dictatorship
     * order for "size" number of agents.
     */
    public static List<Integer> getShuffledNumberList(final int size) {
        assert size >= 0;
        
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            result.add(i);
        }
        Collections.shuffle(result);
        return result;
    }
    
    /**
     * @param partialAllocation each list in the partialAllocation 
     * shows the agents in one team,
     * where any agent on the team has a 1, and all others have 0. 
     * each team has exactly 1 list.
     * every agent is on exactly 1 team (i.e., each agent 
     * has a 1 in exactly 1 list).
     * @return a list of lists indexed by agent index. the result 
     * is a square matrix in list form,
     * where each row shows the demand of a particular agent. the 
     * main diagonal will always have
     * all 1's. this is generated by producing k copies of each list 
     * in partialAllocation, 1 for
     * each member of that list's team, and ordering the resulting 
     * lists to match the indexes of
     * the team members.
     */
    public static List<List<Integer>> getAllocation(
        final List<List<Integer>> partialAllocation
    ) {
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
        for (int i = 0; i < partialAllocation.get(0).size(); i++) {
            // agent with index i's team has not been found yet
            boolean found = false;
            for (List<Integer> row: partialAllocation) {
                // if agent i is on this team
                if (row.get(i) == 1) {
                    // if i was already on a different team
                    if (found) {
                        throw new IllegalArgumentException(
                            "Duplicate value: " + i
                        );
                    }
                    // agent i has been found on a team
                    found = true;
                    
                    final List<Integer> copy = new ArrayList<Integer>();
                    for (final Integer item: row) {
                        copy.add(item);
                    }
                    // in the resulting list of lists, add a list representing
                    // agent i's team, as agent i's row.
                    result.add(copy);
                }
            }
            // if agent i was not found on any team
            if (!found) {
                System.out.println("Allocation given: ");
                for (List<Integer> row: partialAllocation) {
                    System.out.println(row);
                }
                throw new IllegalArgumentException("Missing value: " + i);
            }
        }
        
        return result;
    }
    
    /**
     * @param demand a list of values in {0, 1}, representing one
     * agent's demand.
     * @return the number of 1's in the list. counts each agent as
     * demanding itself.
     */
    public static int getTeamSize(final List<Integer> demand) {
        int teamSize = 0;
        // add 1 if there's a 1, else add a 0. this gives the team size.
        // note that 1 is always present for the self agent, as desired.
        for (Integer i: demand) {
            teamSize += i;
        }
        return teamSize;
    }
    
    /**
     * @param n number of agents remaining
     * @param kMin minimum agents per team, including self agent
     * @param kMax maximum agents per team, including self agent
     * @return a list of integers, where each integer is a feasible
     * number of agents to assign to the "next" team. to be feasible,
     * the integer must be in range [kMin, kMax], and the resulting
     * number of players left, (n - value), must be able to be divided
     * evenly into teams with sizes in [kMin, kMax].
     */
    public static List<Integer> getFeasibleNextTeamSizes(
        final int n,
        final int kMin,
        final int kMax
    ) {
        assert n >= 1;
        assert kMin >= 0;
        assert kMin <= kMax;
        // this method should not be called from a state that
        // already has no feasible split. it would always return the empty list.
        assert TabuSearch.checkKRange(n, kMin, kMax);
        
        final List<Integer> feasibleTeamSizes = new ArrayList<Integer>();
        // team size must be in {kMin, kMin + 1, . . ., kMax}
        for (int teamSize = kMin; teamSize <= kMax; teamSize++) {
            // check if the remaining problem of 
            // assigning (n - teamSize) agents
            // to teams of sizes in {kMin, kMin + 1, . . . kMax} is feasible.
            if (TabuSearch.checkKRange(n - teamSize, kMin, kMax)) {
                feasibleTeamSizes.add(teamSize);
            }
        }
        
        return feasibleTeamSizes;
    }
}