package experiment;

import java.util.List;

import coalitiongames.Util;

public abstract class SampleInputLoader {
    
    public static void main(final String[] args) {
        // final String fileName = getFileName("bkfrat", 10);
        final String fileName = getRandomlyGeneratedFileName(20, 10);
        System.out.println(fileName);
        final List<Integer> rsdOrder = getRsdOrder(fileName);
        System.out.println(rsdOrder);
        final List<Double> budgets = getBudgets(fileName);
        System.out.println(budgets);
        final List<List<Double>> matrix = getMatrix(fileName);
        Util.printDemandAsMatrix(matrix);
    }

    public static String getFileName(
        final String prefix,
        final int runNumber
    ) {
        return
            SampleInputGenerator.INPUT_FOLDER + prefix 
            + "_" + runNumber + FileHandler.TEXT_EXTENSION;
    }
    
    public static String getRandomlyGeneratedFileName(
        final int numPlayers,
        final int runNumber
    ) {
        return
            "inputFiles/random_" + numPlayers + "_agents_" 
            + runNumber + FileHandler.TEXT_EXTENSION;
    }
    
    public static List<Integer> getRsdOrder(
        final String fileName
    ) {
        final List<String> lines = FileHandler.getLines(fileName);
        final String rsdOrderLine = lines.get(0);
        return FileHandler.getSpaceSeparatedIntegerRow(rsdOrderLine);
    }
    
    public static List<Double> getBudgets(final String fileName) {
        final List<String> lines = FileHandler.getLines(fileName);
        final String budgetLine = lines.get(1);
        return FileHandler.getSpaceSeparatedDoubleRow(budgetLine);
    }
    
    public static List<List<Double>> getMatrix(final String fileName) {
        final List<String> lines = FileHandler.getLines(fileName);
        if (lines.size() <= 2) {
            throw new IllegalArgumentException("no matrix");
        }
        lines.remove(1);
        lines.remove(0);
        final List<List<Double>> result = 
            FileHandler.getSpaceSeparatedDoubleRows(lines);
        if (result.isEmpty()) {
            throw new IllegalArgumentException("empty matrix");
        }
        
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).size() != result.size()) {
                throw new IllegalArgumentException("not a square matrix");
            }
        }
        
        return result;
    }
}
