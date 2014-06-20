package coalitiongames;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import coalitiongames.PriceWithError.PriceUpdateSource;

public abstract class TabuSearchEachDraft {

    /**
     * @param agents a list of all agents in the problem, not only the
     * "remaining" agents
     * @param gammaZ
     * @param captain the taken agent that will choose a new agent to join
     * its team
     * @param teams each team has one list, and each list has the indexes 
     * in "agents" of the agents currently on the team.
     * @param finalTeamSizes
     * @return
     */
    public static SearchResult tabuSearchEachDraftTakenCaptain(
        final List<Agent> agents,
        final GammaZ gammaZ,
        final Agent captain,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes
    ) {
        // there must be some free agent left.
        assert EachAgentDraftTabu.isFreeAgentLeft(teams, agents);
        // the captain is already taken (on a team)
        assert EachDraftHelper.isAgentTaken(teams, agents.indexOf(captain));
        
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        final int n = agents.size();
        final double maxPrice = 
            MipGenerator.MIN_BUDGET + MipGenerator.MIN_BUDGET / n;
        final double maxBudget = maxPrice;
        PriceWithError currentNode = 
            getInitialPriceWithErrorTakenCaptain(
                finalTeamSizes, agents, teams, maxPrice, gammaZ, captain
            );
        PriceWithError bestNode = currentNode;
        
        final List<Double> bestErrorValues = new ArrayList<Double>();
        final List<PriceUpdateSource> priceUpdateSources =
            new ArrayList<PriceUpdateSource>();
        final Queue<PriceWithError> tabuQueue = 
            new DropOutQueue<PriceWithError>(TabuSearch.DEFAULT_TABU_STEPS);
        if (MipGenerator.DEBUGGING) {
            System.out.println("Best error: " + bestNode.getErrorValue());
        }
        int step = 0;
        // stop searching if no error at best node
        while (bestNode.getErrorValue() > 0.0) {
            // add current node to tabu queue so it won't be revisited
            tabuQueue.add(currentNode);
            bestErrorValues.add(bestNode.getErrorValue());
            priceUpdateSources.add(currentNode.getPriceUpdateSource());
            step++;
            if (step > TabuSearch.DEFAULT_TABU_STEPS) {
                break;
            }
            // get neighbors of currentNode, sorted by increasing error
            final List<PriceWithError> sortedNeighbors = 
                NeighborGenEachTabu.sortedNeighbors(
                    currentNode.getPrices(), 
                    currentNode.getError(), 
                    maxPrice, 
                    agents, 
                    gammaZ,
                    teams,
                    finalTeamSizes,
                    captain
                );
            // check if any neighbor is not already in the tabu queue
            boolean newNeighborFound = false;
            for (final PriceWithError neighbor: sortedNeighbors) {
                // check if neighbor is not in the tabu queue
                if (!tabuQueue.contains(neighbor)) {
                    currentNode = neighbor;
                    // found a neighbor not already in the tabu queue
                    newNeighborFound = true;
                    if (MipGenerator.DEBUGGING) {
                        System.out.println("Step: " + step);
                        System.out.println(
                            "Current error: " + currentNode.getErrorValue()
                        );
                    }
                    if (
                        currentNode.getErrorValue() < bestNode.getErrorValue()
                    ) {
                        bestNode = currentNode;
                        if (MipGenerator.DEBUGGING) {
                            System.out.println(
                                "Best error: " + bestNode.getErrorValue()
                            );
                        }
                    }
                    // stop searching neighbors for best one not in tabu queue
                    break;
                }
            }
            if (!newNeighborFound) {
                // all neighbors are in the tabu queue
                break;
            }
            if (bestNode != currentNode && !tabuQueue.contains(bestNode)) {
                // no better neighbors found in last "queueLength" steps
                break;
            }
        }
        
        // search complete. return best node.
        
        final double similarity = 
            PreferenceAnalyzer.getMeanPairwiseCosineSimilarity(agents);
        
        final long searchDurationMillis = 
            new Date().getTime() - searchStartMillis;
        final int kMax = finalTeamSizes.get(finalTeamSizes.size() - 1);
        final int kMin = finalTeamSizes.get(0);
        final SearchResult result = new SearchResult(
            bestNode.getPrices(), 
            bestNode.getDemand(), 
            bestNode.getError(), 
            bestNode.getErrorValue(), 
            kMin, 
            kMax, 
            maxBudget, 
            agents,
            searchDurationMillis,
            null,
            bestErrorValues,
            priceUpdateSources,
            1,
            null,
            similarity
        );
        return result;
    }
    
