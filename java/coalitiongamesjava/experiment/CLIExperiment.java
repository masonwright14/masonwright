package experiment;

import java.util.Arrays;
import java.util.List;

import experiment.ProblemGenerator.SearchAlgorithm;

public abstract class CLIExperiment {
    
    private static final String HELP_ARG = "help";

    private static final String ALL_NOT_TABU_STRING = "allNotTabu";
    
    private static final String ALL_FILES_STRING = "all";
    
    /*
     * Assumptions:
     * Has directories /inputFiles and /outputFiles
     * 
     * 3 special cases:
     * all fast algorithms, specific file
     * one algorithm, all files
     * all fast algorithms, all files
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
        if (algorithmName.equals(ALL_NOT_TABU_STRING)) {
            if (inputFilePrefix.equals(ALL_FILES_STRING)) {
                // run all non-tabu algorithms on all files
                ExperimentRunner.runAllExperimentsOfType(false);
            } else {
                // run all non-tabu algorithms on one file
                ExperimentRunner.runExperimentsForInputFilePrefixOfType(
                    ExperimentRunner.SOLVER_NAME, 
                    inputFilePrefix,
                    false // not tabu algorithms
                );
            }
        } else {
            final SearchAlgorithm algorithm = 
                ProblemGenerator.getSearchAlgorithm(algorithmName);
            if (inputFilePrefix.equals(ALL_FILES_STRING)) {
                // run specific algorithm on all files
                ExperimentRunner.runAllExperimentsOfSubtype(algorithm);
            } else {
                // run specific algorithm on specific file
                ExperimentRunner.runExperiment(
                    algorithm, ExperimentRunner.SOLVER_NAME, inputFilePrefix
                );
            }
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
                if (!algorithmName.equals(ALL_NOT_TABU_STRING)) {
                    System.out.println("Invalid algorithm name.");
                    printAlgorithmMessage();
                    return false;
                }
            }
            
            if (!ProblemGenerator.inputPrefixes().contains(inputFilePrefix)) {
                if (!inputFilePrefix.equals(ALL_FILES_STRING)) {
                    System.out.println("Invalid input file prefix.");
                    printInputFileMessage();
                    return false;
                }
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
        System.out.println(
            ProblemGenerator.getAlgorithmNames() 
            + " or " + ALL_NOT_TABU_STRING
        );
    }
    
    private static void printInputFileMessage() {
        System.out.println("Input file prefixes:");
        System.out.println(
            ProblemGenerator.inputPrefixes() + " or " + ALL_FILES_STRING
        );
    }
}
