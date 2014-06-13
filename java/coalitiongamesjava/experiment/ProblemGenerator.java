package experiment;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import coalitiongames.Agent;
import coalitiongames.DraftAllocation;
import coalitiongames.GammaZ;
import coalitiongames.GammaZ2;
import coalitiongames.RandomAllocation;
import coalitiongames.RsdAllLevelsTabuSearch;
import coalitiongames.RsdAllocation;
import coalitiongames.RsdTabuSearch;
import coalitiongames.SearchResult;
import coalitiongames.SimpleSearchResult;

public abstract class ProblemGenerator {
    
    public static enum SimpleSearchAlgorithm {
        RANDOM_ANY, RANDOM_OPT, RSD_GREEDY, RSD_OPT, DRAFT
    }
    
    public static enum TabuSearchAlgorithm {
        TABU_ONE, TABU_ALL, TABU_ALL_OPT
    }
    
    public static void main(final String[] args) {
        /*
        final SimpleSearchResult result = 
            getSimpleSearchResult(
                "inputFiles/bkfrat_1.txt", 
                SimpleSearchAlgorithm.RSD_OPT
            );
        System.out.println(result);
        */
        
        final SearchResult result =
            getSearchResult(
                "inputFiles/newfrat_1.txt", 
                TabuSearchAlgorithm.TABU_ALL_OPT
            );
        System.out.println(result);
    }
    
    public static SimpleSearchResult getSimpleSearchResult(
        final String fileName,
        final SimpleSearchAlgorithm algorithm
    ) {
        final List<Integer> rsdOrder = SampleInputLoader.getRsdOrder(fileName);
        final List<Double> budgets = SampleInputLoader.getBudgets(fileName);
        final List<List<Double>> values = SampleInputLoader.getMatrix(fileName);
        final int kMax = (int) Math.ceil(Math.sqrt(rsdOrder.size()));
        return getSimpleSearchResult(
            rsdOrder,
            budgets,
            values,
            kMax,
            algorithm
        );
    }
    
    public static SearchResult getSearchResult(
        final String fileName,
        final TabuSearchAlgorithm algorithm
    ) {
        final List<Integer> rsdOrder = SampleInputLoader.getRsdOrder(fileName);
        final List<Double> budgets = SampleInputLoader.getBudgets(fileName);
        final List<List<Double>> values = SampleInputLoader.getMatrix(fileName);
        final int kMax = (int) Math.ceil(Math.sqrt(rsdOrder.size()));
        return getSearchResult(
            rsdOrder,
            budgets,
            values,
            kMax,
            algorithm
        );
    }
    
    public static SearchResult getSearchResult(
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax,
        final TabuSearchAlgorithm algorithm
    ) {
        switch (algorithm) {
        case TABU_ALL:
            return runTabuAllAllocation(rsdOrder, budgets, values, kMax);
        case TABU_ALL_OPT:
            return runTabuAllOptAllocation(rsdOrder, budgets, values, kMax);
        case TABU_ONE:
            return runTabuOneAllocation(rsdOrder, budgets, values, kMax);
        default:
            throw new IllegalArgumentException();
        }
    }
        
    public static SimpleSearchResult getSimpleSearchResult(
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax,
        final SimpleSearchAlgorithm algorithm
    ) {
        switch (algorithm) {
        case DRAFT:
            return runDraftAllocation(rsdOrder, budgets, values, kMax);
        case RANDOM_ANY:
            return runRandomAnyAllocation(budgets, values, kMax);
        case RANDOM_OPT:
            return runRandomOptAllocation(budgets, values, kMax);
        case RSD_GREEDY:
            return runRsdGreedyAllocation(rsdOrder, budgets, values, kMax);
        case RSD_OPT:
            return runRsdOptAllocation(rsdOrder, budgets, values, kMax);
        default:
            throw new IllegalArgumentException();
        }
    }
    
    public static SearchResult runTabuAllOptAllocation(
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax
    ) {
        final int n = rsdOrder.size();
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        final GammaZ gammaZ = new GammaZ2();
        final SearchResult searchResult = 
            RsdAllLevelsTabuSearch.rsdTabuSearchAllLevelsOptimalSizes(
                agents, gammaZ, kMax, rsdOrder
            );
        return searchResult;
    }
    
    public static SearchResult runTabuAllAllocation(
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax
    ) {
        final int n = rsdOrder.size();
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        final GammaZ gammaZ = new GammaZ2();
        final int kMin = 1;
        final SearchResult searchResult = 
            RsdAllLevelsTabuSearch.rsdTabuSearchAllLevels(
                agents, gammaZ, kMax, kMin, rsdOrder
            );
        return searchResult;
    }
    
    public static SearchResult runTabuOneAllocation(
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax
    ) {
        final int n = rsdOrder.size();
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        final GammaZ gammaZ = new GammaZ2();
        final SearchResult searchResult = 
            RsdTabuSearch.rsdTabuSearchOneLevel(
                agents, gammaZ, kMax, rsdOrder
            );
        return searchResult;
    }
    
    public static SimpleSearchResult runRsdOptAllocation(
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax
    ) {
        final int n = values.size();
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        final int kMin = 1;
        final SimpleSearchResult searchResult = 
            RsdAllocation.rsdOptimalSizesAllocation(
                agents, kMax, kMin, rsdOrder
            );
        return searchResult;
    }
    
    public static SimpleSearchResult runRsdGreedyAllocation(
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax
    ) {
        final int n = values.size();
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        final int kMin = 1;
        final SimpleSearchResult searchResult = 
            RsdAllocation.rsdGreedySizesAllocation(
                agents, kMax, kMin, rsdOrder
            );
        return searchResult;
    }
    
    public static SimpleSearchResult runRandomOptAllocation(
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax
    ) {
        final int n = values.size();
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        final SimpleSearchResult searchResult = 
            RandomAllocation.randomOptimalSizesAllocation(
                agents, kMax
            );
        return searchResult;
    }
    
    public static SimpleSearchResult runRandomAnyAllocation(
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax
    ) {
        final int n = values.size();
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        final int kMin = 1;
        final SimpleSearchResult searchResult = 
            RandomAllocation.randomAllocation(
                agents, kMax, kMin
            );
        return searchResult;
    }

    public static SimpleSearchResult runDraftAllocation(
        final List<Integer> rsdOrder,
        final List<Double> budgets,
        final List<List<Double>> values,
        final int kMax
    ) {
        final int n = rsdOrder.size();
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            final List<Double> agentValues = values.get(i);
            agentValues.remove(i); // remove -1.0 for own value.
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(
                    agentValues, subsetList, budgets.get(i), id, uuids.get(i)
                )
            );
        }
        
        final SimpleSearchResult searchResult = 
            DraftAllocation.draftAllocation(
                agents, kMax, rsdOrder
            );
        return searchResult;
    }
    
    private static List<UUID> getUuids(final int n) {
        final List<UUID> uuids = new ArrayList<UUID>();
        for (int i = 0; i < n; i++) {
            uuids.add(UUID.randomUUID());
        }
        return uuids;
    }
    
    private static List<UUID> getUuidsWithout(
        final List<UUID> original,
        final int toRemove
    ) {
        final List<UUID> result = new ArrayList<UUID>(original);
        result.remove(toRemove);
        return result;
    }
}
