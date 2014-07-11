package regretexperiment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import experiment.FileHandler;

public abstract class RegretOutputPrinter {
    
    private static final String REGRET_OUTPUT_FOLDER_NAME = 
        "regretOutputFiles/";
    
    private static final String DESCRIPTION_STRING = "regretDescr";
    
    private static final String DESCRIPTION_HEADER = 
        "numberAgents,deviationsPerAgent," 
            + "algorithm,solver,dataFileName,numberOfTeams\n";
    
    private static final String SUMMARY_STRING = "regretSummary";
    
    private static final String SUMMARY_HEADER = 
        "runNumber,runTimeInMillis\n";
    
    private static final String RESULT_STRING = "regretResults";
    
    private static final String RESULTS_HEADER = 
        "runNumber,maxRegretTruthFraction," 
        + "maxRegretTruthFractionNoJitter,regretAgentIndex,"
        + "regretAgentRsdIndex,countRegretTruth,countRegretTruthNoJitter," 
        + "meanTruthRegretFraction\n";

    private static final char COMMA = ',';
    
    private static final char NEWLINE = '\n';
    
    private static final char UNDERBAR = '_';

    
    public static void printOutput(
        final List<RegretSearchResult> searchResults,
        final String algorithmName,
        final String solverName,
        final String inputFilePrefix
    ) {
        printDescriptionOutput(
            searchResults.get(0), algorithmName, solverName, inputFilePrefix
        );
        printSummaryOutput(searchResults, algorithmName, inputFilePrefix);
        printResultOutput(searchResults, algorithmName, inputFilePrefix);
    }
    
    private static void printDescriptionOutput(
        final RegretSearchResult searchResult,
        final String algorithmName,
        final String solverName,
        final String inputFilePrefix
    ) {
        final String descriptionFileName = 
            REGRET_OUTPUT_FOLDER_NAME + algorithmName + UNDERBAR 
            + inputFilePrefix + UNDERBAR 
            + DESCRIPTION_STRING + FileHandler.CSV_EXTENSION;
        final StringBuilder builder = new StringBuilder();
        builder.append(DESCRIPTION_HEADER);
        
        final int n = searchResult.getNumberAgents();
        final int deviationsPerAgent = searchResult.getDeviationsPerAgent();
        final int numberOfTeams = searchResult.getNumberOfTeams();
        builder.append(n).append(COMMA).
            append(deviationsPerAgent).append(COMMA).
            append(algorithmName).append(COMMA).
            append(solverName).append(COMMA).
            append(inputFilePrefix).append(COMMA).
            append(numberOfTeams).append(NEWLINE);
        FileHandler.writeToFile(descriptionFileName, builder.toString());
    }
    
    private static void printSummaryOutput(
        final List<RegretSearchResult> searchResults,
        final String algorithmName,
        final String inputFilePrefix
    ) {
        final String summaryFileName = 
            REGRET_OUTPUT_FOLDER_NAME + algorithmName + UNDERBAR 
            + inputFilePrefix + UNDERBAR 
            + SUMMARY_STRING + FileHandler.CSV_EXTENSION;
        final StringBuilder builder = new StringBuilder();
        builder.append(SUMMARY_HEADER);
        for (int i = 0; i < searchResults.size(); i++) {
            final RegretSearchResult searchResult = searchResults.get(i);
            final int runNumber = i + 1;
            final long runTimeInMillis = searchResult.getDurationMillis();
            builder.append(runNumber).append(COMMA).
                append(runTimeInMillis).append(NEWLINE);
        }
        
        FileHandler.writeToFile(summaryFileName, builder.toString());
    }
    
    private static void printResultOutput(
        final List<RegretSearchResult> searchResults,
        final String algorithmName,
        final String inputFilePrefix
    ) {
        final String resultsFileName = 
            REGRET_OUTPUT_FOLDER_NAME + algorithmName + UNDERBAR 
            + inputFilePrefix + UNDERBAR 
            + RESULT_STRING + FileHandler.CSV_EXTENSION;
        Writer output = null;
        try {
            output = new BufferedWriter(
                new FileWriter(
                    FileHandler.getFileAndCreateIfNeeded(resultsFileName)
                ));
            output.write(RESULTS_HEADER);
            
            for (
                int resultIndex = 0; 
                resultIndex < searchResults.size(); 
                resultIndex++
            ) {
                final RegretSearchResult result = 
                    searchResults.get(resultIndex);
                final int runNumber = resultIndex + 1;
                final double maxRegretTruthFraction = 
                    result.getMaxRegretFromTruthFraction();
                final double maxRegretTruthFractionNoJitter = 
                    result.getMaxRegretFromTruthFractionNoJitter();
                final int regretAgentIndex = result.getMostRegretAgentIndex();
                final int regretAgentRsdIndex = 
                    result.getMostRegretAgentRsdIndex();
                final int countRegretTruth = 
                    result.getAgentsWithRegretFromTruth();
                final int countRegretTruthNoJitter = 
                    result.getAgentsWithRegretFromTruthNoJitter();
                final double meanTruthRegretFraction = 
                    result.getMeanRegretFromTruthFraction();
                
                final StringBuilder sb = new StringBuilder();
                sb.append(runNumber).append(COMMA).
                    append(maxRegretTruthFraction).append(COMMA).
                    append(maxRegretTruthFractionNoJitter).append(COMMA).
                    append(regretAgentIndex).append(COMMA).
                    append(regretAgentRsdIndex).append(COMMA).
                    append(countRegretTruth).append(COMMA).
                    append(countRegretTruthNoJitter).append(COMMA).
                    append(meanTruthRegretFraction).append(NEWLINE);
                output.write(sb.toString());
            }
            
            output.write(NEWLINE);
            output.flush();
            output.close();
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
}
