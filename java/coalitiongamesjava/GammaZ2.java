package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public final class GammaZ2 implements GammaZ {

    private static final double EPSILON = 0.01;
    
    /*
     * z2(p)_j = theta_iurq(p_j) IURQ_j - UND_j
     * theta_iurq(p_j) = 1 + epsilon - (epsilon / maxPrice) p_j
     */
    @Override
    public List<Double> z(
        final List<List<Integer>> demand, 
        final List<Double> prices,
        final int kMax, 
        final double maxPrice
    ) {
        final List<Integer> incomingUnrequitedDemand = 
            DemandAnalyzer.getIncomingUnrequitedDemand(demand);
        final List<Integer> underDemand = DemandAnalyzer.getUnderDemand(
            demand, 
            prices
        );
        
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < demand.size(); i++) {
            final double thetaIurq = thetaIurq(prices.get(i), maxPrice);
            final double z2 = 
                thetaIurq * incomingUnrequitedDemand.get(i) 
                - underDemand.get(i);
            result.add(z2);
        }
        
        return result;
    }

    /*
     * gammaZ2 = 1 / N
     */
    @Override
    public List<Double> gammaZ(
        final List<List<Integer>> demand, 
        final List<Double> prices,
        final int kMax, 
        final double maxPrice
    ) {
        final int n = demand.size();
        final double gamma2 = 1.0 / n;
        final List<Double> z = z(demand, prices, kMax, maxPrice);
        for (int i = 0; i < z.size(); i++) {
            z.set(i, z.get(i) * gamma2);
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

    /*
     * theta_iurq(p_j) = 1 + epsilon - (epsilon / maxPrice) p_j
     */
    private double thetaIurq(
        final double price,
        final double maxPrice
    ) {
        return 1.0 + EPSILON - (EPSILON / maxPrice) * price;
    }
}
