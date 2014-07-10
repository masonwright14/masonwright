package regretexperiment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import coalitiongames.Agent;
import coalitiongames.DraftAllocation;
import coalitiongames.EachAgentDraftAllocation;
import coalitiongames.EachAgentDraftTabu;
import coalitiongames.EachDraftCaptainsChoice;
import coalitiongames.GammaZ;
import coalitiongames.GammaZ2;
import coalitiongames.RsdAllLevelsTabuSearch;
import coalitiongames.RsdAllocation;
import coalitiongames.RsdTabuAllSpitl;
import coalitiongames.RsdTabuSearch;
import coalitiongames.RsdUtil;
import coalitiongames.SimpleSearchResult;
import experiment.ProblemGenerator;
import experiment.ProblemGenerator.SearchAlgorithm;
import experiment.ProblemGenerator.SimpleSearchAlgorithm;
import experiment.ProblemGenerator.TabuSearchAlgorithm;
import experiment.SampleInputLoader;

public abstract class RegretProblemGenerator {

    
    public static RegretSearchResult getRegretSearchResult(
        final String fileName,
        final SearchAlgorithm algorithm,
        final int deviationsPerAgent
    ) {
        final List<Integer> rsdOrder = SampleInputLoader.getRsdOrder(fileName);
        final List<Double> budgets = SampleInputLoader.getBudgets(fileName);
        assert ProblemGenerator.checkRsdBudgets(budgets, rsdOrder);
        final int kMax = (int) Math.ceil(Math.sqrt(rsdOrder.size()));
        final int numberAgents = rsdOrder.size();
        
        final long problemStartMillis = new Date().getTime();

        assert deviationsPerAgent > 0;
        // don't use this method for randomized algorithms
        assert algorithm != SimpleSearchAlgorithm.RANDOM_ANY 
            && algorithm != SimpleSearchAlgorithm.RANDOM_OPT;
        
        final List<Agent> truthfulAgents = getAgents(fileName);
        final List<Double> truthfulTotalUtilities = 
            getTotalUtilities(truthfulAgents);
        final List<Integer> truthfulTotalUtilitiesNoJitter =
            getTotalUtilitiesNoJitter(truthfulAgents);
        final UtilityFractionData utilityFractionData = 
            getTruthfulUtilityFractionData(fileName, algorithm);
        final List<Double> truthfulUtilityFractions = 
            utilityFractionData.getUtilityFractions();
        final List<Double> truthfulUtilityFractionsNoJitter = 
            utilityFractionData.getUtilityFractionsNoJitter();
        final List<Integer> rsdIndexes = getRsdIndexes(rsdOrder);

        double maxRegretTruthFraction = 0.0;
        double maxRegretTruthFractionNoJitter = 0.0;
        int mostRegretAgentIndex = -1;
        int mostRegretAgentRsdIndex = -1;
        int agentsWithRegretFromTruth = 0;
        int agentsWithRegretFromTruthNoJitter = 0;
        double totalRegretTruthFraction = 0.0;
        final double tolerance = 0.001;
        for (int agentIndex = 0; agentIndex < numberAgents; agentIndex++) {
            final Agent selfAgent = truthfulAgents.get(agentIndex);
            final List<Double> truthfulSelfUtilities = selfAgent.getValues();
            final List<List<Double>> deviateUtilities =
                UtilityGenerator.getDeviateUtilityList(
                    deviationsPerAgent, 
                    truthfulSelfUtilities
                );
            
            double agentMaxRegretTruthFraction = 0.0;
            double agentMaxRegretTruthFractionNoJitter = 0.0;

            final double truthfulTotalUtility = 
                truthfulTotalUtilities.get(agentIndex);
            final double truthfulTotalUtilityNoJitter = 
                truthfulTotalUtilitiesNoJitter.get(agentIndex);
            final double truthfulFractionUtility = 
                truthfulUtilityFractions.get(agentIndex);
            final double truthfulFractionUtilityNoJitter = 
                truthfulUtilityFractionsNoJitter.get(agentIndex);
            for (
                int deviationIndex = 0; 
                deviationIndex < deviationsPerAgent; 
                deviationIndex++
            ) {
                /*
                 * Run with default agents, except for one deviate
                 * agent, with currentDeviateUtility, at position
                 * agentIndex.
                 * 
                 * We have:
                 * agents
                 * kMax
                 * rsdOrder
                 * 
                 * Get back:
                 * teamUtility # in terms of TRUTHFUL UTILITIES
                 * teamUtilityNoJitter # in terms of TRUTHFUL UTILITIES
                 * 
                 * you can't just take the teamUtility reported by the
                 * SearchResult, because that is not in terms of truthful
                 * utilities.
                 * 
                 * Find regrets:
                 * (teamUtility / truthfulTotalUtility) 
                 *      - truthfulFractionUtility
                 * (teamUtilityNoJitter / truthfulTotalUtilityNoJitter) 
                 *      - truthfulFractionUtilityNoJitter
                 * 
                 * Update if increased:
                 * agentMaxRegretTruthFraction
                 * agentMaxRegretTruthFractionNoJitter
                 */
                
                final List<Double> currentDeviateUtility = 
                    deviateUtilities.get(deviationIndex);
                final List<Agent> deviateAgents = getAgentsWithDeviate(
                    truthfulAgents, 
                    agentIndex, 
                    currentDeviateUtility
                );
                final UtilityDeviateData data = 
                    getDeviationUtility(
                        deviateAgents, 
                        kMax, 
                        rsdOrder, 
                        algorithm,
                        agentIndex,
                        selfAgent
                    );
                final double teamUtility = data.getUtility();
                final double teamUtilityNoJitter = data.getUtilityNoJitter();
                final double regretFraction =
                    (teamUtility / truthfulTotalUtility) 
                        - truthfulFractionUtility;
                final double regretFractionNoJitter =
                    (teamUtilityNoJitter / truthfulTotalUtilityNoJitter) 
                        - truthfulFractionUtilityNoJitter;
                if (regretFraction > agentMaxRegretTruthFraction) {
                    agentMaxRegretTruthFraction = regretFraction;
                }
                if (
                    regretFractionNoJitter 
                    > agentMaxRegretTruthFractionNoJitter
                ) {
                    agentMaxRegretTruthFractionNoJitter 
                        = regretFractionNoJitter;
                }
            }
            
            if (agentMaxRegretTruthFraction > tolerance) {
                agentsWithRegretFromTruth++;
            }
            if (agentMaxRegretTruthFractionNoJitter > tolerance) {
                agentsWithRegretFromTruthNoJitter++;
            }
            if (agentMaxRegretTruthFraction > maxRegretTruthFraction) {
                maxRegretTruthFraction = agentMaxRegretTruthFraction;
                mostRegretAgentIndex = agentIndex;
                mostRegretAgentRsdIndex = rsdIndexes.get(agentIndex);
            }
            if (
                agentMaxRegretTruthFractionNoJitter 
                    > maxRegretTruthFractionNoJitter
            ) {
                maxRegretTruthFractionNoJitter = 
                    agentMaxRegretTruthFractionNoJitter;
            }
            
            totalRegretTruthFraction += agentMaxRegretTruthFraction;
        }
        
        final double meanRegretFromTruthFraction = 
            totalRegretTruthFraction / numberAgents;
        final long durationMillis = 
            new Date().getTime() - problemStartMillis;
        
        final int numberOfTeams = 
            RsdUtil.getOptimalTeamSizeList(numberAgents, kMax).size();
        return new RegretSearchResult(
            maxRegretTruthFraction, 
            maxRegretTruthFractionNoJitter, 
            mostRegretAgentIndex, 
            mostRegretAgentRsdIndex, 
            agentsWithRegretFromTruth, 
            agentsWithRegretFromTruthNoJitter, 
            meanRegretFromTruthFraction, 
            deviationsPerAgent, 
            numberAgents, 
            durationMillis,
            numberOfTeams
        );
    }
    
