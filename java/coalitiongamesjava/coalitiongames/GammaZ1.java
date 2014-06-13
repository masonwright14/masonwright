package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public final class GammaZ1 implements GammaZ {

    private static final double EPSILON = 0.01;
    
    /**
     * Gamma1 = 1 / (N - 1)
     * z1(p)_j = theta_urq(p_j) URQ_j - theta_UND(p_j) UND_j
     * theta_urq(p_j) = p_j (2 - kMax) / ((kMax - 1) maxPrice) + 1
     * URQ_j = unrequited demand to/from agent j
     * theta_UND(p_j) = p_j (2 epsilon / maxPrice) + 1 - epsilon
     * UND_j = 1 if no other agent demand j, else 0, regardless of p_j
     * epsilon << 1
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
        
        final List<Integer> unrequitedDemand = 
            DemandAnalyzer.getUnrequitedDemand(demand);
        final List<Integer> underDemand = 
            DemandAnalyzer.getUnderDemand(demand);
        
        final List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < demand.size(); i++) {
            final double thetaUrq = thetaUrq(prices.get(i), kMax, maxPrice);
            final double thetaUnd = thetaUnd(prices.get(i), maxPrice);
            final double z1 = 
                thetaUrq * unrequitedDemand.get(i) 
                - thetaUnd * underDemand.get(i);
            result.add(z1);
        }
        
        return result;
    }
    
    @Override
    public List<Double> gammaZ(
        final List<List<Integer>> demand, 
        final List<Double> prices,
        final int kMax, 
        final double maxPrice
    ) {
        final int n = demand.size();
        final double gamma1 = 1.0 / (n - 1.0);
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
        return kMax * Math.sqrt(n);
    }

    /*
     * theta_urq(p_j) = p_j (2 - kMax) / ((kMax - 1) maxPrice) + 1
     */
    private double thetaUrq(
        final double price,
        final int kMax,
        final double maxPrice
    ) {
        return price * (2.0 - kMax) / ((kMax - 1.0) * maxPrice) + 1.0;
    }
    
    /*
     * theta_UND(p_j) = p_j (2 epsilon / maxPrice) + 1 - epsilon
     */
    private double thetaUnd(
        final double price,
        final double maxPrice
        
    ) {
        return price * (2.0 * EPSILON / maxPrice) + 1.0 - EPSILON;
    }
}
