package regretexperiment;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import coalitiongames.RsdUtil;
import experiment.FileHandler;
import experiment.SampleInputGenerator;

public abstract class RegretRandomInputGenerator {

    public static void main(final String[] args) {
        // final int runCount = 40;
        // final int numPlayers = 20;
        // generateRandomInputFiles(numPlayers, runCount);
        //  generateAllRandomInputFiles(runCount);
        generateSmallRandomInputFiles();
    }
    
    public static void generateSmallRandomInputFiles() {
        final int runCount = 8;
        final int agents = 20;
        generateRandomInputFiles(agents, runCount);
    }
    
    public static void generateAllRandomInputFiles(
        final int runCount
    ) {
        final int[] numPlayerArray = {20, 30, 50, 200};
        for (int i = 0; i < numPlayerArray.length; i++) {
            generateRandomInputFiles(numPlayerArray[i], runCount);
            System.out.println("Done with: " + numPlayerArray[i]);
        }
    }

    
    public static void generateRandomInputFiles(
        final int numPlayers,
        final int runCount
    ) { 
        for (int runNumber = 1; runNumber <= runCount; runNumber++) {
            final String outFileName = 
                "regretInputFiles/random_" + numPlayers + "_agents_" 
                + runNumber + FileHandler.TEXT_EXTENSION;
            final List<Integer> rsdOrder = 
                RsdUtil.getShuffledNumberList(numPlayers);
            final List<Double> budgets = 
                SampleInputGenerator.getBudgets(numPlayers, rsdOrder);
            final List<List<Double>> valueMatrix = 
                getRandomValueMatrix(numPlayers);
            FileHandler.printInputFile(
                outFileName, rsdOrder, budgets, valueMatrix
            );
        }
    }
    
    /*
     * Each player must assign value -1 to itself.
     * 
     * Assign each player base value of its index + 1, 
     * so player index 0 has value
     * 1 initially, and player (N - 1) has value N.
     * 
     * Add to the base value a random drawn from the 
     * normal distribution with 0 mean
     * and standard deviation of (N / 5). (N / 5) is chosen as the s.d. because
     * in Budish & Othman 2010, they used s.d. of 10 
     * with 50 courses to be valued.
     * 
     * While the sum of the base value and the random 
     * noise term is <= 0, redraw the
     * noise term.
     * 
     * Don't add a noise term to the self value of -1.
     */
    private static List<List<Double>> getRandomValueMatrix(
        final int numPlayers
    ) {
        final List<List<Double>> matrix = new ArrayList<List<Double>>();
        final Random random = new Random();
        final double standardDeviationFraction = 5.0;
        final double stadardDeviation = numPlayers / standardDeviationFraction;
        for (int i = 0; i < numPlayers; i++) {
            final List<Double> row = new ArrayList<Double>();
            for (int j = 0; j < numPlayers; j++) {
                if (i == j) {
                    row.add(-1.0);
                } else {
                    final int baseValue = j + 1;
                    double valueWithNoise = 
                        baseValue + random.nextGaussian() * stadardDeviation;
                    while (valueWithNoise <= 0.0) {
                        valueWithNoise = 
                            baseValue 
                            + random.nextGaussian() * stadardDeviation;
                    }
                    row.add(valueWithNoise);
                }
            }
            
            matrix.add(row);
        }
        
        return matrix;
    }
}
