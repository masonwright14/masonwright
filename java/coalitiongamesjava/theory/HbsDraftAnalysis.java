package theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import regretexperiment.RegretProblemGenerator;
import coalitiongames.Agent;
import coalitiongames.DemandProblemGenerator;
import coalitiongames.DraftAllocation;
import coalitiongames.SimpleSearchResult;

public class HbsDraftAnalysis {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final int numPlayers = 6;
        // testNext(numPlayers);
        final int teamSize = 3;
        analyzeHbsDraft(numPlayers, teamSize);
    }

    public static void analyzeHbsDraft(
        final int numPlayers,
        final int teamSize
    ) {
        final int minPlayers = 4;
        if (numPlayers < minPlayers) {
            throw new IllegalArgumentException();
        }
        if (teamSize <= 1 || teamSize >= numPlayers) {
            throw new IllegalArgumentException();
        }
        if (numPlayers % teamSize != 0) {
            throw new IllegalArgumentException();
        }
        final int maxPlayers = 12;
        if (numPlayers > maxPlayers) {
            throw new IllegalArgumentException("too many agents");
        }
        
        final int iterations = RsdAnalysis.factorial(numPlayers);
        
        List<Integer> rsdOrder = RsdAnalysis.getIntList(numPlayers);
        final List<UUID> ids = DemandProblemGenerator.getUuids(numPlayers);
        final List<Agent> truthfulAgents = getAgentsSimple(true, ids);
        final List<Agent> falseAgents = getAgentsSimple(false, ids);
        int selfAgentIndex = 0;
        double meanTruthfulUtility = 0;
        double meanFalseUtility = 0;
        int currentIteration = 0;
        int countFalseBetter = 0;
        int countFalseWorse = 0;
        while (rsdOrder != null) {
            final SimpleSearchResult truthfulSearchResult = 
                DraftAllocation.draftAllocation(
                    truthfulAgents,
                    teamSize,
                    rsdOrder
                );
            final SimpleSearchResult falseSearchResult = 
                DraftAllocation.draftAllocation(
                    falseAgents,
                    teamSize,
                    rsdOrder
                );
            
            final double truthfulSelfUtility = 
                truthfulSearchResult.getTeamUtilities().get(selfAgentIndex);
            
            final double falseSelfTrueUtility =
                RegretProblemGenerator.getTruthfulTeamUtility(
                    falseAgents, selfAgentIndex, 
                    truthfulAgents.get(0), falseSearchResult
                );
            
            if (falseSelfTrueUtility > truthfulSelfUtility) {
                countFalseBetter++;

                System.out.println(rsdOrder);
                System.out.println("false allocation:");
                System.out.println(falseSearchResult.getAllocation().get(selfAgentIndex));
                System.out.println("truthful allocation:");
                System.out.println(falseSelfTrueUtility - truthfulSelfUtility);
                System.out.println(falseSelfTrueUtility);
                System.out.println(truthfulSearchResult.getAllocation().get(selfAgentIndex));
                System.out.println();
            } else if (falseSelfTrueUtility < truthfulSelfUtility) {
                countFalseWorse++;
            }
            
            meanTruthfulUtility += truthfulSelfUtility;
            meanFalseUtility += falseSelfTrueUtility;
            
            currentIteration++;
            rsdOrder = RsdAnalysis.next(rsdOrder);
        }
        
        if (currentIteration != iterations) {
            throw new IllegalStateException();
        }
        System.out.println("total truthful utility: " + meanTruthfulUtility);
        System.out.println("total false utility: " + meanFalseUtility);
        meanTruthfulUtility /= iterations;
        meanFalseUtility /= iterations;
        System.out.println(
            "Is true preference revealing: " 
            + (meanTruthfulUtility > meanFalseUtility)
        );
        System.out.println("Expected truthful utility: " + meanTruthfulUtility);
        System.out.println("Expected false utility: " + meanFalseUtility);
        System.out.println(
            "Iterations where false was better: " + countFalseBetter
        );
        System.out.println(
            "Iterations where false was worse: " + countFalseWorse
        );
    }
    
    private static List<Agent> getAgentsSimple(
        final boolean isTruthful,
        final List<UUID> ids
    ) {
        final List<Agent> result = new ArrayList<Agent>();
        final double budget = 0.0;
        int currentId = 0;
        
        if (isTruthful) {
            Double[] aValues = {5.0, 4.9, 7.0, 0.2, 0.0};
            final Agent a = new Agent(
                    Arrays.asList(aValues),
                    DemandProblemGenerator.getUuidsWithout(ids, currentId),
                    budget,
                    currentId,
                    ids.get(currentId)
                );
            result.add(a);
        } else {
            Double[] aValues = {5.0, 6.0, 7.0, 0.2, 0.0};
            final Agent a = new Agent(
                    Arrays.asList(aValues),
                    DemandProblemGenerator.getUuidsWithout(ids, currentId),
                    budget,
                    currentId,
                    ids.get(currentId)
                );
            result.add(a);
        }

        currentId++;
        Double[] bValues = {0.0, 1.1, 1.6, 1.2, 1.3};
        final Agent b = new Agent(
            Arrays.asList(bValues),
            DemandProblemGenerator.getUuidsWithout(ids, currentId),
            budget,
            currentId,
            ids.get(currentId)
        );
        result.add(b);
        
        currentId++;
        final Agent c = new Agent(
            Arrays.asList(bValues),
            DemandProblemGenerator.getUuidsWithout(ids, currentId),
            budget,
            currentId,
            ids.get(currentId)
        );
        result.add(c);
        
        currentId++;
        final Agent d = new Agent(
            Arrays.asList(bValues),
            DemandProblemGenerator.getUuidsWithout(ids, currentId),
            budget,
            currentId,
            ids.get(currentId)
        );
        result.add(d);
        
        currentId++;
        final Agent e = new Agent(
            Arrays.asList(bValues),
            DemandProblemGenerator.getUuidsWithout(ids, currentId),
            budget,
            currentId,
            ids.get(currentId)
        );
        result.add(e);
        
        currentId++;
        final Agent f = new Agent(
            Arrays.asList(bValues),
            DemandProblemGenerator.getUuidsWithout(ids, currentId),
            budget,
            currentId,
            ids.get(currentId)
        );
        result.add(f);
        
        return result;
    }
}
