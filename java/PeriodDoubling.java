package chaos;

/*
 * xnext = r * x * (1 - x)
as r goes from 2 to 4
-where are the period doublings?
 */
public class PeriodDoubling {

    public static void main(final String[] args) {
        /*
         * Result: 
         * first period doubling at r = 3.0 (period 2)
         * second period doubling at r = 3.5 (period 4)
         * chaos may set in by r = 3.6
         * next step: zoom in from 3.5 to 3.6
        printAllSeries(
            2.0, 
            0.1, 
            20, 
            0.5, 
            100, 
            20
        );
        */
        
        /*
         * Result:
         * [prior result: period 2 at r = 3.0]
         * period 4 at r = 3.500
         * period 8 at r = 3.540
         * period 16 at r = 3.565
         * chaos may set in by r = 3.570
         */
        printAllSeries(
            3.5, 
            0.005, 
            20, 
            0.5, 
            100, 
            32
        );
    }
    
    public static void printAllSeries(
        final double rMin,
        final double rDifference,
        final int rCount,
        final double initialValue,
        final int startDuration,
        final int printDuration
    ) {
        assert rMin > 0;
        assert rDifference > 0;
        assert rCount > 0;
        assert startDuration >= 0;
        assert printDuration > 0;
        
        double r = rMin;
        for (int i = 0; i < rCount; i++) {
            printSeriesAfterStart(
                r, 
                initialValue, 
                startDuration, 
                printDuration
            );
            
            System.out.println();
            r += rDifference;
        }
    }
    
    public static void printSeriesAfterStart(
        final double r,
        final double initialValue,
        final int startDuration, 
        final int printDuration
    ) {
        assert startDuration >= 0;
        assert printDuration >= 1;
        
        System.out.println("r: " + r + ". Initial value: " + initialValue);
        
        double currentValue = initialValue;
        for (int i = 0; i < startDuration; i++) {
            currentValue = getNextValue(r, currentValue);
        }
        
        for (int i = 0; i < printDuration; i++) {
            currentValue = getNextValue(r, currentValue);
            System.out.println(currentValue);
        }
        System.out.println();
    }
    
    public static double getNextValue(
        final double r, 
        final double currentValue
    ) {
        return r * currentValue * (1 - currentValue);
    }
}
