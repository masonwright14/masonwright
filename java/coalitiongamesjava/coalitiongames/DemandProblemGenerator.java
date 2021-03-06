package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import coalitiongames.MaxSocialWelfareAllocation.ProblemType;

public abstract class DemandProblemGenerator {
    
    public static void main(final String[] args) {
        // runSmallProblem();
        // runVerySmallTabuSearch();
        // runSmallTabuSearch();
        // runVerySmallRsdTabuSearch();
        // runSmallRsdTabuSearch();
        // runVerySmallTabuSearchRanges();
        // runSmallTabuSearchRanges();
        // runVerySmallRsdAllLevelsTabuSearch();
        // runSmallRsdAllLevelsTabuSearch();
        // runSmallRandomAllocation();
        // runSmallRandomOptimalSizeAllocation();
        // runSmallGreedyRsdAllocation();
        // runSmallOptimalRsdAllocation();
        // runSmallDraftAllocation();
        // runGrandCoalitionRsdTabuSearch();
        // runGrandCoalitionRsdAllLevelsTabuSearch();
        // runSmallRsdAllLevelsOptimalSizesTabuSearch();
        // runSmallEachAgentDraftAllocation();
        // runSmallEachDraftCaptainsChoice();
        // runVerySmallEachDraftTabu();
        // runSmallEachDraftTabu();
        // runVerySmallRsdTabuAllSpitlSearch();
        // runVerySmallMaxSocialWelfare();
        // runSmallMaxSocialWelfare();
        runMediumMaxSocialWelfare();
        // runTrickyMaxSocialWelfare();
    }
    
