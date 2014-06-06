package coalitiongames;

import java.util.ArrayList;
import java.util.List;

abstract class DemandProblemGenerator {
    
    public static void main(final String[] args) {
        runSmallProblem();
    }
    
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
    
    private static void runProblem(
        final int n,
        final double valueRange,
        final double priceRange,
        final int kMax,
        final int kMin
    ) {
        final double basePrice = MipGeneratorGLPK.MIN_BUDGET / kMax;
        final double maxPrice = 
            MipGeneratorGLPK.MIN_BUDGET + MipGeneratorGLPK.MIN_BUDGET / n;
        
        final List<Double> prices = new ArrayList<Double>();
        for (int i = 1; i <= n; i++) {
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
        
        
        final double baseValue = 50.0;
        final List<Agent> agents = new ArrayList<Agent>();
        for (int i = 1; i <= n; i++) {
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
                MipGeneratorGLPK.MIN_BUDGET 
                + Math.random() * MipGeneratorGLPK.MIN_BUDGET / n;
            
            final int id = i;
            agents.add(new Agent(values, budget, id));
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
            DemandAnalyzer.getUnderDemand(demand, prices);
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
