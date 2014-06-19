package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public final class GammaZ4 implements GammaZ {

    /*
     * z_j = IURQ_j - kMin * UND_j
     * 
     * UND_j is in {0, 1, . . ., kMin - 1}.
     * UND_j = (kMin - 1) - # of other agents that demand j,
     * or 0 if the difference would be negative.
     * 
     * IURQ_j is in {0, 1, . . ., N - 1}, where N = # of agents.
     * IURQ_j = # agents that demand j, but j does not demand.
     */
    @Override
    public List<Double> z(
        final List<List<Integer>> demand, 
        final List<Double> prices,
        final int kMax, 
        final int kMin,
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
        final List<Integer> integerUnderDemand = 
            DemandAnalyzer.getIntegerUnderDemand(demand, kMin);
        
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < demand.size(); i++) {
            final double z4 = 
                incomingUnrequitedDemand.get(i) 
                - kMin * integerUnderDemand.get(i);
            result.add(z4);
        }
        
        return result;
    }

    @Override
    public List<Double> gammaZ(
        final List<List<Integer>> demand, 
        final List<Double> prices,
        final int kMax, 
        final int kMin,
        final double maxPrice
    ) {
        final int n = demand.size();
        final double gamma4 = 1.0 / n;
        final List<Double> z = z(demand, prices, kMax, kMin, maxPrice);
        for (int i = 0; i < z.size(); i++) {
            z.set(i, z.get(i) * gamma4);
        }
        return z;
    }

    @Override
    public double worstCaseError(final int kMax, final int n) {
        // TODO
        return 0;
    }

}