    private static UtilityDeviateData getDeviationUtility(
        final List<Agent> agents,
        final int kMax,
        final List<Integer> rsdOrder,
        final SearchAlgorithm algorithm,
        final int agentIndex,
        final Agent truthfulSelfAgent
    ) {
        if (algorithm instanceof SimpleSearchAlgorithm) {
            return getDeviationUtilitySimple(
                agents, 
                kMax, 
                rsdOrder, 
                (SimpleSearchAlgorithm) algorithm,
                agentIndex,
                truthfulSelfAgent
            );
        }
            
        assert algorithm instanceof TabuSearchAlgorithm;
        return getDeviationUtilityTabu(
            agents, 
            kMax, 
            rsdOrder, 
            (TabuSearchAlgorithm) algorithm,
            agentIndex,
            truthfulSelfAgent
        );
    }
    
    private static UtilityDeviateData getDeviationUtilitySimple(
        final List<Agent> agents,
        final int kMax,
        final List<Integer> rsdOrder,
        final SimpleSearchAlgorithm algorithm,
        final int agentIndex,
        final Agent truthfulSelfAgent
    ) {
        SimpleSearchResult searchResult;
        final int kMin = 1;
        switch (algorithm) {
        case DRAFT:
            searchResult = 
                DraftAllocation.draftAllocation(
                    agents, kMax, rsdOrder
                );
            break;
        case EACH_DRAFT:
            searchResult = 
                EachAgentDraftAllocation.eachAgentDraftAllocation(
                    agents, kMax, rsdOrder
                );
            break;
        case EACH_DRAFT_CC:
            searchResult = 
                EachDraftCaptainsChoice.eachDraftCaptainsChoiceAllocation(
                    agents, kMax, rsdOrder
                );
            break;
        case RANDOM_ANY:
            throw new IllegalArgumentException("random algorithm");
        case RANDOM_OPT:
            throw new IllegalArgumentException("random algorithm");
        case RSD_GREEDY:
            searchResult = 
                RsdAllocation.rsdGreedySizesAllocation(
                    agents, kMax, kMin, rsdOrder
                );
            break;
        case RSD_OPT:
            searchResult = 
                RsdAllocation.rsdOptimalSizesAllocation(
                    agents, kMax, kMin, rsdOrder
                );
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        if (searchResult == null) {
            throw new IllegalStateException();
        }
        
        final double utility = 
            getTruthfulTeamUtility(
                agents, agentIndex, truthfulSelfAgent, searchResult
            );
        final int utilityNoJitter = 
            getTruthfulTeamUtilityNoJitter(
                agents, agentIndex, truthfulSelfAgent, searchResult
            );
        final UtilityDeviateData result = 
            new UtilityDeviateData(utility, utilityNoJitter);
        
        return result;
    }
    
    public static final double getTruthfulTeamUtility(
        final List<Agent> agents,
        final int agentIndex,
        final Agent truthfulSelfAgent,
        final SimpleSearchResult result
    ) {
        final List<Integer> team = result.getAllocation().get(agentIndex);
        double total = 0.0;
        for (int j = 0; j < agents.size(); j++) {
            if (agentIndex != j && team.get(j) == 1) {
                final UUID otherAgentUuid = agents.get(j).getUuid();
                total += truthfulSelfAgent.getValueByUUID(otherAgentUuid);
            }
        }

        return total;
    }
    
    public static final int getTruthfulTeamUtilityNoJitter(
        final List<Agent> agents,
        final int agentIndex,
        final Agent truthfulSelfAgent,
        final SimpleSearchResult result
    ) {
        final List<Integer> team = result.getAllocation().get(agentIndex);
        int total = 0;
        for (int j = 0; j < agents.size(); j++) {
            if (agentIndex != j && team.get(j) == 1) {
                final UUID otherAgentUuid = agents.get(j).getUuid();
                total += 
                    Math.floor(
                        truthfulSelfAgent.getValueByUUID(otherAgentUuid)
                    );
            }
        }

        return total;
    }
    
    private static UtilityDeviateData getDeviationUtilityTabu(
        final List<Agent> agents,
        final int kMax,
        final List<Integer> rsdOrder,
        final TabuSearchAlgorithm algorithm,
        final int agentIndex,
        final Agent truthfulSelfAgent
    ) {
        SimpleSearchResult searchResult;
        final GammaZ gammaZ = new GammaZ2();
        final int kMin = 1;
        switch (algorithm) {
        case TABU_ALL:
            searchResult = 
                RsdAllLevelsTabuSearch.rsdTabuSearchAllLevels(
                    agents, gammaZ, kMax, kMin, rsdOrder
                );
            break;
        case TABU_ALL_OPT:
            searchResult = 
                RsdAllLevelsTabuSearch.rsdTabuSearchAllLevelsOptimalSizes(
                    agents, gammaZ, kMax, rsdOrder
                );
            break;
        case TABU_EACH:
            searchResult = 
                EachAgentDraftTabu.eachAgentDraftTabu(agents, kMax, rsdOrder);
            break;
        case TABU_ONE:
            searchResult = 
                RsdTabuSearch.rsdTabuSearchOneLevel(
                    agents, gammaZ, kMax, rsdOrder
                );
            break;
        case TABU_ALL_OPT_SPITL:
            searchResult =
                RsdTabuAllSpitl.rsdTabuSearchAllLevelsOptimalSizesSpitl(
                    agents, gammaZ, kMax, rsdOrder
                );
            break;
        default:
            throw new IllegalArgumentException();
        }
        
        if (searchResult == null) {
            throw new IllegalStateException();
        }
        
        final double utility = 
            getTruthfulTeamUtility(
                agents, agentIndex, truthfulSelfAgent, searchResult
            );
        final int utilityNoJitter = 
            getTruthfulTeamUtilityNoJitter(
                agents, agentIndex, truthfulSelfAgent, searchResult
            );
        final UtilityDeviateData result = 
            new UtilityDeviateData(utility, utilityNoJitter);
        
        return result;
    }
    
    /**
     * @return a list of the turn number (0-based) of each agent.
     * for example, item 0 is the turn number of the first agent in
     * the agents list, item 1 is the turn number of the next agent
     * in the agents list, etc.
     */
    private static List<Integer> getRsdIndexes(
        final List<Integer> rsdOrder
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < rsdOrder.size(); i++) {
            result.add(rsdOrder.indexOf(i));
        }
        return result;
    }
    
