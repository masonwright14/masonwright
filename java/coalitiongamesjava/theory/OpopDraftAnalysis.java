package theory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import regretexperiment.RegretProblemGenerator;
import coalitiongames.Agent;
import coalitiongames.DemandProblemGenerator;
import coalitiongames.EachAgentDraftAllocation;
import coalitiongames.SimpleSearchResult;

public class OpopDraftAnalysis {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final int numPlayers = 6;
        // testNext(numPlayers);
        final int teamSize = 3;
        analyzeOpopDraft(numPlayers, teamSize);
    }

    /*
     * Result:
     * Lying is an improvement in expectation: 7.8 vs. 7.737.
     * Lying performs better in 12 of 720 (6!) runs.
     * 
     * Gain from false report:
     * (ABC) -> (ACD) * 12. 12 (D - B). 4.9 * 12
     * 
     * Lose from false report:
     * (ACF) -> (ABD) * 6. 6 (B + D - C - F). 2.2 * 6
     * (ABC) -> (ABD) * 6. 6 (D - C). 0.1 * 6
     * 
     * 4.9 * 12 - 6 * 2.2 - 6 * 0.1 = 45
     * 
     * total increase:
     * 12D - 12B + 6B + 6D - 6C - 6F + 6D - 6C
     * = -6B - 12C + 24D - 6F
     * 
     * expected gain of false reporting: 45 / 720 = 0.0625
     * 5570.4 -> 5615.4
     * 7.737 -> 7.799
     * 
     * truthful values:
     *  A   B   C   D   E   F 
     *  x  0.0 5.0 4.9 6.0 2.1
     * 0.0  x  3.0 1.2 1.1 1.0
     * 0.0 3.0  x  1.2 1.1 1.0
     * 0.0 3.1 3.0  x  1.1 1.0
     * 0.0 1.1 1.0 3.1  x  3.0
     * 0.0 1.1 1.0 3.1 3.0  x 
     * 
     * false report:
     *  x  0.0 5.0 5.5 6.0 1.6
     */
    public static void analyzeOpopDraft(
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
                EachAgentDraftAllocation.eachAgentDraftAllocation(
                    truthfulAgents,
                    teamSize,
                    rsdOrder
                );
            final SimpleSearchResult falseSearchResult = 
                EachAgentDraftAllocation.eachAgentDraftAllocation(
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

            } else if (falseSelfTrueUtility < truthfulSelfUtility) {
                countFalseWorse++;

                System.out.println(rsdOrder);
                System.out.println("false allocation:");
                System.out.println(falseSearchResult.getAllocation().get(selfAgentIndex));
                System.out.println("truthful allocation:");
                System.out.println(falseSelfTrueUtility - truthfulSelfUtility);
                System.out.println(truthfulSearchResult.getAllocation().get(selfAgentIndex));
                System.out.println();
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
    
    /*
     * Better: 18 times
     * (ABF) -> (ABC) * 6. 6 * (C - F)
     * (ABF) -> (ACE) * 6. 6 * (C + E - B - F)
     * (ABE) -> (ABC) * 6. 6 * (C - E)
     * 
     * Worse: 6 times
     * (ABE) -> (ACE). 6 * (C - B)
     * 
     * total change:
     * 6C - 6F + 6C + 6E - 6B - 6F + 6C - 6E + 6C - 6B
     * = -12B + 24C - 12F
     * to increase:
     * increase C
     * reduce B
     * preserve: C < B
     * 
     * truthful values:
     *  A   B   C   D   E   F 
     *  x  5.0 4.9 7.0 0.2 0.0
     * 0.0  x  1.1 1.4 1.2 1.3
     * 0.0 1.1  x  1.4 1.2 1.3
     * 0.0 1.1 1.4  x  1.2 1.3
     * 0.0 1.1 1.4 1.2  x  1.3
     * 0.0 1.1 1.4 1.2 1.3  x 
     * 
     * false report:
     *  x  5.0 6.0 7.0 0.2 0.0
     *  
     *  worse cases:
     *  order is any of 6 like ADC???. leads from (ABE) -> (ACE). 6 * -0.1
     *  
     *  better cases:
     *  order is any of 6 like ADB???. leads from (ABF) -> (ACE). 6 * 0.1
     *  order is any of 6 like ADF???. leads from (ABE) -> (ABC). 6 * 4.7
     *  order is any of 6 like ADE???. leads from (ABF) -> (ABC). 6 * 4.9
     *  total gain: 9.6 * 6 = 57.6
     *  mean gain: 57.6 / 720 = 0.08
     */
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
    
    /*
     * NB: Must use same ids for both truthful and not truthful agents.
     */
    @SuppressWarnings("unused")
    private static List<Agent> getAgents(
        final boolean isTruthful,
        final List<UUID> ids
    ) {
        final List<Agent> result = new ArrayList<Agent>();
        final double budget = 0.0;
        int currentId = 0;
        
        if (isTruthful) {
            Double[] aValues = {0.0, 5.0, 4.9, 6.0, 2.1};
            final Agent a = new Agent(
                    Arrays.asList(aValues),
                    DemandProblemGenerator.getUuidsWithout(ids, currentId),
                    budget,
                    currentId,
                    ids.get(currentId)
                );
            result.add(a);
        } else {
            Double[] aValues = {0.0, 5.0, 5.5, 6.0, 1.6};
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
        Double[] bValues = {0.0, 3.0, 1.2, 1.1, 1.0};
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
        Double[] dValues = {0.0, 3.1, 3.0, 1.1, 1.0};
        final Agent d = new Agent(
            Arrays.asList(dValues),
            DemandProblemGenerator.getUuidsWithout(ids, currentId),
            budget,
            currentId,
            ids.get(currentId)
        );
        result.add(d);
        
        currentId++;
        Double[] eValues = {0.0, 1.1, 1.0, 3.1, 3.0};
        final Agent e = new Agent(
            Arrays.asList(eValues),
            DemandProblemGenerator.getUuidsWithout(ids, currentId),
            budget,
            currentId,
            ids.get(currentId)
        );
        result.add(e);
        
        currentId++;
        final Agent f = new Agent(
            Arrays.asList(eValues),
            DemandProblemGenerator.getUuidsWithout(ids, currentId),
            budget,
            currentId,
            ids.get(currentId)
        );
        result.add(f);
        
        return result;
    }
    
    @SuppressWarnings("unused")
    private static void testNext(final int n) {
        List<Integer> initial = RsdAnalysis.getIntList(n);
        int count = 1;
        System.out.println(initial);
        initial = RsdAnalysis.next(initial);
        while (initial != null) {
            count++;
            System.out.println(initial);
            initial = RsdAnalysis.next(initial);
        }
        System.out.println("total: " + count);
        if (count != RsdAnalysis.factorial(n)) {
            throw new IllegalStateException();
        }
    }
}
