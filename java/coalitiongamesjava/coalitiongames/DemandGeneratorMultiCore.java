package coalitiongames;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class DemandGeneratorMultiCore implements DemandGenerator {
    
    private final int numCores;
    private final ExecutorService executor;
    private static volatile DemandGeneratorMultiCore singleton;
    
    private DemandGeneratorMultiCore() {
        this.numCores = Runtime.getRuntime().availableProcessors();
        this.executor = Executors.newFixedThreadPool(this.numCores / 2);
    }
    
    public static DemandGeneratorMultiCore getDemandGenerator() {
        if (singleton == null) {
            singleton = new DemandGeneratorMultiCore();
        }
        return singleton;
    }

    @Override
    public List<List<Integer>> getAggregateDemand(
        final List<Agent> agents,
        final List<Double> prices, 
        final List<Integer> teamSizes, 
        final double maxPrice
    ) {
        assert prices.size() == agents.size();
        final List<Future<List<Integer>>> interimResults = 
            new ArrayList<Future<List<Integer>>>();
        
        // process Agents in order they are listed
        for (int i = 0; i < agents.size(); i++) {
            final Agent agent = agents.get(i);            
            
            // each agent should store the values of the (N - 1) other agents.
            assert agent.getValues().size() == agents.size() - 1;
            
            // don't pass the price of the self agent to the MIP solver
            final List<Double> iPrices = new ArrayList<Double>();
            for (int j = 0; j < prices.size(); j++) {
                if (j != i) {
                    iPrices.add(prices.get(j));
                }
            }
            
            final Callable<List<Integer>> callable =
                new MipRangeCallable(
                    agent.getValues(), iPrices, 
                    agent.getBudget(), teamSizes, maxPrice, i
                );
            final Future<List<Integer>> callableResult = 
                this.executor.submit(callable);
            interimResults.add(callableResult);
        }
        
        final List<List<Integer>> result = new ArrayList<List<Integer>>();

        for (final Future<List<Integer>> futureList: interimResults) {
            try {
                final List<Integer> listResult = futureList.get();
                result.add(listResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new IllegalStateException();
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
        
        return result;
    }

    @Override
    public List<List<Integer>> getAggregateDemand(
        final List<Agent> agents,
        final List<Double> prices, 
        final int kMax, 
        final int kMin, 
        final double maxPrice
    ) {
        assert prices.size() == agents.size();
        final List<Future<List<Integer>>> interimResults = 
            new ArrayList<Future<List<Integer>>>();
        // process Agents in order they are listed
        for (int i = 0; i < agents.size(); i++) {
            final Agent agent = agents.get(i);            
            
            // each agent should store the values of the (N - 1) other agents.
            assert agent.getValues().size() == agents.size() - 1;
            
            // don't pass the price of the self agent to the MIP solver
            final List<Double> iPrices = new ArrayList<Double>();
            for (int j = 0; j < prices.size(); j++) {
                if (j != i) {
                    iPrices.add(prices.get(j));
                }
            }
            
            final Callable<List<Integer>> callable =
                new MipCallable(
                    agent.getValues(), iPrices, 
                    agent.getBudget(), kMax, kMin, maxPrice, i
                );
            final Future<List<Integer>> callableResult = 
                this.executor.submit(callable);
            interimResults.add(callableResult);
        }

        final List<List<Integer>> result = new ArrayList<List<Integer>>();

        for (final Future<List<Integer>> futureList: interimResults) {
            try {
                final List<Integer> listResult = futureList.get();
                result.add(listResult);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new IllegalStateException();
            } catch (ExecutionException e) {
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }
        
        return result;
    }
    
    public static final class MipRangeCallable 
        implements Callable<List<Integer>> {
        
        private final List<Double> values;
        private final List<Double> prices;
        private final double budget;
        private final List<Integer> teamSizes;
        private final double maxPrice;
        private final int agentIndex;
        
        public MipRangeCallable(
            final List<Double> aValues,
            final List<Double> aPrices,
            final double aBudget,
            final List<Integer> aTeamSizes,
            final double aMaxPrice,
            final int aAgentIndex
        ) {
            this.values = aValues;
            this.prices = aPrices;
            this.budget = aBudget;
            this.teamSizes = aTeamSizes;
            this.maxPrice = aMaxPrice;
            this.agentIndex = aAgentIndex;
        }

        @Override
        public List<Integer> call() throws Exception {
            final MipGenerator mipGen = new MipGeneratorCPLEX();
            final MipResult mipResult = mipGen.getLpSolution(
                values, // values for (N - 1) other agents
                prices, // prices of (N - 1) other agents
                budget, 
                teamSizes, 
                maxPrice
            );
            
            final List<Integer> result = mipResult.getRoundedColumnValues();
            result.add(agentIndex, 1);
            return result;
        }
    }
    
    public static final class MipCallable implements Callable<List<Integer>> {
        
        private final List<Double> values;
        private final List<Double> prices;
        private final double budget;
        private final int kMax;
        private final int kMin;
        private final double maxPrice;
        private final int agentIndex;
        
        public MipCallable(
            final List<Double> aValues,
            final List<Double> aPrices,
            final double aBudget,
            final int aKMax,
            final int aKMin,
            final double aMaxPrice,
            final int aAgentIndex
        ) {
            this.values = aValues;
            this.prices = aPrices;
            this.budget = aBudget;
            this.kMax = aKMax;
            this.kMin = aKMin;
            this.maxPrice = aMaxPrice;
            this.agentIndex = aAgentIndex;
        }

        @Override
        public List<Integer> call() throws Exception {
            final MipGenerator mipGen = new MipGeneratorCPLEX();
            final MipResult mipResult = mipGen.getLpSolution(
                values, // values for (N - 1) other agents
                prices, // prices of (N - 1) other agents
                budget, 
                kMax, 
                kMin, 
                maxPrice
            );
            
            final List<Integer> result = mipResult.getRoundedColumnValues();
            result.add(agentIndex, 1);
            return result;
        }
    }
}
