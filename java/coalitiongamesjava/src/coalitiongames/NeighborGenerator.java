package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public abstract class NeighborGenerator {
    
    public static List<List<Double>> neighbors(
        final List<Double> prices,
        final List<Double> z,
        final List<Double> stepSizesUnilateral,
        final List<Double> stepSizesGradient,
        final double maxPrice
    ) {
        final List<List<Double>> result = new ArrayList<List<Double>>();
        final List<List<Double>> unilateralNeighbors = unilateralNeighbors(
            prices, 
            z, 
            stepSizesUnilateral, 
            maxPrice
        );
        final List<List<Double>> gradientNeighbors = gradientNeighbors(
            prices, 
            z, 
            stepSizesGradient, 
            maxPrice
        );
        result.addAll(unilateralNeighbors);
        result.addAll(gradientNeighbors);
        return result;
    }
    
    public static List<List<Double>> unilateralNeighbors(
        final List<Double> prices,
        final List<Double> z,
        final List<Double> stepSizesUnilateral,
        final double maxPrice
    ) {
        assert maxPrice >= 0;
        assert prices.size() == z.size();
        
        if (MipGenerator.DEBUGGING) {
            for (Double price: prices) {
                if (price < 0 || price > maxPrice) {
                    throw new IllegalArgumentException();
                }
            }
            
            for (Double stepSize: stepSizesUnilateral) {
                if (stepSize <= 0 || stepSize > maxPrice) {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        final List<List<Double>> result = new ArrayList<List<Double>>();
        
        // iterate over all agents, handling the current agent's unilateral
        // price changes.
        for (int i = 0; i < prices.size(); i++) {
            // if the agent is under-demanded (price should be reduced)
            if (z.get(i) < 0) {
                // add 1 new price vector to the neighbors, 
                // where this agent's price is 0
                final List<Double> newPrices = new ArrayList<Double>();
                for (int j = 0; j < prices.size(); j++) {
                    if (j == i) {
                        newPrices.add(0.0);
                    } else {
                        newPrices.add(prices.get(j));
                    }
                }
                
                result.add(newPrices);
            
                // else if the agent is over-demanded (price should be raised)
            } else if (z.get(i) > 0) {
                // for each step size, raise the agent's price by that amount
                // and add the resulting price vector to the neighbors
                for (final Double stepSize: stepSizesUnilateral) {
                    final List<Double> newPrices = new ArrayList<Double>();
                    for (int j = 0; j < prices.size(); j++) {
                        if (j == i) {
                            double newPrice = prices.get(i) + stepSize;
                            // keep prices in [0, maxPrice]
                            if (newPrice > maxPrice) {
                                newPrice = maxPrice;
                            }
                            newPrices.add(newPrice);
                        } else {
                            newPrices.add(prices.get(j));
                        }
                    }
                    
                    result.add(newPrices);
                }
            }
        }
        
        return result;
    }

    /**
     * 
     * @param prices current prices of the N agents, in order.
     * these prices should already by in [0, maxPrice].
     * @param z error gradient to be used for update step. from
     * some GammaZ.z() function.
     * @param stepSizesGradient list of sizes of steps to be taken, based
     * on L2-norm. z will be converted to a unit vector, multiplied
     * by each of these values in turn, and added to the current prices.
     * @param maxPrice maximum allowed price for an item
     * @return list of updated prices, where each price is a list of doubles
     * of length N (the same as the length of the input list "prices"), and
     * the number of price lists equals the number of entries in "stepSizes".
     * updated prices will be truncated to [0, maxPrice] before returning.
     */
    public static List<List<Double>> gradientNeighbors(
        final List<Double> prices,
        final List<Double> z,
        final List<Double> stepSizesGradient,
        final double maxPrice
    ) {
        assert maxPrice >= 0;
        assert prices.size() == z.size();
        
        if (MipGenerator.DEBUGGING) {
            for (Double price: prices) {
                if (price < 0 || price > maxPrice) {
                    throw new IllegalArgumentException();
                }
            }
            
            for (Double stepSize: stepSizesGradient) {
                if (stepSize <= 0 || stepSize > maxPrice) {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        final List<Double> unitVectorZ = unitVectorZ(z);
        
        final List<List<Double>> result = new ArrayList<List<Double>>();
        for (final Double stepSize: stepSizesGradient) {
            List<Double> newPrices = new ArrayList<Double>();
            for (int i = 0; i < prices.size(); i++) {
                double newPrice = 
                    prices.get(i) + stepSize * unitVectorZ.get(i);
                // keep prices in [0, maxPrice]
                if (newPrice < 0.0) {
                    newPrice = 0.0;
                }
                if (newPrice > maxPrice) {
                    newPrice = maxPrice;
                }
                newPrices.add(newPrice);
            }
            
            result.add(newPrices);
        }
        
        return result;
    }
    
    private static List<Double> unitVectorZ(final List<Double> z) {
        final double l2Norm = DemandAnalyzer.errorSizeDouble(z);
        final List<Double> result = new ArrayList<Double>();
        for (final Double item: z) {
            result.add(item / l2Norm);
        }
        
        if (MipGenerator.DEBUGGING) {
            final double tolerance = 0.0001;
            double norm = 0.0;
            for (final Double item: result) {
                norm += item * item;
            }
            if (Math.abs(norm - 1.0) > tolerance) {
                throw new IllegalStateException();
            }
        }
        
        return result;
    }
}
