package coalitiongames;

import java.util.List;

public interface MipGenerator {

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
