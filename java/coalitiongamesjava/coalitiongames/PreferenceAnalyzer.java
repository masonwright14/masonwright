package coalitiongames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class PreferenceAnalyzer {

    public static void main(final String[] args) {
        final Double[] arr1 = {5.0, 4.0, -1.2};
        List<Double> list1 = Arrays.asList(arr1);
        final Double[] arr2 = {3.0, 7.0, -4.0};
        List<Double> list2 = Arrays.asList(arr2);   
        final Double[] arr3 = {-1.0, -4.5, 15.0};
        List<Double> list3 = Arrays.asList(arr3);   

        System.out.println("1 vs. 2: " + getCosineSimilarity(list1, list2));
        System.out.println("1 vs. 3: " + getCosineSimilarity(list1, list3));
        System.out.println("2 vs. 3: " + getCosineSimilarity(list2, list3));
        
        List<List<Double>> allDemands = new ArrayList<List<Double>>();
        allDemands.add(list1);
        allDemands.add(list2);
        allDemands.add(list3);
        System.out.println(
            "Overall similarity: " 
            + getMeanPairwiseCosineSimilarityFromDemands(allDemands)
        );
    }
    
    public static double getMeanPairwiseCosineSimilarity(
        final List<Agent> agents
    ) {
        final List<List<Double>> agentDemands = new ArrayList<List<Double>>();
        for (final Agent agent: agents) {
            agentDemands.add(agent.getValues());
        }
        
        return getMeanPairwiseCosineSimilarityFromDemands(agentDemands);
    }
    
    public static double getMeanPairwiseCosineSimilarityMedianBias(
        final List<Agent> agents
    ) {
        final List<List<Double>> agentDemands = new ArrayList<List<Double>>();
        for (final Agent agent: agents) {
            final double median = getMedian(agent.getValues());
            final List<Double> row = new ArrayList<Double>();
            for (Double value: agent.getValues()) {
                row.add(value - median);
            }
            agentDemands.add(row);
        }
        
        return getMeanPairwiseCosineSimilarityFromDemands(agentDemands);
    }
    
    private static double getMedian(final List<Double> values) {
        final List<Double> copyList = new ArrayList<Double>(values);
        Collections.sort(copyList);
        if (copyList.size() % 2 == 0) {
            final double item1 = copyList.get((copyList.size() / 2) - 1);
            final double item2 = copyList.get(copyList.size() / 2);
            return (item1 + item2) / 2.0;
        }
        
        return copyList.get(copyList.size() / 2);
    }
    
    /**
     * @param agents a list of agents with their cardinal preferences, 
     * which presumably have jitter added in (0, 1) to each item.
     * @return the mean of pairwise cosine similarities between each
     * pair of agents' values vectors, excluding the 2 dimensions of
     * the agents in the pair. all values are floored before taking the
     * similarity measure, to remove the effect of jitter.
     */
    private static double getMeanPairwiseCosineSimilarityFromDemands(
        final List<List<Double>> agentDemands
    ) {
        assert agentDemands.size() > 1;
        double totalSimilarity = 0.0;
        int pairCount = 0;
        for (int i = 0; i < agentDemands.size(); i++) {
            final List<Double> iAgentDemand = agentDemands.get(i);
            for (int j = i + 1; j < agentDemands.size(); j++) {
                final List<Double> jAgentDemand = agentDemands.get(j);
                final List<Double> iAgentValuesFloored = 
                    getFlooredValues(getValuesWithout(iAgentDemand, i, j));
                final List<Double> jAgentValuesFloored = 
                    getFlooredValues(getValuesWithout(jAgentDemand, j, i));
                final double cosineSimilarity = 
                    getCosineSimilarity(
                        iAgentValuesFloored, jAgentValuesFloored
                    );
                totalSimilarity += cosineSimilarity;
                pairCount++;
            }
        }
        
        if (pairCount == 0) {
            return 0.0;
        }
        
        final double result = totalSimilarity / pairCount;
        assert result >= -1.0 && result <= 1.0;
        return result;
    }
    
    private static List<Double> getFlooredValues(
        final List<Double> input
    ) {
        final List<Double> result = new ArrayList<Double>();
        for (final Double item: input) {
            result.add(Math.floor(item));
        }
        return result;
    }
    
    private static List<Double> getValuesWithout(
        final List<Double> values,
        final int selfIndex,
        final int otherIndex
    ) {
        final List<Double> copyValues = new ArrayList<Double>(values);
        assert selfIndex != otherIndex;
        if (selfIndex < otherIndex) {
            // index of other agent in values is 1 lower than in agents,
            // because the self agent is already absent.
            copyValues.remove(otherIndex - 1);
        } else {
            copyValues.remove(otherIndex);
        }
        
        return copyValues;
    }
    
    /*
     *  cos theta = (A dot B) / (|A| |B|)
        A dot B = componentwise product of A, B
        |A| = sqrt of sum of squares in A
        |B| = sqrt of sum of squares in B
     */
    private static double getCosineSimilarity(
        final List<Double> a,
        final List<Double> b
    ) {
       final double aDotB = dot(a, b);
       final double aNorm = l2Norm(a);
       final double bNorm = l2Norm(b);
       if (aNorm == 0 || bNorm == 0) {
           return 0.0;
       }
       
       final double result = (aDotB / (aNorm * bNorm));
       if (result < -1.0 || result > 1.0) {
           System.out.println("ERROR: " + result);
           System.out.println(a);
           System.out.println(b);
           System.out.println(aDotB);
           System.out.println(aNorm);
           System.out.println(bNorm);
       }
       assert result >= -1.0 && result <= 1.0;
       return result;
    }
    
    private static double l2Norm(final List<Double> vec) {
        return Math.sqrt(dot(vec, vec));
    }
    
    private static double dot(
        final List<Double> a, 
        final List<Double> b
    ) {
        double result = 0.0;
        for (int i = 0; i < a.size(); i++) {
            result += a.get(i) * b.get(i);
        }
        return result;
    }
}