    /*
     * Return a list of agents containing all the same Agents,
     * in the same order, as the truthfulAgents list,
     * except with a new Agent in position newAgentIndex,
     * whose utilities for other agents match newUtilities.
     */
    private static List<Agent> getAgentsWithDeviate(
        final List<Agent> truthfulAgents,
        final int newAgentIndex,
        final List<Double> newUtilities
    ) {
        assert !truthfulAgents.isEmpty();
        assert newAgentIndex >= 0 
            && newAgentIndex < truthfulAgents.size();
        assert newUtilities.size() == truthfulAgents.size() - 1;
        final List<Agent> result = new ArrayList<Agent>();
        
        for (int i = 0; i < truthfulAgents.size(); i++) {
            if (i == newAgentIndex) {
                // defecting agent. give this agent the
                // utilities in newUtilities.
                final Agent oldAgent = truthfulAgents.get(i);
                final List<UUID> agentIdsForValues = 
                    oldAgent.getAgentIdsForValues();
                final double budget = oldAgent.getBudget();
                final int id = oldAgent.getId();
                final UUID uuid = oldAgent.getUuid();
                final Agent newAgent = 
                    new Agent(
                        newUtilities, 
                        agentIdsForValues, 
                        budget, 
                        id, 
                        uuid
                    );
                result.add(newAgent);
            } else {
                // other agent. keep the same as before.
                result.add(truthfulAgents.get(i));
            }
        }
        
        assert result.size() == truthfulAgents.size();
        assert !truthfulAgents.get(newAgentIndex).getValues().equals(
            result.get(newAgentIndex).getValues()
        );
        return result;
    }
    