    /**
     * @param agents a list of all agents in the problem, not only the
     * "remaining" agents
     * @param gammaZ
     * @param captain the free agent that will choose a team to join
     * @param teams each team has one list, and each list has the indexes 
     * in "agents" of the agents currently on the team.
     * @param finalTeamSizes
     * @return
     */
    public static SearchResult tabuSearchEachDraftFreeCaptain(
        final List<Agent> agents,
        final GammaZ gammaZ,
        final Agent captain,
        final List<List<Integer>> teams,
        final List<Integer> finalTeamSizes
    ) {
        // some team must have room to join.
        assert EachAgentDraftTabu.
            countTeamsWithSpace(teams, finalTeamSizes) > 0;
        // the captain is free, not taken
        assert !EachDraftHelper.isAgentTaken(teams, agents.indexOf(captain));
        
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        final int n = agents.size();
        final double maxPrice = 
            MipGenerator.MIN_BUDGET + MipGenerator.MIN_BUDGET / n;
        final double maxBudget = maxPrice;
        PriceWithError currentNode = 
            getInitialPriceWithErrorFreeCaptain(
                finalTeamSizes, agents, teams, maxPrice, gammaZ, captain
            );
        PriceWithError bestNode = currentNode;
        
        final List<Double> bestErrorValues = new ArrayList<Double>();
        final List<PriceUpdateSource> priceUpdateSources =
            new ArrayList<PriceUpdateSource>();
        final Queue<PriceWithError> tabuQueue = 
            new DropOutQueue<PriceWithError>(TabuSearch.DEFAULT_TABU_STEPS);
        if (MipGenerator.DEBUGGING) {
            System.out.println("Best error: " + bestNode.getErrorValue());
        }
        int step = 0;
        // stop searching if no error at best node
        while (bestNode.getErrorValue() > 0.0) {
            // add current node to tabu queue so it won't be revisited
            tabuQueue.add(currentNode);
            bestErrorValues.add(bestNode.getErrorValue());
            priceUpdateSources.add(currentNode.getPriceUpdateSource());
            step++;
            if (step > TabuSearch.DEFAULT_TABU_STEPS) {
                break;
            }
            // get neighbors of currentNode, sorted by increasing error
            final List<PriceWithError> sortedNeighbors = 
                NeighborGenEachTabu.sortedNeighbors(
                    currentNode.getPrices(), 
                    currentNode.getError(), 
                    maxPrice, 
                    agents, 
                    gammaZ,
                    teams,
                    finalTeamSizes,
                    captain
                );
            // check if any neighbor is not already in the tabu queue
            boolean newNeighborFound = false;
            for (final PriceWithError neighbor: sortedNeighbors) {
                // check if neighbor is not in the tabu queue
                if (!tabuQueue.contains(neighbor)) {
                    currentNode = neighbor;
                    // found a neighbor not already in the tabu queue
                    newNeighborFound = true;
                    if (MipGenerator.DEBUGGING) {
                        System.out.println("Step: " + step);
                        System.out.println(
                            "Current error: " + currentNode.getErrorValue()
                        );
                    }
                    if (
                        currentNode.getErrorValue() < bestNode.getErrorValue()
                    ) {
                        bestNode = currentNode;
                        if (MipGenerator.DEBUGGING) {
                            System.out.println(
                                "Best error: " + bestNode.getErrorValue()
                            );
                        }
                    }
                    // stop searching neighbors for best one not in tabu queue
                    break;
                }
            }
            if (!newNeighborFound) {
                // all neighbors are in the tabu queue
                break;
            }
            if (bestNode != currentNode && !tabuQueue.contains(bestNode)) {
                // no better neighbors found in last "queueLength" steps
                break;
            }
        }
        
        // search complete. return best node.
                
        final double similarity = 
            PreferenceAnalyzer.getMeanPairwiseCosineSimilarity(agents);
        
        final long searchDurationMillis = 
            new Date().getTime() - searchStartMillis;
        final int kMax = finalTeamSizes.get(finalTeamSizes.size() - 1);
        final int kMin = finalTeamSizes.get(0);
        final SearchResult result = new SearchResult(
            bestNode.getPrices(), 
            bestNode.getDemand(), 
            bestNode.getError(), 
            bestNode.getErrorValue(), 
            kMin, 
            kMax, 
            maxBudget, 
            agents,
            searchDurationMillis,
            null,
            bestErrorValues,
            priceUpdateSources,
            1,
            null,
            similarity
        );
        return result;
}  
    
