package coalitiongames;

import java.util.List;

public interface GammaZ {

    /**
     * 
     * @param demand a list of the demand of each agent, in the order the agents
     * are listed. each agent's demand is listed for itself as 1, so each agent
     * has a demanded listed for all N agents, including itself. 
     * hence, the result can be thought of as a square matrix 
     * with 1 along the main diagonal,
     * stored as a list of lists of integers. all integers are in {0, 1}.
     * the sum along any row should be in {kMin, kMin + 1, . . ., kMax}, when
     * the demand for self at matrix[i][i] = 1 is considered.
     * @param prices prices for all agents, in their listed order. 
     * prices should be in the truncated price space P: [0, max-price].
     * @param kMax 1 more than the maximum agents that can be demanded. 
     * in other words, the max agents per team.
     * 
     * @return a list of values to add to the truncated price of each agent
     * to get the next update price of the agents. this can be thought of
     * as a gradient vector in the direction in which prices should change (not
     * necessarily a unit-length vector.)
     */
    List<Double> z(
        List<List<Integer>> demand, 
        List<Double> prices,
        int kMax,
        int kMin,
        double maxPrice
    );
    
    List<Double> gammaZ(
        List<List<Integer>> demand, 
        List<Double> prices,
        int kMax,
        int kMin,
        double maxPrice
    );
    
    /**
     * 
     * @param kMax 1 more than the maximum agents that can be demanded. 
     * in other words, the max agents per team.
     * @param n total number of agents, including any "self" agent
     * @return L2-norm of worst case market clearing error guaranteed by
     * the fixed point theorem, for a particular loss function z
     */
    double worstCaseError(
        int kMax,
        int n
    );
}
