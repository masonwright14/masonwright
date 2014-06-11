package coalitiongames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import coalitiongames.PriceWithError.PriceUpdateSource;

public abstract class NeighborGenerator {
    
    public static List<PriceWithError> sortedNeighbors(
        final List<PriceWithSource> neighborPricesWithSource,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final List<Integer> teamSizes,
        final double maxPrice
    ) {
        final List<PriceWithError> result = 
            new ArrayList<PriceWithError>();
        for (
            final PriceWithSource neighborPriceWithSource
            : neighborPricesWithSource
        ) {
            final List<List<Integer>> neighborDemand = 
                DemandGenerator.getAggregateDemand(
                    agents, 
                    neighborPriceWithSource.getPrice(), 
                    teamSizes,
                    maxPrice
                );
            final int kMax = TabuSearch.getKMax(teamSizes);
            final List<Double> neighborZ = 
                gammaZ.z(
                    neighborDemand, neighborPriceWithSource.getPrice(), 
                    kMax, maxPrice
                );
            final double neighborError = 
                DemandAnalyzer.errorSizeDouble(neighborZ);
            result.add(
                new PriceWithError(
                    neighborPriceWithSource.getPrice(), 
                    neighborZ, 
                    neighborDemand, 
                    neighborError,
                    neighborPriceWithSource.getPriceUpdateSource()
                )
            );
        }
        
        Collections.sort(result);
        
        if (MipGenerator.DEBUGGING) {
            final double firstError = result.get(0).getErrorValue();
            final double lastError = 
                result.get(result.size() - 1).getErrorValue();
            if (firstError > lastError) {
                throw new IllegalStateException();
            }
        }
        
        return result;
    }
    
    /*
     * Returns neighbors sorted from lowest error to highest.
     */
    public static List<PriceWithError> sortedNeighbors(
        final List<PriceWithSource> neighborPricesWithSource,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final int kMax,
        final int kMin,
        final double maxPrice
    ) {
        final List<PriceWithError> result = 
            new ArrayList<PriceWithError>();
        for (
            final PriceWithSource neighborPriceWithSource
            : neighborPricesWithSource
        ) {
            final List<List<Integer>> neighborDemand = 
                DemandGenerator.getAggregateDemand(
                    agents, 
                    neighborPriceWithSource.getPrice(), 
                    kMax, 
                    kMin, 
                    maxPrice
                );
            final List<Double> neighborZ = 
                gammaZ.z(
                    neighborDemand, neighborPriceWithSource.getPrice(), 
                    kMax, maxPrice
                );
            final double neighborError = 
                DemandAnalyzer.errorSizeDouble(neighborZ);
            result.add(
                new PriceWithError(
                    neighborPriceWithSource.getPrice(), 
                    neighborZ, 
                    neighborDemand, 
                    neighborError,
                    neighborPriceWithSource.getPriceUpdateSource()
                )
            );
        }
        
        Collections.sort(result);
        
        if (MipGenerator.DEBUGGING) {
            final double firstError = result.get(0).getErrorValue();
            final double lastError = 
                result.get(result.size() - 1).getErrorValue();
            if (firstError > lastError) {
                throw new IllegalStateException();
            }
        }
        
        return result;
    }
    
    public static List<PriceWithError> sortedNeighbors(
        final List<Double> prices,
        final List<Double> z,
        final double maxPrice,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final List<Integer> teamSizes
    ) {
        final List<PriceWithSource> neighborPrices = 
            neighbors(
                prices, 
                z, 
                maxPrice
            );
        return sortedNeighbors(
            neighborPrices, 
            agents, 
            gammaZ, 
            teamSizes,
            maxPrice
        );
    }
    
    public static List<PriceWithError> sortedNeighbors(
        final List<Double> prices,
        final List<Double> z,
        final double maxPrice,
        final List<Agent> agents,
        final GammaZ gammaZ,
        final int kMax,
        final int kMin
    ) {
        final List<PriceWithSource> neighborPrices = 
            neighbors(
                prices, 
                z, 
                maxPrice
            );
        return sortedNeighbors(
            neighborPrices, 
            agents, 
            gammaZ, 
            kMax, 
            kMin, 
            maxPrice
        );
    }
    
    /**
     * convenience function that calls neighbors() with default
     * values for step sizes, for unilateral neighbors and gradient neighbors.
     */
    private static List<PriceWithSource> neighbors(
        final List<Double> prices,
        final List<Double> z,
        final double maxPrice
    ) {
        final List<Double> stepSizesUnilateral = new ArrayList<Double>();
        // assumes MipGenerator.MIN_BUDGET = 100.0
        final Double[] unilateralSizes = {1.0, 0.5, 0.1, 0.05, 0.001};
        Collections.addAll(stepSizesUnilateral, unilateralSizes);
        
        final List<Double> stepSizesGradient = new ArrayList<Double>();
        // assumes MipGenerator.MIN_BUDGET = 100.0
        final Double[] gradientSizes = {10.0, 5.0, 1.0, 0.5, 0.1};
        Collections.addAll(stepSizesGradient, gradientSizes);
        
        return neighbors(
            prices,
            z,
            stepSizesUnilateral,
            stepSizesGradient,
            maxPrice
        );
    }
    
