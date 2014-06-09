package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public final class GammaZ3 implements GammaZ {
    
    private static final double EPSILON = 0.01;

    /*
     * Gamma3 = 1 / N
     * z3(p)_j = IURQ_j (1 + epsilon) - (OURQ_j + UND_j)
     */
    @Override
    public List<Double> z(
        final List<List<Integer>> demand, 
        final List<Double> prices,
        final int kMax, 
        final double maxPrice
    ) {
        if (MipGenerator.DEBUGGING) {
            for (Double price: prices) {
                if (price < 0 || price > maxPrice) {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        final List<Integer> incomingUnrequitedDemand = 
            DemandAnalyzer.getIncomingUnrequitedDemand(demand);
        final List<Integer> outgoingUnrequitedDemand =
            DemandAnalyzer.getOutgoingUnrequitedDemand(demand);
        final List<Integer> underDemand = 
            DemandAnalyzer.getUnderDemand(demand);
            
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < demand.size(); i++) {
            final double z3 = incomingUnrequitedDemand.get(i) * (1.0 + EPSILON) 
                - (outgoingUnrequitedDemand.get(i) + underDemand.get(i));
            result.add(z3);
        }
        
        return result;
    }

    @Override
    public List<Double> gammaZ(
        final  List<List<Integer>> demand, 
        final List<Double> prices,
        final int kMax, 
        final double maxPrice
    ) {
        final int n = demand.size();
        final double gamma1 = 1.0 / n;
        final List<Double> z = z(demand, prices, kMax, maxPrice);
        for (int i = 0; i < z.size(); i++) {
            z.set(i, z.get(i) * gamma1);
        }
        return z;
    }

    @Override
    public double worstCaseError(
        final int kMax, 
        final int n
    ) {
        // FIXME
        return Double.MAX_VALUE;
    }
}