    @SuppressWarnings("unused")
    private static void runVerySmallTabuSearchRanges() {
        final int agents = 10;
        final int valueRange = 10;
        final List<Integer> teamSizeRange = new ArrayList<Integer>();
        final Integer[] myArr = {2, 4};
        teamSizeRange.addAll(Arrays.asList(myArr));
        
        final GammaZ gammaZ = new GammaZ2();
        runTabuSearchRange(
            agents, 
            valueRange, 
            teamSizeRange,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runSmallTabuSearchRanges() {
        final int agents = 20;
        final int valueRange = 10;
        final List<Integer> teamSizeRange = new ArrayList<Integer>();
        final Integer[] myArr = {2, 3, 5};
        teamSizeRange.addAll(Arrays.asList(myArr));
        final GammaZ gammaZ = new GammaZ2();
        runTabuSearchRange(
            agents, 
            valueRange, 
            teamSizeRange,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runSmallTabuSearch() {
        final int agents = 20;
        final int valueRange = 10;
        final int kMax = 5;
        final int kMin = 0;
        final GammaZ gammaZ = new GammaZ2();
        runTabuSearch(
            agents, 
            valueRange, 
            kMax, 
            kMin,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runVerySmallMaxSocialWelfare() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        runMaxSocialWelfare(agents, valueRange, kMax);
    }
    
    @SuppressWarnings("unused")
    private static void runSmallMaxSocialWelfare() {
        final int agents = 15;
        final int valueRange = 15;
        final int kMax = 5;
        runMaxSocialWelfare(agents, valueRange, kMax);
    }
    
    private static void runMediumMaxSocialWelfare() {
        final int agents = 20;
        final int valueRange = 20;
        final int kMax = 5;
        runMaxSocialWelfare(agents, valueRange, kMax);
    }

    @SuppressWarnings("unused")
    private static void runTrickyMaxSocialWelfare() {
        final int agents = 17;
        final int valueRange = 17;
        final int kMax = 5;
        runMaxSocialWelfare(agents, valueRange, kMax);
    }
    
    @SuppressWarnings("unused")
    private static void runVerySmallTabuSearch() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        final int kMin = 3;
        final GammaZ gammaZ = new GammaZ2();
        runTabuSearch(
            agents, 
            valueRange, 
            kMax, 
            kMin,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runVerySmallRsdTabuAllSpitlSearch() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        final GammaZ gammaZ = new GammaZ2();
        runRsdTabuAllSpitlSearch(
            agents, 
            valueRange, 
            kMax,
            gammaZ
        );
    }
    
    
    @SuppressWarnings("unused")
    private static void runVerySmallRsdAllLevelsTabuSearch() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        final int kMin = 1;
        final GammaZ gammaZ = new GammaZ2();
        runRsdAllLevelsTabuSearch(
            agents, 
            valueRange, 
            kMax, 
            kMin,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runSmallRsdAllLevelsTabuSearch() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        final int kMin = 2;
        final GammaZ gammaZ = new GammaZ2();
        runRsdAllLevelsTabuSearch(
            agents, 
            valueRange, 
            kMax, 
            kMin,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runSmallEachDraftTabu() {
        final int agents = 15;
        final int valueRange = 10;
        final int kMax = 6;
        runEachDraftTabu(
            agents, 
            valueRange, 
            kMax
        );
    }
    
    @SuppressWarnings("unused")
    private static void runVerySmallEachDraftTabu() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        runEachDraftTabu(
            agents, 
            valueRange, 
            kMax
        );
    }
    
    @SuppressWarnings("unused")
    private static void runSmallRsdAllLevelsOptimalSizesTabuSearch() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        final GammaZ gammaZ = new GammaZ2();
        runRsdAllLevelsOptimalSizesTabuSearch(
            agents, 
            valueRange, 
            kMax,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runGrandCoalitionRsdAllLevelsTabuSearch() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 10;
        final int kMin = 2;
        final GammaZ gammaZ = new GammaZ2();
        runRsdAllLevelsTabuSearch(
            agents, 
            valueRange, 
            kMax, 
            kMin,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runSmallRandomOptimalSizeAllocation() {
        final int agents = 20;
        final int kMax = 5;
        final int kMin = 3;
        runRandomAllocation(agents, kMax, kMin, true);
    }
    
    @SuppressWarnings("unused")
    private static void runSmallRandomAllocation() {
        final int agents = 20;
        final int kMax = 5;
        final int kMin = 3;
        runRandomAllocation(agents, kMax, kMin, false);
    }
    
    @SuppressWarnings("unused")
    private static void runSmallDraftAllocation() {
        final int agents = 20;
        final int valueRange = 10;
        final int kMax = 5;
        runDraftAllocation(agents, valueRange, kMax);
    }
    
    @SuppressWarnings("unused")
    private static void runSmallEachDraftCaptainsChoice() {
        final int agents = 20;
        final int valueRange = 10;
        final int kMax = 5;
        runEachAgentDraftAllocation(agents, valueRange, kMax, true);
    }
    
    @SuppressWarnings("unused")
    private static void runSmallEachAgentDraftAllocation() {
        final int agents = 20;
        final int valueRange = 10;
        final int kMax = 5;
        runEachAgentDraftAllocation(agents, valueRange, kMax, false);
    }
    
    @SuppressWarnings("unused")
    private static void runSmallGreedyRsdAllocation() {
        final int agents = 20;
        final int valueRange = 10;
        final int kMax = 5;
        final int kMin = 3;
        runRsdAllocation(agents, valueRange, kMax, kMin, true);
    }
    
    @SuppressWarnings("unused")
    private static void runSmallOptimalRsdAllocation() {
        final int agents = 20;
        final int valueRange = 10;
        final int kMax = 5;
        final int kMin = 3;
        runRsdAllocation(agents, valueRange, kMax, kMin, false);
    }

    
    @SuppressWarnings("unused")
    private static void runGrandCoalitionRsdTabuSearch() {
        final int agents = 20;
        final int valueRange = 10;
        final int kMax = 20;
        final GammaZ gammaZ = new GammaZ2();
        runRsdTabuSearch(
            agents, 
            valueRange, 
            kMax, 
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runSmallRsdTabuSearch() {
        final int agents = 20;
        final int valueRange = 10;
        final int kMax = 5;
        final GammaZ gammaZ = new GammaZ2();
        runRsdTabuSearch(
            agents, 
            valueRange, 
            kMax, 
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runVerySmallRsdTabuSearch() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        final GammaZ gammaZ = new GammaZ2();
        runRsdTabuSearch(
            agents, 
            valueRange, 
            kMax,
            gammaZ
        );
    }
    
    @SuppressWarnings("unused")
    private static void runSmallProblem() {
        final int agents = 20;
        final int valueRange = 10;
        final int priceRange = 4;
        final int kMax = 5;
        final int kMin = 0;
        runProblem(
            agents, 
            valueRange, 
            priceRange, 
            kMax, 
            kMin
        );
    }
    
    public static List<UUID> getUuids(final int n) {
        final List<UUID> uuids = new ArrayList<UUID>();
        for (int i = 0; i < n; i++) {
            uuids.add(UUID.randomUUID());
        }
        return uuids;
    }
    
    public static List<UUID> getUuidsWithout(
        final List<UUID> original,
        final int toRemove
    ) {
        final List<UUID> result = new ArrayList<UUID>(original);
        result.remove(toRemove);
        return result;
    }
    
    private static void runEachDraftTabu(
        final int n,
        final double valueRange,
        final int kMax
    ) {
        final List<Integer> rsdOrder = 
            RsdUtil.getShuffledNumberList(n);
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        final List<Double> budgets = new ArrayList<Double>();
        for (int i = 0; i < n; i++) {
            final double budget =
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            budgets.add(budget);
        }
        
        Collections.sort(budgets);
        Collections.reverse(budgets);
        final List<Double> sortedBudgets = new ArrayList<Double>(budgets);
        budgets.clear();
        for (int i = 0; i < n; i++) {
            budgets.add(sortedBudgets.get(rsdOrder.indexOf(i)));
        }
        
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(values, subsetList, budgets.get(i), id, uuids.get(i))
            );
        }
        
        final SimpleSearchResult searchResult = 
            EachAgentDraftTabu.eachAgentDraftTabu(agents, kMax, rsdOrder);
        System.out.println(searchResult.toString());
    }
    
    private static void runRsdAllLevelsOptimalSizesTabuSearch(
        final int n,
        final double valueRange,
        final int kMax,
        final GammaZ gammaZ
    ) {
        final List<Integer> rsdOrder = 
            RsdUtil.getShuffledNumberList(n);
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        final List<Double> budgets = new ArrayList<Double>();
        for (int i = 0; i < n; i++) {
            final double budget =
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            budgets.add(budget);
        }
        
        Collections.sort(budgets);
        Collections.reverse(budgets);
        final List<Double> sortedBudgets = new ArrayList<Double>(budgets);
        budgets.clear();
        for (int i = 0; i < n; i++) {
            budgets.add(sortedBudgets.get(rsdOrder.indexOf(i)));
        }
        
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(values, subsetList, budgets.get(i), id, uuids.get(i))
            );
        }
        
        final SearchResult searchResult = 
            RsdAllLevelsTabuSearch.rsdTabuSearchAllLevelsOptimalSizes(
                agents, 
                gammaZ, 
                kMax,
                rsdOrder
            );
        System.out.println(searchResult.toString());
    }
    
    private static void runRsdTabuAllSpitlSearch(
        final int n,
        final double valueRange,
        final int kMax,
        final GammaZ gammaZ
    ) {
        final List<Integer> rsdOrder = 
            RsdUtil.getShuffledNumberList(n);
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        final List<Double> budgets = new ArrayList<Double>();
        for (int i = 0; i < n; i++) {
            final double budget =
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            budgets.add(budget);
        }
        
        // if budgets should be in rsdOrder, sort them first high to low,
        // then iterate over the agents, picking out that agent's budget
        // based on the agent's rsdOrder.
        Collections.sort(budgets);
        Collections.reverse(budgets);
        final List<Double> sortedBudgets = new ArrayList<Double>(budgets);
        budgets.clear();
        for (int i = 0; i < n; i++) {
            budgets.add(sortedBudgets.get(rsdOrder.indexOf(i)));
        }
        
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(values, subsetList, budgets.get(i), id, uuids.get(i))
            );
        }
        
        final SearchResult searchResult = 
            RsdTabuAllSpitl.rsdTabuSearchAllLevelsOptimalSizesSpitl(
                agents, 
                gammaZ, 
                kMax,
                rsdOrder
            );
        System.out.println(searchResult.toString());
    }
    
    private static void runRsdAllLevelsTabuSearch(
        final int n,
        final double valueRange,
        final int kMax,
        final int kMin,
        final GammaZ gammaZ
    ) {
        final List<Integer> rsdOrder = 
            RsdUtil.getShuffledNumberList(n);
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        final List<Double> budgets = new ArrayList<Double>();
        for (int i = 0; i < n; i++) {
            final double budget =
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            budgets.add(budget);
        }
        
        // if budgets should be in rsdOrder, sort them first high to low,
        // then iterate over the agents, picking out that agent's budget
        // based on the agent's rsdOrder.
        Collections.sort(budgets);
        Collections.reverse(budgets);
        final List<Double> sortedBudgets = new ArrayList<Double>(budgets);
        budgets.clear();
        for (int i = 0; i < n; i++) {
            budgets.add(sortedBudgets.get(rsdOrder.indexOf(i)));
        }
        
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(
                new Agent(values, subsetList, budgets.get(i), id, uuids.get(i))
            );
        }
        
        final SearchResult searchResult = 
            RsdAllLevelsTabuSearch.rsdTabuSearchAllLevels(
                agents, 
                gammaZ, 
                kMax,
                kMin,
                rsdOrder
            );
        System.out.println(searchResult.toString());
    }
    
    private static void runEachAgentDraftAllocation(
        final int n,
        final double valueRange,
        final int kMax,
        final boolean isCaptainsChoice
    ) {
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        final double baseValue = 50.0;
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(values, subsetList, budget, id, uuids.get(i)));
        }
        
        final List<Integer> rsdOrder = RsdUtil.getShuffledNumberList(n);
        if (isCaptainsChoice) {
            final SimpleSearchResult searchResult = 
                EachDraftCaptainsChoice.eachDraftCaptainsChoiceAllocation(
                    agents, kMax, rsdOrder
                );
            System.out.println(searchResult.toString());            
        } else {
            final SimpleSearchResult searchResult = 
                EachAgentDraftAllocation.eachAgentDraftAllocation(
                    agents, kMax, rsdOrder
                );
            System.out.println(searchResult.toString()); 
        }
    }
    
    private static void runDraftAllocation(
        final int n,
        final double valueRange,
        final int kMax
    ) {
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        final double baseValue = 50.0;
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(values, subsetList, budget, id, uuids.get(i)));
        }
        
        final List<Integer> rsdOrder = RsdUtil.getShuffledNumberList(n);
        final SimpleSearchResult searchResult = 
            DraftAllocation.draftAllocation(
                agents, kMax, rsdOrder
            );
        System.out.println(searchResult.toString()); 
    }
    
