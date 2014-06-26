package experiment;

import java.util.Arrays;
import java.util.List;

import experiment.ProblemGenerator.SearchAlgorithm;

public abstract class CLIExperiment {
    
    private static final String HELP_ARG = "help";

    
    /*
     * Assumptions:
     * Has directories /inputFiles and /outputFiles
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
                System.out.println("Algorithm names:");
                System.out.println(ProblemGenerator.getAlgorithmNames());
                System.out.println("Input file prefixes:");
                System.out.println(ProblemGenerator.inputPrefixes());
            }
            return;
        }
        
        // argsList.size() == 2
        final String algorithmName = argsList.get(0);
        final String inputFilePrefix = argsList.get(1);
        final SearchAlgorithm algorithm = 
            ProblemGenerator.getSearchAlgorithm(algorithmName);
        ExperimentRunner.runExperiment(
            algorithm, ExperimentRunner.SOLVER_NAME, inputFilePrefix
        );
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
                System.out.println("Invalid algorithm name.");
                System.out.println("Algorithm names:");
                System.out.println(ProblemGenerator.getAlgorithmNames());
                return false;
            }
            
            if (!ProblemGenerator.inputPrefixes().contains(inputFilePrefix)) {
                System.out.println("Invalid input file prefix.");
                System.out.println("Input file prefixes:");
                System.out.println(ProblemGenerator.inputPrefixes());
                return false;
            }
        }
        
        return true;
    }
    
    private static void printUsageMessage() {
        System.out.println("Usage:");
        System.out.println(
            "java -jar CLIExperiment.jar algorithmName inputFilePrefix"
        );
        System.out.println("Or:");
        System.out.println("java -jar CLIExperiment.jar help");
    }
}
