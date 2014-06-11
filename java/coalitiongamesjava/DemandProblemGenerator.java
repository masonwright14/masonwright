package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

abstract class DemandProblemGenerator {
    
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
        runSmallRandomAllocation();
        runSmallRandomOptimalSizeAllocation();
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
    private static void runVerySmallRsdAllLevelsTabuSearch() {
        final int agents = 10;
        final int valueRange = 10;
        final int kMax = 4;
        final int kMin = 0;
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
    
    private static void runSmallRandomOptimalSizeAllocation() {
        final int agents = 20;
        final int kMax = 5;
        final int kMin = 3;
        runRandomAllocation(agents, kMax, kMin, true);
    }
    
    private static void runSmallRandomAllocation() {
        final int agents = 20;
        final int kMax = 5;
        final int kMin = 3;
        runRandomAllocation(agents, kMax, kMin, false);
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
    
    private static void runRsdAllLevelsTabuSearch(
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
        
        final List<Integer> rsdOrder = 
            RsdUtil.getShuffledNumberList(agents.size());
        
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
        
        final List<List<Integer>> demand = DemandGenerator.getAggregateDemand(
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
            gammaZ.z(demand, prices, kMax, maxPrice);
        System.out.println("Z1:\n" + z1);
        double errorSize = DemandAnalyzer.errorSizeDouble(z1);
        System.out.println("Error size: " + errorSize);
        final double worstCaseErrorZ1 = gammaZ.worstCaseError(kMax, n);
        System.out.println("Error bound: " + worstCaseErrorZ1);
        
        gammaZ = new GammaZ2();
        final List<Double> z2 =
            gammaZ.z(demand, prices, kMax, maxPrice);
        System.out.println("Z2:\n" + z2);
        errorSize = DemandAnalyzer.errorSizeDouble(z2);
        System.out.println("Error size: " + errorSize);
        
        gammaZ = new GammaZ3();
        final List<Double> z3 =
            gammaZ.z(demand, prices, kMax, maxPrice);
        System.out.println("Z3:\n" + z3);
        errorSize = DemandAnalyzer.errorSizeDouble(z3);
        System.out.println("Error size: " + errorSize);
    }
}