    private static void runRsdAllocation(
        final int n,
        final double valueRange,
        final int kMax,
        final int kMin,
        final boolean isGreedy
    ) {
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        final double baseValue = 50.0;
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(values, subsetList, budget, id, uuids.get(i)));
        }
        
        final List<Integer> rsdOrder = RsdUtil.getShuffledNumberList(n);
        if (isGreedy) {
            final SimpleSearchResult searchResult = 
                RsdAllocation.rsdGreedySizesAllocation(
                    agents, kMax, kMin, rsdOrder
                );
            System.out.println(searchResult.toString()); 
        } else {
            final SimpleSearchResult searchResult = 
                RsdAllocation.rsdOptimalSizesAllocation(
                    agents, kMax, kMin, rsdOrder
                );
            System.out.println(searchResult.toString()); 
        }
    }
    
    private static void runRandomAllocation(
        final int n,
        final int kMax,
        final int kMin,
        final boolean optimalSize
    ) {
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                final double newValue = 10;
                values.add(newValue);
            }
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(values, subsetList, budget, id, uuids.get(i)));
        }
        if (optimalSize) {
            final SimpleSearchResult searchResult = 
                RandomAllocation.randomOptimalSizesAllocation(agents, kMax);
            System.out.println(searchResult.toString());            
        } else {
            final SimpleSearchResult searchResult = 
                RandomAllocation.randomAllocation(agents, kMax, kMin);
            System.out.println(searchResult.toString());
        }
    }
    
    private static void runRsdTabuSearch(
        final int n,
        final double valueRange,
        final int kMax,
        final GammaZ gammaZ
    ) {
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(values, subsetList, budget, id, uuids.get(i)));
        }
        
        final List<Integer> rsdOrder = 
            RsdUtil.getShuffledNumberList(agents.size());
        
        final SearchResult searchResult = 
            RsdTabuSearch.rsdTabuSearchOneLevel(
                agents, 
                gammaZ, 
                kMax,
                rsdOrder
            );
        System.out.println(searchResult.toString());
    }
    
    private static void runMaxSocialWelfare(
        final int n,
        final double valueRange,
        final int kMax
    ) {
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final List<Double> normalizedValues = 
                MaxSocialWelfareAllocation.normalizeUtility(values);
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(normalizedValues, subsetList, budget, id, uuids.get(i)));
        }
        
        final List<Integer> rsdOrder = 
            RsdUtil.getShuffledNumberList(n);
        final SimpleSearchResult searchResult = 
            MaxSocialWelfareAllocation.
                maxSocialWelfareAllocation(agents, kMax, rsdOrder, ProblemType.HARD);
        System.out.println(searchResult.toString());
    }
    
    private static void runTabuSearchRange(
        final int n,
        final double valueRange,
        final List<Integer> teamSizes,
        final GammaZ gammaZ
    ) {
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(values, subsetList, budget, id, uuids.get(i)));
        }
        
        final SearchResult searchResult = 
            TabuSearch.tabuSearchRanges(
                agents, 
                gammaZ, 
                teamSizes
            );
        System.out.println(searchResult.toString());
    }

    private static void runTabuSearch(
        final int n,
        final double valueRange,
        final int kMax,
        final int kMin,
        final GammaZ gammaZ
    ) {
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(values, subsetList, budget, id, uuids.get(i)));
        }
        
        final SearchResult searchResult = 
            TabuSearch.tabuSearch(
                agents, 
                gammaZ, 
                kMax, 
                kMin
            );
        System.out.println(searchResult.toString());
    }
    
    private static void runProblem(
        final int n,
        final double valueRange,
        final double priceRange,
        final int kMax,
        final int kMin
    ) {
        final double basePrice = MipGenerator.MIN_BUDGET / kMax;
        final double maxPrice = 
            MipGenerator.MIN_BUDGET + MipGenerator.MIN_BUDGET / n;
        
        final List<Double> prices = new ArrayList<Double>();
        for (int i = 0; i < n; i++) {
            double newPrice = 
                basePrice + Math.random() * priceRange - priceRange / 2.0;
            if (newPrice < 0) {
                newPrice = 0;
            }
            if (newPrice > maxPrice) {
                newPrice = maxPrice;
            }
            prices.add(newPrice);
        }
        
        
        final List<Agent> agents = new ArrayList<Agent>();
        final List<UUID> uuids = getUuids(n);
        for (int i = 0; i < n; i++) {
            List<Double> values = new ArrayList<Double>();
            final double baseValue = 50.0;
            for (int j = 1; j < n; j++) {
                double newValue = 
                    baseValue + Math.random() * valueRange - valueRange / 2.0;
                if (newValue < 0) {
                    newValue = 0;
                }
                values.add(newValue);
            }
            
            final double budget = 
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / n;
            
            final List<UUID> subsetList = getUuidsWithout(uuids, i);
            final int id = i;
            agents.add(new Agent(values, subsetList, budget, id, uuids.get(i)));
        }
        
        final DemandGenerator demandGen = 
            DemandGeneratorMultiCore.getDemandGenerator();
        final List<List<Integer>> demand = demandGen.getAggregateDemand(
            agents, 
            prices, 
            kMax, 
            kMin, 
            maxPrice
        );
        
        Util.printDemandAsMatrix(demand);
        
        final List<Integer> underDemand = 
            DemandAnalyzer.getUnderDemand(demand);
        System.out.println("Under-demand:\n" + underDemand);
        
        final List<Integer> unrequitedDemand = 
            DemandAnalyzer.getUnrequitedDemand(demand);
        System.out.println("Unrequited demand:\n" + unrequitedDemand);
        
        GammaZ gammaZ = new GammaZ1();
        final List<Double> z1 = 
            gammaZ.z(demand, prices, kMax, 1, maxPrice);
        System.out.println("Z1:\n" + z1);
        double errorSize = DemandAnalyzer.errorSizeDouble(z1);
        System.out.println("Error size: " + errorSize);
        final double worstCaseErrorZ1 = gammaZ.worstCaseError(kMax, n);
        System.out.println("Error bound: " + worstCaseErrorZ1);
        
        gammaZ = new GammaZ2();
        final List<Double> z2 =
            gammaZ.z(demand, prices, kMax, 1, maxPrice);
        System.out.println("Z2:\n" + z2);
        errorSize = DemandAnalyzer.errorSizeDouble(z2);
        System.out.println("Error size: " + errorSize);
        
        gammaZ = new GammaZ3();
        final List<Double> z3 =
            gammaZ.z(demand, prices, kMax, 1, maxPrice);
        System.out.println("Z3:\n" + z3);
        errorSize = DemandAnalyzer.errorSizeDouble(z3);
        System.out.println("Error size: " + errorSize);
    }
}
