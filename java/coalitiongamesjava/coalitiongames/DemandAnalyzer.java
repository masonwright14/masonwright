package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public abstract class DemandAnalyzer {
    
    /**
     * 
     * @param errorDemand a vector of errors
     * @return the RMS error. that is, the sum of squares of entries,
     * then taking the square root.
     */
    public static double errorSize(final List<Integer> errorDemand) {
        double result = 0.0;
        for (Integer demand: errorDemand) {
            result += demand * demand;
        }
        
        return Math.sqrt(result);
    }

    /**
     * 
     * @param errorDemand a vector of errors
     * @return the RMS error. that is, the sum of squares of entries,
     * then taking the square root.
     */
    public static double errorSizeDouble(final List<Double> errorDemand) {
        double result = 0.0;
        for (Double demand: errorDemand) {
            result += demand * demand;
        }
        
        return Math.sqrt(result);
    }
    
    /**
     * 
     * @param errorDemand a vector of errors
     * @return true if any entry is nonzero, else false
     */
    public static boolean hasClearingError(final List<Integer> errorDemand) {
        for (Integer i: errorDemand) {
            if (i != 0) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 
     * @param errorDemand a vector of errors
     * @return true if any entry is nonzero, else false.
     * a tolerance of 0.00001 for zero entries.
     */
    public static boolean hasClearingErrorDouble(
        final List<Double> errorDemand
    ) {
        final double tolerance = 0.00001;
        for (Double i: errorDemand) {
            if (Math.abs(i) >= tolerance) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 
     * @param price a price of any agent, in the search problem
     * @return true if and only if the absolute value is strictly within
     * "tolerance" of zero.
     */
    public static boolean priceIsZero(final double price) {
        final double tolerance = 0.00001;
        return Math.abs(price) < tolerance;
    }
    
    /**
     * @param demand a list of lists of integers in {0, 1}.
     * each row (list of integers) is a single agent's demand,
     * with 0 for agents not in its team, and 1 for others including
     * itself.
     * @return a list of integers in {0, 1, 2, . . ., kMin - 1}. an entry
     * of size "p" means that p fewer than (kMin - 1) other agents demand
     * the agent.
     */
    public static List<Integer> getIntegerUnderDemand(
        final List<List<Integer>> demand,
        final int kMin
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        
        if (kMin <= 1) {
            for (int i = 0; i < demand.size(); i++) {
                result.add(0);
            }
            return result;
        }
        
        final int minDemand = kMin - 1;
        for (int i = 0; i < demand.size(); i++) {
            // if not demanded by any other agent, under-demand is (kMin - 1)
            int underDemand = minDemand;
            for (int j = 0; j < demand.size(); j++) {
                if (i != j && demand.get(j).get(i) == 1) {
                    // demanded by other agent j
                    // reduce under-demand by 1
                    underDemand--;
                    if (underDemand == 0) {
                        // don't let under-demand become negative
                        break;
                    }
                }
            }
            
            assert underDemand >= 0;
            result.add(underDemand);
        }
        
        return result;       
    }
    
    /**
     * 
     * @param demand a list of lists of integers in {0, 1}.
     * each row (list of integers) is a single agent's demand,
     * with 0 for agents not in its team, and 1 for others including
     * itself.
     * @return a list of integers in {0, 1}. a 1 indicates that an
     * agent is not demanded by any other agent.
     * Under-demand can be 1 even if an agent's price is 0, but under-demand
     * is acceptable for an agent with price of 0.
     */
    public static List<Integer> getUnderDemand(
        final List<List<Integer>> demand
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        
        for (int i = 0; i < demand.size(); i++) {
            int underDemand = 1;
            for (int j = 0; j < demand.size(); j++) {
                if (i != j && demand.get(j).get(i) == 1) {
                    underDemand = 0;
                    break;
                }
            }
            
            result.add(underDemand);
        }
        
        return result;
    }
    
    /**
     * 
     * @param demand a list of lists of integers in {0, 1}.
     * each row (list of integers) is a single agent's demand,
     * with 0 for agents not in its team, and 1 for others including
     * itself.
     * @return a list of integers in {0, 1, . . ., N - 1}. for a given
     * agent, the unrequited demand is the number of other agents that
     * the agent demands but do not demand it, plus the number of other
     * agents that demand it but it does not demand.
     */
    public static List<Integer> getUnrequitedDemand(
        final List<List<Integer>> demand
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < demand.size(); i++) {
            int unrequitedDemand = 0;
            for (int j = 0; j < demand.size(); j++) {
                if (i != j && (demand.get(j).get(i) != demand.get(i).get(j))) {
                    unrequitedDemand++;
                }
            }
            
            result.add(unrequitedDemand);
        }
        
        return result;
    }

    /**
     * 
     * @param demand a list of lists of integers in {0, 1}.
     * each row (list of integers) is a single agent's demand,
     * with 0 for agents not in its team, and 1 for others including
     * itself.
     * @return a list of integers in {0, 1, . . ., N - 1}. for a given
     * agent, the incoming unrequited demand is the number of other agents
     * that demand it but it does not demand.
     */
    public static List<Integer> getIncomingUnrequitedDemand(
        final List<List<Integer>> demand
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < demand.size(); i++) {
            int incomingUnrequitedDemand = 0;
            for (int j = 0; j < demand.size(); j++) {
                if (
                    i != j 
                    && demand.get(j).get(i) == 1 // j demands i
                    && demand.get(i).get(j) == 0 // i does not demand j
                ) {
                    incomingUnrequitedDemand++; // add to i's IURQ
                }
            }
            
            result.add(incomingUnrequitedDemand);
        }
        
        return result;
    }
    
    /**
     * 
     * @param demand a list of lists of integers in {0, 1}.
     * each row (list of integers) is a single agent's demand,
     * with 0 for agents not in its team, and 1 for others including
     * itself.
     * @return a list of integers in {0, 1, . . ., N - 1}. for a given
     * agent, the outgoing unrequited demand is the number of other agents
     * that it demands but do not demand it.
     */
    public static List<Integer> getOutgoingUnrequitedDemand(
        final List<List<Integer>> demand
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < demand.size(); i++) {
            int outgoingUnrequitedDemand = 0;
            for (int j = 0; j < demand.size(); j++) {
                if (
                    i != j 
                    && demand.get(i).get(j) == 1 // i demands j
                    && demand.get(j).get(i) == 0 // j does not demand i
                ) {
                    outgoingUnrequitedDemand++; // add to i's OURQ
                }
            }
            
            result.add(outgoingUnrequitedDemand);        
        }
        
        return result;
    }
}