    /**
     * 
     * @param prices current price vector, for which to return neighboring price
     * vectors, in the direction of the gradient based on z and on unilateral
     * deviations in the direction of the gradient.
     * @param z loss function. where z is positive, 
     * gradient increases the price,
     * and where z is negative, gradient decreases the price along that axis.
     * @param stepSizesUnilateral step sizes to move in direction of increasing
     * agent price, for agents with positive error according to z()
     * @param stepSizesGradient step sizes in which to move in the direction of
     * the gradient (after converting the gradient to a unit vector)
     * @param maxPrice maximum allowable price for an agent
     * @return  a list of lists of doubles, where each list is a price vector
     * that "neighbors" the given price vector. 
     * each agent returns a price vector for its unilateral move in the 
     * direction of its error for each stepSizesUnilateral,
     * and all agents move in the direction of the gradient 
     * for stepSizesGradient different step sizes.
     */
    private static List<PriceWithSource> neighbors(
        final List<Double> prices,
        final List<Double> z,
        final List<Double> stepSizesUnilateral,
        final List<Double> stepSizesGradient,
        final double maxPrice
    ) {
        assert prices.size() == z.size();
        assert maxPrice >= 0.0;
        
        final List<PriceWithSource> unilateralNeighbors = unilateralNeighbors(
            prices, 
            z, 
            stepSizesUnilateral, 
            maxPrice
        );
        final List<PriceWithSource> gradientNeighbors = gradientNeighbors(
            prices, 
            z, 
            stepSizesGradient, 
            maxPrice
        );
        final List<PriceWithSource> result = new ArrayList<PriceWithSource>();
        result.addAll(unilateralNeighbors);
        result.addAll(gradientNeighbors);
        return result;
    }
    
    /**
     * @param prices price vector, for which to return neighboring price vectors
     * @param z error for each agent
     * @param stepSizesUnilateral step sizes to 
     * take if increasing an agent's price
     * @param maxPrice
     * @return a list of lists of double, where each list is a price vector.
     * for each agent, add a list where the agent's price is 0 if z < 0, or
     * if z > 0, add a list where the agent's price is increased by each amount
     * in stepSizesUnilateral. attempt to limit the number of redundant lists
     * returned if possible.
     */
    private static List<PriceWithSource> unilateralNeighbors(
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
        
        final List<PriceWithSource> result = new ArrayList<PriceWithSource>();
        
        // iterate over all agents, handling the current agent's unilateral
        // price changes.
        for (int i = 0; i < prices.size(); i++) {
            // if the agent is under-demanded (price should be reduced if not
            // already 0)
            if (z.get(i) < 0) {
                // don't add  a redundant neighbor vector if agent i's
                // price is already 0.0
                if (!DemandAnalyzer.priceIsZero(prices.get(i))) {
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
                    
                    result.add(
                        new PriceWithSource(
                            newPrices, PriceUpdateSource.UNILATERAL
                        )
                    );
                }
            
                // else if the agent is over-demanded (price should be raised)
            } else if (z.get(i) > 0) {
                // for each step size, raise the agent's price by that amount
                // and add the resulting price vector to the neighbors,
                // truncated to maxPrice if needed
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
                    
                    result.add(
                        new PriceWithSource(
                            newPrices, PriceUpdateSource.UNILATERAL
                        )
                    );                }
            }
        }
        
        // don't add a neighbor for an agent that has no error (i.e., z == 0)
        
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
    private static List<PriceWithSource> gradientNeighbors(
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
        
        final List<PriceWithSource> result = new ArrayList<PriceWithSource>();
        if (!DemandAnalyzer.hasClearingErrorDouble(z)) {
            // can't produce a unit vector from the zero vector
            return result;
        }
        
        final List<Double> unitVectorZ = unitVectorZ(z);
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
            
            result.add(
                new PriceWithSource(
                    newPrices, PriceUpdateSource.GRADIENT
                )
            );
        }
        
        return result;
    }
    
    /**
     * @param z a list of doubles. must have at least 1 nonzero entry,
     * or a unit vector cannot be produced.
     * @return the same list multiplied by a scalar to make it a unit
     * vector.
     */
    private static List<Double> unitVectorZ(final List<Double> z) {
        final double l2Norm = DemandAnalyzer.errorSizeDouble(z);
        if (l2Norm <= 0.0) {
            throw new IllegalArgumentException();
        }
        
        final List<Double> result = new ArrayList<Double>();
        for (final Double item: z) {
            result.add(item / l2Norm);
        }
        
        if (MipGenerator.DEBUGGING) {
            double norm = 0.0;
            for (final Double item: result) {
                norm += item * item;
            }
            final double tolerance = 0.0001;
            if (Math.abs(norm - 1.0) > tolerance) {
                throw new IllegalStateException();
            }
        }
        
        return result;
    }
}
