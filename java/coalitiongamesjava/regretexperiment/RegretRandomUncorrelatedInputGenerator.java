package regretexperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import coalitiongames.MipGenerator;
import coalitiongames.RsdUtil;
import experiment.FileHandler;
import experiment.SampleInputGenerator;

public abstract class RegretRandomUncorrelatedInputGenerator {

    public static void main(final String[] args) {
        // Util.printDemandAsMatrix(getRandomValueMatrix(5));

        final int runCount = 40;
        // final int numPlayers = 20;
        // generateRandomInputFiles(numPlayers, runCount);
        generateAllRandomInputFiles(runCount);
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
                "regretInputFiles/rndUncor_" + numPlayers + "_agents_" 
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
     * Generate uncorrelated preferences for all players.
     * 
     * Each player has 100.0 value points to spread randomly over
     * the (numPlayers - 1) other players.
     * 
     * 
     */
    private static List<List<Double>> getRandomValueMatrix(
        final int numPlayers
    ) {
        final List<List<Double>> matrix = new ArrayList<List<Double>>();
        final double totalValue = 100.0;
        for (int i = 0; i < numPlayers; i++) {
            final List<Double> randomDraws = new ArrayList<Double>();
            for (int j = 1; j <= numPlayers - 2; j++) {
                randomDraws.add(Math.random() * totalValue);
            }
            Collections.sort(randomDraws);
            
            // (numPlayers - 2) random draws divide totalValue into
            // (numPlayers - 1) random regions. the size of each region,
            // in order, is the next player value.
            final List<Double> row = new ArrayList<Double>();
            
            // first player's value is minimum of randomDraws.
            row.add(randomDraws.get(0));
            
            // for each index i from 1 to (randomDraws.size() - 1),
            // next item is randomDraws.get(i) - randomDraws.get(i - 1).
            for (int j = 1; j < randomDraws.size(); j++) {
                row.add(randomDraws.get(j) - randomDraws.get(j - 1));
            }
            
            // last player's value is totalValue - max of randomDraws.
            row.add(totalValue - randomDraws.get(randomDraws.size() - 1));
            
            // check sum of row before adding -1.0 for self value
            if (MipGenerator.DEBUGGING) {
                double totalInRow = 0.0;
                for (final Double item: row) {
                    totalInRow += item;
                    if (item < 0.0 || item > totalValue) {
                        throw new IllegalStateException();
                    }
                }
                
                final double tolerance = 0.01;
                if (Math.abs(totalInRow - totalValue) > tolerance) {
                    throw new IllegalStateException();
                }
            }
            
            // insert -1.0 for value of self in the row, at index i.
            row.add(i, -1.0);
            
            matrix.add(row);
        }
        
        return matrix;
    }
}