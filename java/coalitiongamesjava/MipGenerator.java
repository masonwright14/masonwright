package coalitiongames;

import java.util.List;

public interface MipGenerator {
    
    // TODO
    // handle case where no set of size kMin or larger
    // is affordable.
    // return an MipResult that indicates this,
    // and handle this type of MipResult wherever
    // MipResults are used.
    
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
}
