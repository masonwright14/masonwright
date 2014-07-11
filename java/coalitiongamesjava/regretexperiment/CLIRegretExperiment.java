package regretexperiment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import experiment.ExperimentRunner;
import experiment.ProblemGenerator;
import experiment.ProblemGenerator.SearchAlgorithm;
import experiment.ProblemGenerator.TabuSearchAlgorithm;

public abstract class CLIRegretExperiment {

    private static final String HELP_ARG = "help";

    private static final String ALL_FAST_STRING = "allFast";
        
    /*
     * Assumptions:
     * Has directories /regretInputFiles and /regretOutputFiles
     * 
     * 2 cases:
     * all fast algorithms, specific file
     * one algorithm, specific file
     */
    public static void main(final String[] args) {
        final List<String> argsList = Arrays.asList(args);
        if (!checkArgs(argsList)) {
            printUsageMessage();
            return;
        }
        
        if (argsList.size() == 1) {
            if (argsList.get(0).equals(HELP_ARG)) {
                printUsageMessage();
                printAlgorithmMessage();
                printInputFileMessage();
            }
            return;
        }
        
        // argsList.size() == 2
        final String algorithmName = argsList.get(0);
        final String inputFilePrefix = argsList.get(1);
        if (algorithmName.equals(ALL_FAST_STRING)) {
            // run all non-tabu algorithms on one file
            RegretExperimentRunner.runFastRegretExperiments(
                inputFilePrefix
            );
        } else {
            final SearchAlgorithm algorithm = 
                ProblemGenerator.getSearchAlgorithm(algorithmName);
            final int slowDeviations = 25;
            final int fastDeviations = 25;
            int deviations = fastDeviations;
            if (algorithm instanceof TabuSearchAlgorithm) {
                deviations = slowDeviations;
            }
            // run specific algorithm on specific file
            RegretExperimentRunner.runRegretExperiment(
                algorithm, 
                ExperimentRunner.SOLVER_NAME, 
                inputFilePrefix, 
                deviations
            );
        }
    }
    
    private static boolean checkArgs(final List<String> args) {
        if (args == null || args.size() < 1) {
            System.out.println("Expected arguments.");
            return false;
        }
        
        if (args.size() == 1) {
            if (!args.get(0).equals(HELP_ARG)) {
                return false;
            }
        }
        
        if (args.size() > 2) {
            System.out.println("Too many arguments.");
        }
        
        if (args.size() == 2) {
            final String algorithmName = args.get(0);
            final String inputFilePrefix = args.get(1);
            
            if (!ProblemGenerator.getAlgorithmNames().contains(algorithmName)) {
                if (!algorithmName.equals(ALL_FAST_STRING)) {
                    System.out.println("Invalid algorithm name.");
                    printAlgorithmMessage();
                    return false;
                }
            }
            
            if (!ProblemGenerator.inputPrefixes().contains(inputFilePrefix)) {
                System.out.println("Invalid input file prefix.");
                printInputFileMessage();
                return false;
            }
        }
        
        return true;
    }
    
    private static void printUsageMessage() {
        System.out.println("Usage:");
        System.out.println(
            "java -jar coalitiongames.jar algorithmName inputFilePrefix"
        );
        System.out.println("Or:");
        System.out.println("java -jar coalitiongames.jar help");
    }
    
    private static void printAlgorithmMessage() {
        System.out.println("Algorithm names:");
        List<String> algorithmNames = ProblemGenerator.getAlgorithmNames();
        ArrayList<String> algorithmNamesArrList = 
            new ArrayList<String>(algorithmNames);
        // don't allow randomized algorithms
        algorithmNamesArrList.remove("randomAny");
        algorithmNamesArrList.remove("randomOpt");
        System.out.println(
            algorithmNamesArrList 
            + " or " + ALL_FAST_STRING
        );
    }
    
    private static void printInputFileMessage() {
        System.out.println("Input file prefixes:");
        System.out.println(
            ProblemGenerator.inputPrefixes()
        );
    }
}
