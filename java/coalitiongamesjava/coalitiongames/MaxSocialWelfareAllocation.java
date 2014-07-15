package coalitiongames;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class MaxSocialWelfareAllocation {
    
    public static final double AGENT_UTILITY_BUDGET = 100.0;

    public static SimpleSearchResult maxSocialWelfareAllocation(
        final List<Agent> agents,
        final int kMax,
        final List<Integer> rsdOrder
    ) {
        final int minimumAgents = 4;
        assert agents != null && agents.size() >= minimumAgents;
        final int n = agents.size();
        assert kMax <= n;
        
        if (!verifyUtilityBudgets(agents)) {
            throw new IllegalArgumentException("incorrect utility budgets");
        }
        
        final List<Integer> teamSizes = 
            RsdUtil.getOptimalTeamSizeList(agents.size(), kMax);
        assert teamSizes.get(0) >= teamSizes.get(teamSizes.size() - 1);
        final int kMin = teamSizes.get(teamSizes.size() - 1);
       
        // time the duration of the search to the millisecond
        final long searchStartMillis = new Date().getTime();
        
        final MipResult mipResult = runMaxWelfareCPLEX(
            agents,
            kMax, 
            kMin
        );
        
        final List<Integer> roundedValues = 
            mipResult.getRoundedColumnValues();
        final List<List<Integer>> allocation = matrixFromList(roundedValues);
        
        final long searchDurationMillis = 
            new Date().getTime() - searchStartMillis;
        final double similarity = 
            PreferenceAnalyzer.getMeanPairwiseCosineSimilarity(agents);
        final List<Integer> captainIndexes = new ArrayList<Integer>();
        return new SimpleSearchResult(
            allocation, kMin, kMax, agents, rsdOrder, 
            searchDurationMillis, captainIndexes, similarity
        );
    }
    
    public static List<Double> normalizeUtility(final List<Double> oldUtility) {
        double totalUtility = 0.0;
        for (Double value: oldUtility) {
            totalUtility += value;
        }
        if (totalUtility <= 0.0) {
            return new ArrayList<Double>(oldUtility);
        }
        
        final double factor = AGENT_UTILITY_BUDGET / totalUtility;
        
        final List<Double> result = new ArrayList<Double>();
        for (Double value: oldUtility) {
            result.add(factor * value);
        }
        
        return result;
    }
    
    private static boolean verifyUtilityBudgets(final List<Agent> agents) {
        final double tolerance = 0.001;
        for (Agent agent: agents) {
            double totalUtility = 0.0;
            final List<Double> utilities = agent.getValues();
            for (Double value: utilities) {
                totalUtility += value;
            }
            
            if (Math.abs(totalUtility - AGENT_UTILITY_BUDGET) > tolerance) {
                return false;
            }
        }
        
        return true;
    }
    
    private static List<List<Double>> valueMatrix(final List<Agent> agents) {
        final List<List<Double>> result = new ArrayList<List<Double>>();
        for (int i = 0; i < agents.size(); i++) {
            final Agent agent = agents.get(i);
            final List<Double> values = 
                new ArrayList<Double>(agent.getValues());
            values.add(i, 0.0);
            result.add(values);
        }
        if (MipGenerator.DEBUGGING) {
            for (int i = 0; i < agents.size(); i++) {
                if (result.get(i).get(i) != 0.0) {
                    throw new IllegalStateException();
                }
                if (result.get(i).size() != agents.size()) {
                    throw new IllegalStateException();
                }
            }
        }
        
        return result;
    }
    
    private static List<Double> listFromMatrix(
        final List<List<Double>> matrix
    ) {
        final List<Double> result = new ArrayList<Double>();
        for (List<Double> row: matrix) {
            result.addAll(row);
        }
        return result;
    }
    
    private static List<List<Integer>> matrixFromList(
        final List<Integer> list
    ) {
        final List<List<Integer>> result = new ArrayList<List<Integer>>();
        final int colSize = (int) Math.sqrt(list.size());
        if (colSize * colSize != list.size()) {
            throw new IllegalArgumentException("not a square matrix list");
        }
        for (int i = 0; i < colSize; i++) {
            final List<Integer> current = new ArrayList<Integer>();
            for (int j = colSize * i; j < colSize * (i + 1); j++) {
                current.add(list.get(j));
            }
            assert current.size() == colSize;
            result.add(current);
        }
        
        assert result.size() == colSize;
        return result;
    }
    
    /**
     * 
     * @param matrixWidth 1-based. 1 is minimum
     * @param row 0-based, so top row is row 0
     * @param col 0-based, so left column is column 0
     * @return
     */
    private static int listIndex(
        final int matrixWidth, 
        final int row, 
        final int col
    ) {
        return row * matrixWidth + col;
    }

    
    private static MipResult runMaxWelfareCPLEX(
        final List<Agent> agents,
        final int kMax,
        final int kMin
    ) {
        final List<List<Double>> valueMatrix = valueMatrix(agents);
        final List<Double> objectiveArgs = listFromMatrix(valueMatrix);
        
        final int rowLength = objectiveArgs.size();
        
        final int agentCount = agents.size();
        
        try {
            final IloCplex lp = new IloCplex();
            
            // set these parameters if memory is a concern
            lp.setParam(IloCplex.IntParam.Threads, 2);
            final double sizeInMb = 600.0;
            lp.setParam(IloCplex.DoubleParam.WorkMem, sizeInMb);
            final double gapAbsoluteTolerance = 0.4;
            lp.setParam(IloCplex.DoubleParam.EpAGap, gapAbsoluteTolerance);
            final double gapRelativeTolerance = 0.01;
            lp.setParam(IloCplex.DoubleParam.EpGap, gapRelativeTolerance);
            
            lp.setOut(null);

            // all values in {0, 1}
            int[] xLowerBounds = new int[rowLength];
            int[] xUpperBounds = new int[rowLength];
            for (int i = 0; i < rowLength; i++) {
                xLowerBounds[i] = 0;
                xUpperBounds[i] = 1;
            }
            IloIntVar[] x  = lp.intVarArray(
                rowLength, xLowerBounds, xUpperBounds
            );
            
            // each agent must choose itself,
            // so x_ii must equal 1, for all i.
            // 1 * x_ii = 1
            for (int i = 0; i < agentCount; i++) {
                IloLinearIntExpr chooseSelfConstraint = lp.linearIntExpr();
                final int selfSelfIndex = listIndex(agentCount, i, i);
                chooseSelfConstraint.addTerm(1, x[selfSelfIndex]);
                lp.addEq(chooseSelfConstraint, 1);
            }
            
            // if i chooses j, j must choose i.
            // so x_ij = x_ji for all i, j.
            // 1 * x_ij - 1 * x_ji = 0
            for (int i = 0; i < agentCount; i++) {
                for (int j = i + 1; j < agentCount; j++) {
                    IloLinearIntExpr symmetryConstraint = lp.linearIntExpr();
                    final int ijIndex = listIndex(agentCount, i, j);
                    final int jiIndex = listIndex(agentCount, j, i);
                    symmetryConstraint.addTerm(1, x[ijIndex]);
                    symmetryConstraint.addTerm(-1, x[jiIndex]);
                    lp.addEq(symmetryConstraint, 0);
                }
            }
            
            // for any pair of rows, either they must have no 1's in the same
            // columns, or all 1's in all columns must match.
            // for any row after the top row, and any pair of columns, the
            // sum of the values in the first column plus the difference in
            // values in the second column must be <= 2.
            // this prevents there from being matching 1's in some column
            // for different rows, where those rows have mismatches 1's in
            // another column, which would lead to a total of 3.
            // x_ij + x_i'j + x_ij' - x_i'j' <= 2, for all i, i' > i, j, j' > j
            // x_ij + x_i'j - x_ij' + x_i'j' <= 2, for all i, i' > i, j, j' > j
            for (int rowUpper = 0; rowUpper < agentCount - 1; rowUpper++) {
                for (
                    int rowLower = rowUpper + 1; 
                    rowLower < agentCount; 
                    rowLower++
                ) {
                    for (int i = 0; i < agentCount - 1; i++) {
                        for (int j = i + 1; j < agentCount; j++) {
                            final int upperIIndex = 
                                listIndex(agentCount, rowUpper, i);
                            final int upperJIndex = 
                                listIndex(agentCount, rowUpper, j);
                            final int lowerIIndex = 
                                listIndex(agentCount, rowLower, i);
                            final int lowerJIndex = 
                                listIndex(agentCount, rowLower, j);
                            
                            final IloLinearIntExpr plusConstraint = 
                                lp.linearIntExpr();
                            plusConstraint.addTerm(1, x[lowerIIndex]);
                            plusConstraint.addTerm(1, x[lowerJIndex]);
                            plusConstraint.addTerm(1, x[upperIIndex]);
                            plusConstraint.addTerm(-1, x[upperJIndex]);
                            lp.addLe(plusConstraint, 2);

                            final IloLinearIntExpr minusConstraint = 
                                lp.linearIntExpr();
                            minusConstraint.addTerm(1, x[lowerIIndex]);
                            minusConstraint.addTerm(1, x[lowerJIndex]);
                            minusConstraint.addTerm(-1, x[upperIIndex]);
                            minusConstraint.addTerm(1, x[upperJIndex]);
                            lp.addLe(minusConstraint, 2);
                        }
                    }
                }
            }
            
            // total agents on each team is at least kMin.
            // so Sum over j: x_ij >= kMin, for all i
            for (int i = 0; i < agentCount; i++) {
                IloLinearIntExpr kMinConstraint = lp.linearIntExpr();
                for (int j = 0; j < agentCount; j++) {
                    final int ijIndex = listIndex(agentCount, i, j);
                    kMinConstraint.addTerm(1, x[ijIndex]);
                }
                lp.addGe(kMinConstraint, kMin);
            }
            
            // total agents on each team is no more than kMax.
            // so Sum over j: x_ij <= kMax, for all i
            for (int i = 0; i < agentCount; i++) {
                IloLinearIntExpr kMaxConstraint = lp.linearIntExpr();
                for (int j = 0; j < agentCount; j++) {
                    final int ijIndex = listIndex(agentCount, i, j);
                    kMaxConstraint.addTerm(1, x[ijIndex]);
                }
                lp.addLe(kMaxConstraint, kMax);
            }
            
            // maximize the total value of the bundle
            double [] objectiveValues = new double[rowLength];
            for (int i = 0; i < rowLength; i++) {
                objectiveValues[i] = objectiveArgs.get(i);
            }
            lp.addMaximize(
                lp.scalProd(x, objectiveValues)
            );
            
            if (lp.solve()) {
                final double[] columnValuesArr = lp.getValues(x);
                final List<Double> columnValues = new ArrayList<Double>();
                for (int i = 0; i < columnValuesArr.length; i++) {
                    columnValues.add(columnValuesArr[i]);
                }
                
                final double objectiveValue = lp.getObjValue();
                final MipResult result = new MipResult(
                    "obj",
                    objectiveValue,
                    columnValues,
                    true
                );
                
                lp.end();
                
                return result;
            }
            
        } catch (IloException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
