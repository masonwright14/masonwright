package regretexperiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import coalitiongames.MipGenerator;
import coalitiongames.RsdUtil;
import experiment.FileHandler;

public class RegretSampleInputGenerator {
    public static final String INPUT_FOLDER = "regretInputFiles/";
    
    public static void main(final String[] args) {
        final int runCount = 40;
        generateInputFiles("newfrat_cleaned.txt", "newfrat", runCount);
        // generateAllInputFiles(runCount);
    }
    
    public static void generateAllInputFiles(
        final int runCount
    ) {
        final String[] inputFileNames = {
            "bkfrat_cleaned.txt", "bkoffice_cleaned.txt", 
            "crossParker_cleaned.txt", 
            "freeman_cleaned.txt", "newfrat_cleaned.txt",
            "radoslawEmail_cleaned.txt", "vandebunt_t5_cleaned.txt", 
            "webster_res_cleaned.txt"
        };
        final String[] inputPrefixes = {
            "bkfrat", "bkoff", 
            "cross",
            "free", "newfrat",
            "rados", "vand",
            "webster"
        };
        for (int i = 0; i < inputFileNames.length; i++) {
            generateInputFiles(inputFileNames[i], inputPrefixes[i], runCount);
            System.out.println("Done with: " + inputFileNames[i]);
        }
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
                INPUT_FOLDER + outFilePrefix 
                + "_" + runNumber + FileHandler.TEXT_EXTENSION;
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
     * 
     * rsdOrder: first item is the index of the first agent to go.
     * second item is the index of second agent to go, etc.
     * example:
     * 1 2 0 -> agent 1 goes, then agent 2, then agent 0.
     */
    public static List<Double> getBudgets(
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
        
        // arrange budgets high to low
        Collections.sort(budgets);
        Collections.reverse(budgets);
        final List<Double> sortedBudgets = new ArrayList<Double>(budgets);
        budgets.clear();
        for (int i = 0; i < numPlayers; i++) {
            final int rsdIndexOfPlayerI = rsdOrder.indexOf(i);
            budgets.add(sortedBudgets.get(rsdIndexOfPlayerI));
        }
        
        for (int i = 0; i < numPlayers - 1; i++) {
            int ithPlayer = rsdOrder.get(i);
            int ithPlusOnePlayer = rsdOrder.get(i + 1);
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
