package coalitiongames;

import java.util.List;

public interface MipGenerator {
    
    double MIN_BUDGET = 100.0;
    
    boolean DEBUGGING = false;
    
    MipResult getLpSolution(
        List<Double> values,
        List<Double> prices,
        double budget,
        List<Integer> kSizes,
        double maxPrice
    );

    MipResult getLpSolution(
        List<Double> values,
        List<Double> prices,
        double budget,
        int kMax,
        int kMin,
        double maxPrice
    );
    
    boolean checkLpSolution(
        MipResult solution,
        List<Double> values,
        List<Double> prices,
        double budget,
        int kMax,
        int kMin,
        int iterations
    );
}
