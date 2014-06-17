package coalitiongames;

import java.util.List;

public interface DemandGenerator {

    List<List<Integer>> getAggregateDemand(
        List<Agent> agents,
        List<Double> prices,
        List<Integer> teamSizes,
        double maxPrice
    );
    
    List<List<Integer>> getAggregateDemand(
        List<Agent> agents,
        List<Double> prices,
        int kMax,
        int kMin,
        double maxPrice
    );
}
