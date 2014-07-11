package regretexperiment;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import experiment.ExperimentRunner;
import experiment.FileHandler;
import experiment.ProblemGenerator;
import experiment.ProblemGenerator.SearchAlgorithm;
import experiment.ProblemGenerator.SimpleSearchAlgorithm;

public abstract class RegretExperimentRunner {

    private static final String REGRET_INPUT_FOLDER_NAME = "regretInputFiles";
    
    public static void main(final String[] args) {
        /*
        final String inputFilePrefix = "random_20_agents";
        final int deviationsPerAgent = 100;
        // final SearchAlgorithm algorithm = SimpleSearchAlgorithm.RSD_OPT;
        // final SearchAlgorithm algorithm = SimpleSearchAlgorithm.DRAFT;
        final SearchAlgorithm algorithm = SimpleSearchAlgorithm.EACH_DRAFT_CC;
        runRegretExperiment(
            algorithm, 
            "cplex", 
            inputFilePrefix, 
            deviationsPerAgent
        );
        */
        
        final String inputFilePrefix = "rndUncor_20_agents";
        runFastRegretExperiments(inputFilePrefix);
    }
 
    public static void runFastRegretExperiments(
        final String inputFilePrefix
    ) {
        final SimpleSearchAlgorithm[] fastAlgorithms = {
            SimpleSearchAlgorithm.DRAFT,
            SimpleSearchAlgorithm.EACH_DRAFT,
            SimpleSearchAlgorithm.EACH_DRAFT_CC
        };
        
        // final int deviationsPerAgent = 100;
        final int deviationsPerAgent = 25;
        final String solverName = ExperimentRunner.SOLVER_NAME;
        for (int i = 0; i < fastAlgorithms.length; i++) {
            final SimpleSearchAlgorithm algorithm = fastAlgorithms[i];
            runRegretExperiment(
                algorithm, 
                solverName, 
                inputFilePrefix, 
                deviationsPerAgent
            );
            System.out.println("done with: " + algorithm);
        }
    }
    
    public static void runRegretExperiment(
        final SearchAlgorithm algorithm,
        final String solverName,
        final String inputFilePrefix,
        final int deviationsPerAgent
    ) {
        // should not be a randomized algorithm
        assert algorithm != SimpleSearchAlgorithm.RANDOM_ANY
            && algorithm != SimpleSearchAlgorithm.RANDOM_OPT;
        
        final String algorithmName = 
            ProblemGenerator.getAlgorithmName(algorithm);
        final int runCount = countInputFilesByPrefix(inputFilePrefix);
        final long experimentStartMillis = new Date().getTime();
        final List<RegretSearchResult> searchResults = 
            new ArrayList<RegretSearchResult>();
        for (int runNumber = 1; runNumber <= runCount; runNumber++) {
            final String inputFileName = REGRET_INPUT_FOLDER_NAME + "/" 
                + inputFilePrefix + "_" 
                + runNumber + FileHandler.TEXT_EXTENSION;
            final long runStartMillis = new Date().getTime();

            final RegretSearchResult searchResult =
                RegretProblemGenerator.getRegretSearchResult(
                    inputFileName, algorithm, deviationsPerAgent
                );
            searchResults.add(searchResult);
            final long runDurationMillis =
                new Date().getTime() - runStartMillis;
            System.out.println(
                "\t\t\tRan iteration " + runNumber + " for solver " 
                + algorithmName + " for input " + inputFilePrefix
                + " in " + runDurationMillis + " millis"
            );
        }
        
        final long experimentDurationMillis = 
            new Date().getTime() - experimentStartMillis;
        RegretOutputPrinter.printOutput(
            searchResults, 
            algorithmName, 
            solverName, 
            inputFilePrefix
        );
        
        System.out.println(
            "\t\tRan solver " + algorithmName 
            + " for input: " + inputFilePrefix + " in " 
            + experimentDurationMillis + " millis"
        );
    }
    
    private static int countInputFilesByPrefix(final String prefix) {
        return getInputFileNamesByPrefix(prefix).size();
    }
    
    private static List<String> getInputFileNames() {
        File inputFolder = new File(REGRET_INPUT_FOLDER_NAME);
        File[] listOfContents = inputFolder.listFiles();
        
        final List<String> result = new ArrayList<String>();
        for (int i = 0; i < listOfContents.length; i++) {
            File currentFile = listOfContents[i];
            if (currentFile.isFile()) {
                result.add(currentFile.getName());
            }
        }
        return result;
    }
    
    private static List<String> getInputFileNamesByPrefix(final String prefix) {
        final String prefixWithUnderbar = prefix + "_";
        final List<String> allInputFileNames = getInputFileNames();
        for (int i = allInputFileNames.size() - 1; i >= 0; i--) {
            final String currentName = allInputFileNames.get(i);
            if (currentName.indexOf(prefixWithUnderbar) != 0) {
                allInputFileNames.remove(i);
            }
        }
        
        return allInputFileNames;
    }
}
