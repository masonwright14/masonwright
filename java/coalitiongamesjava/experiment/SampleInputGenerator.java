package experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import coalitiongames.MipGenerator;
import coalitiongames.RsdUtil;

public abstract class SampleInputGenerator {

    private static final String TEXT_EXTENSION = ".txt";
    
    public static void main(final String[] args) {
        final int runCount = 20;
        generateInputFiles("bkfrat_cleaned.txt", "bkfrat", runCount);
    }
    
    public static void generateInputFiles(
        final String baseFileName,
        final String outFilePrefix,
        final int runCount
    ) {
        final List<List<Integer>> inputMatrix = 
            getMatrixFromFile(baseFileName);
        final int numPlayers = inputMatrix.size();
        
        for (int runNumber = 1; runNumber <= runCount; runNumber++) {
            final String outFileName = 
                outFilePrefix + "_" + runNumber + TEXT_EXTENSION;
            final List<Integer> rsdOrder = 
                RsdUtil.getShuffledNumberList(numPlayers);
            final List<Double> budgets = getBudgets(numPlayers, rsdOrder);
            final List<List<Double>> jitteredMatrix = 
                jitterValueMatrix(inputMatrix);
            FileHandler.printInputFile(
                outFileName, rsdOrder, budgets, jitteredMatrix
            );
        }
    }
    
    /*
     * Retain -1's along main diagonal, but jitter other values.
     */
    private static List<List<Double>> jitterValueMatrix(
        final List<List<Integer>> oldMatrix
    ) {
        final int numPlayers = oldMatrix.size();
        final List<List<Double>> result = new ArrayList<List<Double>>();
        for (int i = 0; i < numPlayers; i++) {
            final List<Integer> oldRow = oldMatrix.get(i);
            final List<Double> newRow = new ArrayList<Double>();
            for (int j = 0; j < numPlayers; j++) {
                if (i == j) {
                    newRow.add(-1.0);
                } else {
                    final int oldValue = oldRow.get(j);
                    final double newValue = 
                        oldValue + Math.random() / numPlayers;
                    newRow.add(newValue);
                }
            }
            result.add(newRow);
        }
        
        return result;
    }
    
    /*
     * Report budgets such that the first player in rsdOrder has the highest
     * budget, and so on.
     */
    private static List<Double> getBudgets(
        final int numPlayers,
        final List<Integer> rsdOrder
    ) {
        final List<Double> budgets = new ArrayList<Double>();
        for (int i = 0; i < numPlayers; i++) {
            final double budget =
                MipGenerator.MIN_BUDGET 
                + Math.random() * MipGenerator.MIN_BUDGET / numPlayers;
            budgets.add(budget);
        }
        
        Collections.sort(budgets);
        Collections.reverse(budgets);
        final List<Double> sortedBudgets = new ArrayList<Double>(budgets);
        budgets.clear();
        for (int i = 0; i < numPlayers; i++) {
            final int rsdIndexOfPlayerI = rsdOrder.get(i);
            budgets.add(sortedBudgets.get(rsdIndexOfPlayerI));
        }
        
        for (int i = 0; i < numPlayers - 1; i++) {
            int ithPlayer = rsdOrder.indexOf(i);
            int ithPlusOnePlayer = rsdOrder.indexOf(i + 1);
            if (budgets.get(ithPlayer) < budgets.get(ithPlusOnePlayer)) {
                throw new IllegalStateException();
            }
        }
        
        return budgets;
    }
    
    public static List<List<Integer>> getMatrixFromFile(
        final String fileName
    ) {
        final List<List<Integer>> result = 
            FileHandler.getSpaceSeparatedIntegerRows(
                FileHandler.getLines(fileName)
            );
        
        if (result.isEmpty()) {
            throw new IllegalArgumentException("empty input file");
        }
        
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).size() != result.size()) {
                throw new IllegalArgumentException("not a square matrix");
            }
        }
        
        return result;
    }
}
