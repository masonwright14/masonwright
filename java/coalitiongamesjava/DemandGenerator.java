package coalitiongames;

import java.util.ArrayList;
import java.util.List;

abstract class DemandGenerator {
    
    /**
     * @param agents 
     * @param prices prices for all agents, in their listed order
     * @param teamSizes list of feasible next team sizes, based on 
     * RsdUtil.getFeasibleNextTeamSizes().
     * @param maxPrice highest price allowable for any agent
     * @return a list of the demand of each agent, in the order the agents
     * are listed. each agent's demand is listed for itself as 1, so each agent
     * has a demanded listed for all N agents, including itself. 
     * hence, the result can be thought of as a square matrix 
     * with 1 along the main diagonal,
     * stored as a list of lists of integers. all integers are in {0, 1}.
     * the sum along any row should be in {kMin, kMin + 1, . . ., kMax}, when
     * the demand for self at matrix[i][i] = 1 is considered.
     */
    public static List<List<Integer>> getAggregateDemand(
        final List<Agent> agents,
        final List<Double> prices,
        final List<Integer> teamSizes,
        final double maxPrice
    ) {
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
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
            
            final MipGenerator mipGen = new MipGeneratorGLPK();
            final MipResult mipResult = mipGen.getLpSolution(
                agent.getValues(), // values for (N - 1) other agents
                iPrices, // prices of (N - 1) other agents
                agent.getBudget(), 
                teamSizes,
                maxPrice
            );
            
            // get the column values converted to integers in {0, 1}.
            final List<Integer> toAdd = mipResult.getRoundedColumnValues();
            // each agent is on a team with itself, so insert 1
            // for self-demand as a placeholder
            toAdd.add(i, 1);
            result.add(toAdd);
        }
        
        return result;
    }

    /**
     * @param agents 
     * @param prices prices for all agents, in their listed order
     * @param kMax maximum total agents on a team, so max demand is (kMax - 1)
     * @param kMin minimum total agents on a team, so min demand is (kMin - 1)
     * @param maxPrice highest price allowable for any agent
     * @return a list of the demand of each agent, in the order the agents
     * are listed. each agent's demand is listed for itself as 1, so each agent
     * has a demanded listed for all N agents, including itself. 
     * hence, the result can be thought of as a square matrix 
     * with 1 along the main diagonal,
     * stored as a list of lists of integers. all integers are in {0, 1}.
     * the sum along any row should be in {kMin, kMin + 1, . . ., kMax}, when
     * the demand for self at matrix[i][i] = 1 is considered.
     */
    public static List<List<Integer>> getAggregateDemand(
        final List<Agent> agents,
        final List<Double> prices,
        final int kMax,
        final int kMin,
        final double maxPrice
    ) {
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
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
            
            final MipGenerator mipGen = new MipGeneratorGLPK();
            final MipResult mipResult = mipGen.getLpSolution(
                agent.getValues(), // values for (N - 1) other agents
                iPrices, // prices of (N - 1) other agents
                agent.getBudget(), 
                kMax, 
                kMin, 
                maxPrice
            );
            
            // get the column values converted to integers in {0, 1}.
            final List<Integer> toAdd = mipResult.getRoundedColumnValues();
            // each agent is on a team with itself, so insert 1
            // for self-demand as a placeholder
            toAdd.add(i, 1);
            result.add(toAdd);
        }
        
        return result;
    }
}