    /**
     * @param kMax maximum agents per team, including self
     * @param kMin minimum agents per team, including self
     * @param agents a list of all agents with their budgets and preferences
     * @param maxPrice maximum price an agent can be given
     * @param gammaZ used to evaluate error of a given allocation
     * @return a price vector for the agents, along with the aggregate
     * demand it induces, the error according to gammaZ of that demand,
     * and the l2-norm of this error.
     */
    private static PriceWithError getInitialPriceWithErrorFreeCaptain(
        final List<Integer> finalTeamSizes,
        final List<Agent> agents,
        final List<List<Integer>> teams,
        final double maxPrice,
        final GammaZ gammaZ,
        final Agent captain
    ) {
        final List<Double> prices = new ArrayList<Double>();
        final int kMax = finalTeamSizes.get(finalTeamSizes.size() - 1);
        final int kMin = finalTeamSizes.get(0);
        final double basePrice = MipGenerator.MIN_BUDGET / kMax;
        for (int i = 1; i <= agents.size(); i++) {
            prices.add(basePrice);
        }
        final DemandGeneratorOneCFreeCaptain demandGen = 
            new DemandGeneratorOneCFreeCaptain();
        final List<List<Integer>> aggregateDemand = 
            demandGen.getAggregateDemandFreeCaptain(
                agents, 
                prices, 
                teams, 
                finalTeamSizes,
                maxPrice,
                captain
            );
        final List<Double> errorDemand = 
            gammaZ.z(aggregateDemand, prices, kMax, kMin, maxPrice);
        final double error = DemandAnalyzer.errorSizeDouble(errorDemand);
        return new PriceWithError(
            prices, errorDemand, aggregateDemand, 
            error, PriceUpdateSource.INITIAL
        );
    }
    
    /**
     * @param kMax maximum agents per team, including self
     * @param kMin minimum agents per team, including self
     * @param agents a list of all agents with their budgets and preferences
     * @param maxPrice maximum price an agent can be given
     * @param gammaZ used to evaluate error of a given allocation
     * @return a price vector for the agents, along with the aggregate
     * demand it induces, the error according to gammaZ of that demand,
     * and the l2-norm of this error.
     */
    private static PriceWithError getInitialPriceWithErrorTakenCaptain(
        final List<Integer> finalTeamSizes,
        final List<Agent> agents,
        final List<List<Integer>> teams,
        final double maxPrice,
        final GammaZ gammaZ,
        final Agent captain
    ) {
        final List<Double> prices = new ArrayList<Double>();
        final int kMax = finalTeamSizes.get(finalTeamSizes.size() - 1);
        final int kMin = finalTeamSizes.get(0);
        final double basePrice = MipGenerator.MIN_BUDGET / kMax;
        for (int i = 1; i <= agents.size(); i++) {
            prices.add(basePrice);
        }
        final DemandGeneratorOneCTakenCaptain demandGen = 
            new DemandGeneratorOneCTakenCaptain();
        final List<List<Integer>> aggregateDemand = 
            demandGen.getAggregateDemandTakenCaptain(
                agents, 
                prices, 
                teams, 
                finalTeamSizes,
                maxPrice,
                captain
            );
        final List<Double> errorDemand = 
            gammaZ.z(aggregateDemand, prices, kMax, kMin, maxPrice);
        final double error = DemandAnalyzer.errorSizeDouble(errorDemand);
        return new PriceWithError(
            prices, errorDemand, aggregateDemand, 
            error, PriceUpdateSource.INITIAL
        );
    }
}
