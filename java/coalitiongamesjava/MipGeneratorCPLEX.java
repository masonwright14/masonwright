package coalitiongames;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

public final class MipGeneratorCPLEX implements MipGenerator {

    /**
     * 
     * @param values a list of all the other agents, 
     * other than the demanding agent.
     * @param prices a list of prices for all the other agents, 
     * excluding the demanding agent.
     * @param budget budget of the demanding agent
     * @param kMax 1 more than the maximum agents that can be demanded. 
     * in other words, the max agents per team.
     * @param kMin the min agents per team. 1 more than the 
     * minimum agents that can be demanded.
     * @param maxPrice maximum allowable price, used only 
     * for assertions in guard code.
     * @return
     */
    @Override
    public MipResult getLpSolution(
        final List<Double> values,
        final List<Double> prices,
        final double budget,
        final int kMax,
        final int kMin,
        final double maxPrice
    ) {
        assert values.size() >= 1;
        assert values.size() == prices.size();
        assert budget >= MIN_BUDGET;
        assert kMax >= kMin;
        assert kMin >= 0;
        
        if (!MipChecker.isFeasible(prices, budget, kMin)) {
            // can't afford any bundle of size at least (kMin - 1).
            final String objName = "obj";
            final double objectiveValue = 0.0;
            final List<Double> columnValues = new ArrayList<Double>();
            for (int i = 0; i < values.size(); i++) {
                columnValues.add(0.0);
            }
            final boolean isSuccess = false;
            return new MipResult(
                objName, objectiveValue, columnValues, isSuccess
            );
        }
        
        if (DEBUGGING) {
            for (Double value: values) {
                if (value < 0) {
                    throw new IllegalArgumentException();
                }
            }
            for (Double price: prices) {
                if (price < 0 || price > maxPrice) {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        // does not include the self agent
        final int otherAgentCount = values.size();
        
        try {
            final IloCplex lp = new IloCplex();
            lp.setOut(null);
            int[] xLowerBounds = new int[otherAgentCount];
            int[] xUpperBounds = new int[otherAgentCount];
            for (int i = 0; i < otherAgentCount; i++) {
                xLowerBounds[i] = 0;
                xUpperBounds[i] = 1;
            }
            IloIntVar[] x  = lp.intVarArray(
                otherAgentCount, xLowerBounds, xUpperBounds
            );

            // sum of x[i] <= kMax - 1
            IloLinearIntExpr kMaxConstraint = lp.linearIntExpr(); 
            for (int i = 0; i < otherAgentCount; i++) {
                kMaxConstraint.addTerm(1, x[i]);
            }
            lp.addLe(kMaxConstraint, kMax - 1);
            
            // sum of x[i] price[i] <= budget
            IloLinearNumExpr budgetConstraint = lp.linearNumExpr();
            for (int i = 0; i < otherAgentCount; i++) {
                budgetConstraint.addTerm(prices.get(i), x[i]);
            }
            lp.addLe(budgetConstraint, budget);
            
            // if kMin >= 2 -> sum of x[i] >= kMin - 1
            if (kMin >= 2) {
                IloLinearIntExpr kMinConstraint = lp.linearIntExpr(); 
                for (int i = 0; i < otherAgentCount; i++) {
                    kMinConstraint.addTerm(1, x[i]);
                }
                lp.addGe(kMinConstraint, kMin - 1);
            }
            
            // maximize the total value of the bundle
            double [] objectiveValues = new double[otherAgentCount];
            for (int i = 0; i < otherAgentCount; i++) {
                objectiveValues[i] = values.get(i);
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
                
                if (DEBUGGING) {
                    final int testIterations = 10000;
                    boolean testResult = MipChecker.checkLpSolution(
                        result, values, prices,  budget, 
                        kMax, kMin, testIterations
                    );
                    if (!testResult) {
                        throw new IllegalStateException();
                    }
                }
                return result;
            }
            
            lp.end();
            throw new IllegalStateException(
                "No solution found: " + lp.getStatus()
            );
            
        } catch (final IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error building model");
        }
    }

    /**
     * 
     * @param values a list of all the other agents, 
     * other than the demanding agent.
     * @param prices a list of prices for all the other agents, 
     * excluding the demanding agent.
     * @param budget budget of the demanding agent
     * @param kSizes a list of allowable team sizes, where the self agent is
     * included as 1 of the team's members.
     * @param maxPrice maximum allowable price, used only 
     * for assertions in guard code.
     * @return
     */
    @Override
    public MipResult getLpSolution(
        final List<Double> values, 
        final List<Double> prices,
        final double budget, 
        final List<Integer> kSizes, 
        final double maxPrice
    ) {
        assert !kSizes.isEmpty();
        
        final List<List<Integer>> kRanges = TabuSearch.getMinMaxPairs(kSizes);
        final List<MipResult> mipResults = new ArrayList<MipResult>();
        for (final List<Integer> kRange: kRanges) {
            // greater than or equal to, in case they are the same
            assert kRange.get(1) >= kRange.get(0);
            final MipResult mipResult = 
                getLpSolution(
                    values, 
                    prices, 
                    budget,
                    kRange.get(1), 
                    kRange.get(0), 
                    maxPrice
                );
            mipResults.add(mipResult);
        }
        
        MipResult bestMipResult = mipResults.get(0);
        for (final MipResult mipResult: mipResults) {
            if (
                mipResult.getObjectiveValue() 
                > bestMipResult.getObjectiveValue()
            ) {
                bestMipResult = mipResult;
            }
        }
        
        return bestMipResult;
    }
}