    private static List<Agent> getAgents(final String fileName) {
        final List<Agent> agents = new ArrayList<Agent>();

        final List<Integer> rsdOrder = SampleInputLoader.getRsdOrder(fileName);
        final List<Double> budgets = SampleInputLoader.getBudgets(fileName);
        assert ProblemGenerator.checkRsdBudgets(budgets, rsdOrder);
        final List<List<Double>> values = SampleInputLoader.getMatrix(fileName);
        final int n = rsdOrder.size();
        final List<UUID> uuids = ProblemGenerator.getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = 
                ProblemGenerator.getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        return agents;
    }
    
    private static List<Double> getTotalUtilities(
        final List<Agent> agents
    ) {
        final List<Double> result = new ArrayList<Double>();
        for (Agent agent: agents) {
            double total = 0.0;
            for (double value: agent.getValues()) {
                total += value;
            }
            result.add(total);
        }
        
        return result;
    }
    
    private static List<Integer> getTotalUtilitiesNoJitter(
        final List<Agent> agents
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (Agent agent: agents) {
            int total = 0;
            for (double value: agent.getValues()) {
                total += (int) Math.floor(value);
            }
            result.add(total);
        }
        
        return result;
    }
    
    private static UtilityFractionData getTruthfulUtilityFractionData(
        final String fileName,
        final SearchAlgorithm algorithm
    ) {
        final boolean isSimpleSearch = 
            algorithm instanceof SimpleSearchAlgorithm;
        if (isSimpleSearch) {
            SimpleSearchAlgorithm simpleAlgorithm = 
                (SimpleSearchAlgorithm) algorithm;
            final SimpleSearchResult simpleSearchResult = 
                ProblemGenerator.getSimpleSearchResult(
                    fileName, simpleAlgorithm
                );
            final List<Double> utilityFractions = 
                simpleSearchResult.getFractionsOfTotalUtility();
            final List<Double> utilityFractionsNoJitter = 
                simpleSearchResult.getFractionsOfTotalUtilityNoJitter();
            return new UtilityFractionData(
                utilityFractions, 
                utilityFractionsNoJitter
            );
        }
        
        TabuSearchAlgorithm tabuAlgorithm = 
            (TabuSearchAlgorithm) algorithm;
        final SimpleSearchResult searchResult =
            ProblemGenerator.getTabuSearchResult(
                fileName, tabuAlgorithm
            );
        final List<Double> utilityFractions = 
            searchResult.getFractionsOfTotalUtility();
        final List<Double> utilityFractionsNoJitter = 
            searchResult.getFractionsOfTotalUtilityNoJitter();
        return new UtilityFractionData(
            utilityFractions, 
            utilityFractionsNoJitter
        );
    }
    
    private static class UtilityDeviateData {
        private double utility;
        private int utilityNoJitter;
        
        public UtilityDeviateData(
            final double aUtility,
            final int aUtilityNoJitter
        ) {
            this.utility = aUtility;
            this.utilityNoJitter = aUtilityNoJitter;
        }

        public double getUtility() {
            return utility;
        }

        public int getUtilityNoJitter() {
            return utilityNoJitter;
        }
    }
    
    private static class UtilityFractionData {
        private List<Double> utilityFractions;
        private List<Double> utilityFractionsNoJitter;
        
        public UtilityFractionData(
            final List<Double> aUtilityFractions,
            final List<Double> aUtilityFractionsNoJitter
        ) {
            this.utilityFractions = 
                new ArrayList<Double>(aUtilityFractions);
            this.utilityFractionsNoJitter = 
                new ArrayList<Double>(aUtilityFractionsNoJitter);
        }

        public List<Double> getUtilityFractions() {
            return utilityFractions;
        }

        public List<Double> getUtilityFractionsNoJitter() {
            return utilityFractionsNoJitter;
        }
    }
}
