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
     * this result is returned. Else, the RSD order is used.
     * The first agent chooses its favorite affordable bundle
     * of teammates that leaves a feasible number left, 
     * and all those teammates are "out" of consideration and
     * finally allocated to this team. the next agent in RSD order chooses
     * its favorite affordable bundle of the remaining agents that leaves
     * a feasible number of remaining agents, and all those agents are "out"
     * and finally allocated to this team. this is repeated until all agents
     * are assigned to teams.
     * 
     * One-level RSD search cannot take a kMin variable, because there is
     * no guarantee the algorithm will find a solution if there is a minimum
     * number of players per team. 
     * The last agent to choose a team, for example, 
     * may not be able to afford any remaining bundle of size kMin or greater.
     * 
     * @param agents a list of all agents with their budgets and preferences
     * @param gammaZ an error function to use for updating prices
     * @param kMax maximum agents per team, including self
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
        final List<Integer> rsdOrder
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        assert rsdOrder.size() == agents.size();
        assert gammaZ != null;
        final int n = agents.size();
        assert kMax <= n;
        
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        // initialResult sets the prices of all agents. if it has market
        // clearing error, not all agents 
        // will get their allocations from this result.
        final SearchResult initialResult = TabuSearch.tabuSearch(
            agents, gammaZ, kMax, 0);
        
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
                        throw new IllegalArgumentException(
                            "Missing rsdOrder index: " + i
                        );
                    }
                }
            }
                        
            // get count of agents that have not yet been assigned to a team.
            final int agentsLeft = agents.size() - takenAgentIndexes.size();
            assert agentsLeft > 0;

            /*
             * check if agent can be allocated its favorite 
             * affordable bundle from initialResult.
             * -- can't demand any agent that is already "taken"
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

            assert !takenAgentIndexes.contains(agentIndex);
            
            // initialDemand demand for this agent was not feasible.
            
            final List<Integer> newAgentDemand = new ArrayList<Integer>();
            // if this is the only agent left, don't bother with tabu search
            if (takenAgentIndexes.size() == agents.size() - 1) {
                for (int i = 0; i < agents.size(); i++) {
                    if (i == agentIndex) {
                        newAgentDemand.add(1);
                    } else {
                        newAgentDemand.add(0);
                    }
                }
            } else {  
                 /* set up MIP over remaining agents, for the current agent
                 * -- prices and budgets are the same as in the initialSolution
                 * -- remove any agents already "taken" from the MIP
                 * number
                 * assign the given bundle to the agent and remove its 
                 * contents from consideration.    
                 */
                final Agent currentAgent = agents.get(agentIndex);
                final List<Double> values = RsdUtil.getValueListWithout(
                    currentAgent.getValues(), agentIndex, takenAgentIndexes
                );
                final List<Double> prices = RsdUtil.getPriceListWithout(
                    initialResult.getPrices(), agentIndex, takenAgentIndexes
                );
                final MipGenerator mipGenerator = new MipGeneratorGLPK();
                final double maxPrice = MipGenerator.MIN_BUDGET 
                    + MipGenerator.MIN_BUDGET / agents.size();
                final MipResult mipResult = mipGenerator.getLpSolution(
                    values, prices, currentAgent.getBudget(), 
                    kMax, 0, maxPrice
                );
                newAgentDemand.addAll(mipResult.getRoundedColumnValues());
                
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
        final List<List<Integer>> resultAllocation = 
            RsdUtil.getAllocation(allocation);
        final SearchResult result = new SearchResult(
            initialResult.getPrices(), resultAllocation, error, 
            errorSize, 0, kMax, maxBudget, agents, searchDurationMillis,
            rsdOrder, initialResult.getBestErrorValues(),
            initialResult.getPriceUpdateSources()
        );
        return result;
    }
}
