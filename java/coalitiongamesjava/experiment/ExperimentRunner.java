package experiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import coalitiongames.SimpleSearchResult;
import experiment.ProblemGenerator.SearchAlgorithm;
import experiment.ProblemGenerator.SimpleSearchAlgorithm;
import experiment.ProblemGenerator.TabuSearchAlgorithm;

public abstract class ExperimentRunner {

    private static final String INPUT_FOLDER_NAME = "inputFiles";
    
    private static final String MEAN_RANK_OUTPUT_FOLDER_NAME = "meanRankFiles";
    
    public static final String SOLVER_NAME = "cplex";
    
    public static void main(final String[] args) {
        /*
        final SearchAlgorithm algorithm = TabuSearchAlgorithm.TABU_ALL_OPT;
        final String inputFilePrefix = "newfrat";
        runExperiment(algorithm, SOLVER_NAME, inputFilePrefix);
        */
        
        // runAllExperimentsOfType(false);
        
        /*
        runExperiment(
            TabuSearchAlgorithm.TABU_ALL_OPT, SOLVER_NAME, "random_20_agents"
        );
        runExperiment(
            TabuSearchAlgorithm.TABU_ALL, SOLVER_NAME, "random_20_agents"
        );
        runExperiment(
            TabuSearchAlgorithm.TABU_ONE, SOLVER_NAME, "random_20_agents"
        );
        */
        
        /*
        runExperiment(
            TabuSearchAlgorithm.TABU_ALL_OPT, SOLVER_NAME, "vand"
        );
        */
        
        /*
        runExperiment(
            TabuSearchAlgorithm.TABU_EACH, SOLVER_NAME, "rndUncor_20_agents"
        );
        */
        
        runExperiment(
            TabuSearchAlgorithm.TABU_ALL_OPT_SPITL, SOLVER_NAME, "newfrat"
        );
        
        /*
        final String fileName = "rndUncor_200_agents";
        runExperiment(SimpleSearchAlgorithm.DRAFT, SOLVER_NAME, fileName);
        runExperiment(SimpleSearchAlgorithm.RANDOM_ANY, SOLVER_NAME, fileName);
        runExperiment(SimpleSearchAlgorithm.RANDOM_OPT, SOLVER_NAME, fileName);
        runExperiment(SimpleSearchAlgorithm.RSD_GREEDY, SOLVER_NAME, fileName);
        runExperiment(SimpleSearchAlgorithm.RSD_OPT, SOLVER_NAME, fileName);
        */
        
        // runAllExperimentsOfSubtype(SimpleSearchAlgorithm.EACH_DRAFT_CC);
        
        // printMeanCosineSimilarities();
        
        printAgentMeanRanksAndValues();
    }
    
    public static void runAllExperimentsOfType(final boolean isTabu) {
        // time the duration of the search to the millisecond
        final long experimentStartMillis = new Date().getTime();
        for (final String inputPrefix: ProblemGenerator.INPUT_PREFIX_ARRAY) {
            runExperimentsForInputFilePrefixOfType(
                SOLVER_NAME, inputPrefix, isTabu
            );
        }
        final long experimentDurationMillis = 
            new Date().getTime() - experimentStartMillis;
        System.out.println(
            "Ran all experiments: " + experimentDurationMillis + " millis"
        );
    }
    
    public static void runAllExperimentsOfSubtype(
        final SearchAlgorithm algorithm
    ) {
        // time the duration of the search to the millisecond
        final long experimentStartMillis = new Date().getTime();
        for (final String inputPrefix: ProblemGenerator.INPUT_PREFIX_ARRAY) {
            runExperiment(algorithm, SOLVER_NAME, inputPrefix);
        }
        final long experimentDurationMillis = 
            new Date().getTime() - experimentStartMillis;
        System.out.println(
            "Ran all experiments: " + experimentDurationMillis + " millis"
        );
    }
    
    public static void runAllExperiments() {
       // time the duration of the search to the millisecond
       final long experimentStartMillis = new Date().getTime();
       for (final String inputPrefix: ProblemGenerator.INPUT_PREFIX_ARRAY) {
           runExperimentsForInputFilePrefix(SOLVER_NAME, inputPrefix);
       }
       final long experimentDurationMillis = 
           new Date().getTime() - experimentStartMillis;
       System.out.println(
           "Ran all experiments: " + experimentDurationMillis + " millis"
       );
    }
    
    public static void runExperimentsForInputFilePrefixOfType(
        final String solverName,
        final String inputFilePrefix,
        final boolean isTabu
    ) {
        final long inputStartMillis = new Date().getTime();
        for (
            final SearchAlgorithm algorithm
            : ProblemGenerator.ALGORITHM_ARRAY
        ) {
            if (algorithm instanceof TabuSearchAlgorithm == isTabu) {
                runExperiment(algorithm, solverName, inputFilePrefix);
            }
        }
        final long inputDurationMillis = 
            new Date().getTime() - inputStartMillis;
        System.out.println(
            "\tRan all experiments for input: " + inputFilePrefix + " in " 
            + inputDurationMillis + " millis"
        );
    }
    
    private static void runExperimentsForInputFilePrefix(
        final String solverName,
        final String inputFilePrefix
    ) {
        final long inputStartMillis = new Date().getTime();
        for (
            final SearchAlgorithm algorithm
            : ProblemGenerator.ALGORITHM_ARRAY
        ) {
            runExperiment(algorithm, solverName, inputFilePrefix);
        }
        final long inputDurationMillis = 
            new Date().getTime() - inputStartMillis;
        System.out.println(
            "\tRan all experiments for input: " + inputFilePrefix + " in " 
            + inputDurationMillis + " millis"
        );
}
    
