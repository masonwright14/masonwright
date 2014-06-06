package coalitiongames;

import java.util.ArrayList;
import java.util.List;

abstract class DemandAnalyzer {
    
    public static double errorSize(final List<Integer> errorDemand) {
        double result = 0.0;
        for (Integer demand: errorDemand) {
            result += demand * demand;
        }
        
        return Math.sqrt(result);
    }
    
    public static double errorSizeDouble(final List<Double> errorDemand) {
        double result = 0.0;
        for (Double demand: errorDemand) {
            result += demand * demand;
        }
        
        return Math.sqrt(result);
    }
    
    public static boolean hasClearingErrorDouble(
        final List<Double> errorDemand
    ) {
        for (Double i: errorDemand) {
            if (i != 0) {
                return true;
            }
        }
        
        return false;
    }
    
    public static boolean hasClearingError(final List<Integer> errorDemand) {
        for (Integer i: errorDemand) {
            if (i != 0) {
                return true;
            }
        }
        
        return false;
    }
    
    public static List<Integer> getUnderDemand(
        final List<List<Integer>> demand,
        final List<Double> prices
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        
        final double tolerance = 0.00001;
        for (int i = 0; i < prices.size(); i++) {
            int underDemand = 0;
            if (Math.abs(prices.get(i)) >= tolerance) {
                underDemand = 1;
                for (int j = 0; j < prices.size(); j++) {
                    if (i != j && demand.get(j).get(i) == 1) {
                        underDemand = 0;
                        break;
                    }
                }
            }
            
            result.add(underDemand);
        }
        
        return result;
    }
    
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

    public static List<Integer> getIncomingUnrequitedDemand(
        final List<List<Integer>> demand
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < demand.size(); i++) {
            int incomingUnrequitedDemand = 0;
            for (int j = 0; j < demand.size(); j++) {
                if (
                    i != j 
                    && demand.get(j).get(i) == 1 
                    && demand.get(i).get(j) == 0
                ) {
                    incomingUnrequitedDemand++;
                }
            }
            
            result.add(incomingUnrequitedDemand);
        }
        
        return result;
    }
    
    public static List<Integer> getOutgoingUnrequitedDemand(
        final List<List<Integer>> demand
    ) {
        final List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < demand.size(); i++) {
            int outgoingUnrequitedDemand = 0;
            for (int j = 0; j < demand.size(); j++) {
                if (
                    i != j 
                    && demand.get(i).get(j) == 1 
                    && demand.get(j).get(i) == 0
                ) {
                    outgoingUnrequitedDemand++;
                }
            }
            
            result.add(outgoingUnrequitedDemand);        }
        
        return result;
    }
}
