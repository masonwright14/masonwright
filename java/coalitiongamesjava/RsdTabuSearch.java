package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class RsdTabuSearch {
    
    /**
     * One-level RSD tabu search works as follows. An approximate CEEI
     * is found by tabu search. If there is no market clearing error,
     * this result is returned. Else, an RSD order is randomly assigned to
     * the agents. The first agent chooses its favorite affordable bundle
     * of teammates that leaves a feasible number left, 
     * and all those teammates are "out" of consideration and
     * finally allocated to this team. the next agent in RSD order chooses
     * its favorite affordable bundle of the remaining agents that leaves
     * a feasible number of remaining agents, and all those agents are "out"
     * and finally allocated to this team. this is repeated until all agents
     * are assigned to teams.
     * 
     * @param agents a list of all agents with their budgets and preferences
     * @param gammaZ an error function to use for updating prices
     * @param kMax maximum agents per team, including self
     * @param kMin minimum agents per team, including self
     * @param rsdOrder shuffled list of numbers from {0, 1, . . . (n - 1)}, 
     * indicating the random serial dictatorship order to use, based on
     * indexes of Agents in "agents" list
     * @return a SearchResult, including an allocation, price vector,
     * and other data
     */
    public static SearchResult rsdTabuSearchOneLevel(
        final List<Agent> agents,
        final GammaZ gammaZ,
        final int kMax,
        final int kMin,
        final List<Integer> rsdOrder
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        assert rsdOrder.size() == agents.size();
        assert gammaZ != null;
        final int n = agents.size();
        assert kMax <= n;
        assert kMax >= kMin;
        assert kMin >= 0;
        assert TabuSearch.checkKRange(n, kMin, kMax);
        
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        // initialResult sets the prices of all agents. if it has market
        // clearing error, not all agents 
        // will get their allocations from this result.
        final SearchResult initialResult = TabuSearch.tabuSearch(
            agents, gammaZ, kMax, kMin);
        
        
        // an allocation. each player appears in exactly 1 List<Integer>, 
        // or row. there is one row per team, instead of 1 row per player.
        // columns (items) in each List<Integer> correspond to players, by index
        // in initialResult.
        final List<List<Integer>> allocation = new ArrayList<List<Integer>>();
        
        // indexes in initialResult of players already allocated to a team.
        final List<Integer> takenAgentIndexes = new ArrayList<Integer>();
        // iterate over players in RSD order, by their index in initialResult
        for (final Integer agentIndex: rsdOrder) {
            // if the player has already been assigned to a team, skip it.
            if (takenAgentIndexes.contains(agentIndex)) {
                continue;
            }
            if (MipGenerator.DEBUGGING) {
                // check for duplicates in takenAgentIndexes.
                Set<Integer> takenAgentIndexSet = new HashSet<Integer>();
                takenAgentIndexSet.addAll(takenAgentIndexes);
                if (takenAgentIndexes.size() != takenAgentIndexSet.size()) {
                    throw new IllegalStateException(
                        "Duplicate entry: " + takenAgentIndexes
                    );
                }
                
                for (int i = 0; i < agents.size(); i++) {
                    if (!rsdOrder.contains(i)) {
                        throw new IllegalArgumentException("Missing rsdOrder index: " + i);
                    }
                }
            }
                        
            // get count of agents that have not yet been assigned to a team.
            final int agentsLeft = agents.size() - takenAgentIndexes.size();
            assert agentsLeft > 0;
            // feasible team sizes include self as a team member. 
            // an agent can demand 1 less than any value in this list.
            final List<Integer> feasibleTeamSizes = 
                getFeasibleNextTeamSizes(agentsLeft, kMin, kMax);
            if (feasibleTeamSizes.isEmpty()) {
                throw new IllegalStateException();
            }
            /*
             * check if agent can be allocated its favorite 
             * affordable bundle from initialResult.
             * -- can't demand any agent that is already "taken"
             * -- size of demanded team must be feasible
             *     -- for example, if there are 12 agents left, kMin = 4,
             *     kMax = 6, can't take 5 agents and leave 7.
             */
            // check if initalResult demand for the current agent is feasible.
            final List<Integer> initialAgentDemand = 
                initialResult.getAllocation().get(agentIndex);
            
            // check if the demand tries to take a player already taken.
            boolean hasDemandConflict = false;
            for (int i = 0; i < initialAgentDemand.size(); i++) {
                // if the agent demands an agent already taken . . .
                if (
                    initialAgentDemand.get(i) == 1 
                    && takenAgentIndexes.contains(i)
                ) {
                    hasDemandConflict = true;
                    break;
                }
            }
            if (!hasDemandConflict) {
                // check if the demanded team size is feasible
                final int teamSize = getTeamSize(initialAgentDemand);
                if (feasibleTeamSizes.contains(teamSize)) {
                    // the allocation is feasible. make this allocation.
                    allocation.add(initialAgentDemand);
                    // indicate that all taken agents have been taken.
                    for (int i = 0; i < initialAgentDemand.size(); i++) {
                        if (initialAgentDemand.get(i) == 1) {
                            takenAgentIndexes.add(i);
                        }
                    }
                    assert takenAgentIndexes.contains(agentIndex);
                    // done with this agent
                    continue;
                }
            }

            assert !takenAgentIndexes.contains(agentIndex);
            // initialDemand demand for this agent was not feasible.
             /* set up MIP over remaining agents, for the current agent
             * -- prices and budgets are the same as in the initialSolution
             * -- remove any agents already "taken" from the MIP
             * -- restrict # of agents demanded to a "feasible" 
             * number
             * assign the given bundle to the agent and remove its 
             * contents from consideration.    
             */
            final Agent currentAgent = 
                initialResult.getAgents().get(agentIndex);
            final List<Double> values = getValueListWithout(
                currentAgent.getValues(), agentIndex, takenAgentIndexes
            );
            final List<Double> prices = getPriceListWithout(
                initialResult.getPrices(), agentIndex, takenAgentIndexes
            );
            final MipGenerator mipGenerator = new MipGeneratorGLPK();
            final double maxPrice = MipGenerator.MIN_BUDGET 
                + MipGenerator.MIN_BUDGET / agents.size();
            final MipResult mipResult = mipGenerator.getLpSolution(
                values, prices,  currentAgent.getBudget(), 
                feasibleTeamSizes, maxPrice
            );
            final List<Integer> newAgentDemand = 
                mipResult.getRoundedColumnValues();
            
            // add 0 demand for agents already on teams,
            // and 1 demand for the self agent
            Collections.sort(takenAgentIndexes);
            for (int i = 0; i < agents.size(); i++) {
                if (takenAgentIndexes.contains(i)) {
                    newAgentDemand.add(i, 0);
                }
                if (i == agentIndex) {
                    newAgentDemand.add(i, 1);
                }
            }
            
            assert newAgentDemand.size() == agents.size();
            
            // make this allocation.
            allocation.add(newAgentDemand);
            // indicate that all taken agents have been taken.
            for (int i = 0; i < newAgentDemand.size(); i++) {
                if (newAgentDemand.get(i) == 1) {
                    takenAgentIndexes.add(i);
                }
            }
            assert takenAgentIndexes.contains(agentIndex);
        }
        
        final List<Double> error = new ArrayList<Double>();
        for (int i = 0; i < agents.size(); i++) {
            error.add(0.0);
        }
        final double errorSize = 0.0;
        final double maxBudget = MipGenerator.MIN_BUDGET 
            + MipGenerator.MIN_BUDGET / agents.size();
        final long searchDurationMillis = 
            new Date().getTime() - searchStartMillis;
        final List<List<Integer>> resultAllocation = getAllocation(allocation);
        final SearchResult result = new SearchResult(
            initialResult.getPrices(), resultAllocation, error, 
            errorSize, kMin, kMax, maxBudget, agents, searchDurationMillis,
            rsdOrder
        );
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
    private static List<List<Integer>> getAllocation(
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
    private static int getTeamSize(final List<Integer> demand) {
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
    private static List<Integer> getFeasibleNextTeamSizes(
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
    
    /**
     * @param oldPriceList the price list including all agent prices
     * @param agentIndex the index of the self agent
     * @param indexesToRemove the indexes of other agent to be removed from
     * the price list
     * @return the price list, without the price of the self agent or any
     * of the agents whose indexes are in agentIndex.
     */
    private static List<Double> getPriceListWithout(
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
    private static List<Double> getValueListWithout(
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
}