    public static void runExperiment(
        final SearchAlgorithm algorithm,
        final String solverName,
        final String inputFilePrefix
    ) {
        final String algorithmName = 
            ProblemGenerator.getAlgorithmName(algorithm);
        final int runCount = countInputFilesByPrefix(inputFilePrefix);
        final boolean isSimpleSearch = 
            algorithm instanceof SimpleSearchAlgorithm;
        final List<SimpleSearchResult> allResults = 
            new ArrayList<SimpleSearchResult>();
        final long experimentStartMillis = new Date().getTime();
        for (int runNumber = 1; runNumber <= runCount; runNumber++) {
            final String inputFileName = INPUT_FOLDER_NAME + "/" 
                + inputFilePrefix + "_" 
                + runNumber + FileHandler.TEXT_EXTENSION;
            
            if (isSimpleSearch) {
                SimpleSearchAlgorithm simpleAlgorithm = 
                    (SimpleSearchAlgorithm) algorithm;
                final SimpleSearchResult simpleSearchResult = 
                    ProblemGenerator.getSimpleSearchResult(
                        inputFileName, simpleAlgorithm
                    );
                allResults.add(simpleSearchResult);
            } else {
                final long runStartMillis = new Date().getTime();
                TabuSearchAlgorithm tabuAlgorithm = 
                    (TabuSearchAlgorithm) algorithm;
                final SimpleSearchResult searchResult =
                    ProblemGenerator.getTabuSearchResult(
                        inputFileName, tabuAlgorithm
                    );
                allResults.add(searchResult);
                final long runDurationMillis =
                    new Date().getTime() - runStartMillis;
                System.out.println(
                    "\t\t\tRan iteration " + runNumber + " for solver " 
                    + algorithmName + " for input " + inputFilePrefix
                    + " in " + runDurationMillis + " millis"
                );
            }
        }
        
        final long experimentDurationMillis = 
            new Date().getTime() - experimentStartMillis;
        System.out.println(
            "\t\tRan solver " + algorithmName 
            + " for input: " + inputFilePrefix + " in " 
            + experimentDurationMillis + " millis"
        );
        
        OutputPrinter.printOutput(
            allResults, algorithmName, solverName, inputFilePrefix
        );
    }
    
    private static int countInputFilesByPrefix(final String prefix) {
        return getInputFileNamesByPrefix(prefix).size();
    }
    
    private static List<String> getInputFileNames() {
        File inputFolder = new File(INPUT_FOLDER_NAME);
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
    
    public static void printMeanCosineSimilarities() {
        for (String inputFilePrefix: ProblemGenerator.INPUT_PREFIX_ARRAY) {
            double total = 0.0;
            final int numRuns = 20;
            for (int runNumber = 1; runNumber <= numRuns; runNumber++) {
                final String inputFileName = INPUT_FOLDER_NAME + "/" 
                    + inputFilePrefix + "_" 
                    + runNumber + FileHandler.TEXT_EXTENSION;
                
                SimpleSearchResult result = 
                    ProblemGenerator.getSimpleSearchResult(
                    inputFileName, SimpleSearchAlgorithm.RANDOM_OPT
                );
                total += result.getMeanPairwiseCosineSimilarity();
            }
            final double meanSimilarity = total / numRuns;
            final DecimalFormat df = new DecimalFormat("#.###");
            System.out.println(
                inputFilePrefix + "," + df.format(meanSimilarity)
            );
        }
    }
    
    /*
     * inputPrefix_ranks.csv
     * first column: playerId (0-based, from 0 to (N - 1))
     * second column: meanRank
     * third column: meanValue
     * 3 columns, (N + 1) rows with header
     * CSV format
     */
    public static void printAgentMeanRanksAndValues() {
        Writer output = null;
        
        final String header = "playerId,meanRank,meanValue\n";
        final DecimalFormat df = new DecimalFormat("#.###");
        try {
            for (String inputFilePrefix: ProblemGenerator.INPUT_PREFIX_ARRAY) {
                final String outputFileName = MEAN_RANK_OUTPUT_FOLDER_NAME + "/"
                    + inputFilePrefix + "_ranks" + FileHandler.CSV_EXTENSION;
                output = new BufferedWriter(
                    new FileWriter(
                        FileHandler.getFileAndCreateIfNeeded(outputFileName)
                    ));
                output.write(header);
                
                final int runNumber = 1;
                final String inputFileName = INPUT_FOLDER_NAME + "/" 
                    + inputFilePrefix + "_" 
                    + runNumber + FileHandler.TEXT_EXTENSION;
                
                SimpleSearchResult result = 
                    ProblemGenerator.getSimpleSearchResult(
                    inputFileName, SimpleSearchAlgorithm.RANDOM_OPT
                );
                final List<Double> utilities = 
                    result.getMeanAgentUtilitiesNoJitter();
                final List<Double> ranks = 
                    result.getMeanAgentRanksNoJitter();
                for (int i = 0; i < result.getAgents().size(); i++) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(i).append(',').
                    append(df.format(ranks.get(i))).
                    append(',').append(df.format(utilities.get(i))).
                    append('\n');
                    output.write(builder.toString());
                }
                
                System.out.println(inputFilePrefix);
                output.write('\n');
                output.flush();
                output.close();
            }

        } catch (IOException e) {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
            return;
        }

    }
    
    /*
    private static String listRoundedToString(final List<Double> input) {
        final StringBuilder builder = new StringBuilder();
        final DecimalFormat df = new DecimalFormat("#.###");
        for (int i = 0; i < input.size(); i++) {
            builder.append(df.format(input.get(i)));
            if (i + 1 < input.size()) {
                builder.append(',');
            }
        }
        builder.append('\n');
        
        return builder.toString();
    }
    */
}
