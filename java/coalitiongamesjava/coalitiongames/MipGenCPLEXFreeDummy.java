package coalitiongames;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

public final class MipGenCPLEXFreeDummy {

    /**
     * 
     * @param teamValues a list of the values of current teams.
     * produced by summing the values to the dummy agent of agents
     * on each team. include only teams with room left.
     * @param teamPrices a list of the prices of current teams.
     * produced by summing the prices of agents on each team.
     * include only teams with room left.
     * @param teamAgentsNeeded a list of how many agents must be
     * added to fill each team, in order. number includes the self
     * agent, so if a team needs 4 agents, the dummy agent's demand 
     * for other free agents must be 3 if choosing that team.
     * each entry must be positive.
     * @param agentValues a list of the values of free agents
     * for the dummy agent.
     * @param agentPrices a list of the prices of free agents other
     * than the dummy
     * @param budget dummy agent's budget
     * @return a list of integers in {0, 1} with length
     * teamValues.size() + agentValues.size(). each integer indicates
     * whether the dummy agent demands that team or agent.
     * if the dummy agent can't afford any team plus enough free
     * agents to fill the team, it demands the empty bundle.
     * else, it demands exactly 1 team plus exactly the number of
     * other agents needed to fill the team, counting itself as
     * a member.
     */
    public List<Integer> getFreeDummyDemand(
        final List<Double> teamValues,
        final List<Double> teamPrices,
        final List<Integer> teamAgentsNeeded,
        final List<Double> agentValues,
        final List<Double> agentPrices,
        final double budget
    ) {
        // must be a team left to join
        assert teamValues.size() >= 1;
        assert budget >= MipGenerator.MIN_BUDGET;
        assert teamValues.size() == teamPrices.size();
        assert teamValues.size() == teamAgentsNeeded.size();
        assert agentValues.size() == agentPrices.size();
        // there shouldn't be more unfilled teams that free agents.
        assert teamValues.size() <= 1 + agentValues.size();
        for (final Integer agentsNeeded: teamAgentsNeeded) {
            // all teams should not be full.
            assert agentsNeeded > 0;
            // there should be enough agents left to fill each team.
            assert agentsNeeded <= 1 + agentValues.size();
        }

        if (!isFeasible(teamPrices, teamAgentsNeeded, agentPrices, budget)) {
            // can't afford any bundle with 1 team and teamAgentsNeeded agents
            // return 0 demand for teams and agents
            return DemandGeneratorOneCTakenCaptain.
                zerosList(teamValues.size() + agentPrices.size());
        }
        
        // the problem is feasible, so run the MIP
        // store items as teams first, then agents.
        final List<Double> allValues = new ArrayList<Double>();
        allValues.addAll(teamValues);
        allValues.addAll(agentValues);
        final List<Double> allPrices = new ArrayList<Double>();
        allPrices.addAll(teamPrices);
        allPrices.addAll(agentPrices);
        final int dimensions = allValues.size();
        
        // generate vector that will be used to ensure that given
        // a selected team, the correct number of other agents
        // is demanded.
        final List<Integer> remainingSizes = new ArrayList<Integer>();
        for (int i = 0; i < dimensions; i++) {
            if (i < teamValues.size()) {
                // i refers to a team. add -1 * (teamAgentsNeeded - 1),
                // so that if only this team is taken, plus self and
                // (teamAgentsNeeded - 1) others, the dot product
                // of remainingSizes and x will be 0.
                remainingSizes.add(-1 * (teamAgentsNeeded.get(i) - 1));
            } else {
                // i refers to an agent. set its coefficient to 1,
                // to act as a count of taken agents.
                remainingSizes.add(1);
            }
        }
        
        try {
            final IloCplex lp = new IloCplex();
            lp.setOut(null);
            
            // x (results) must be in {0, 1}^(# teams + # agents)
            int[] xLowerBounds = new int[dimensions];
            int[] xUpperBounds = new int[dimensions];
            for (int i = 0; i < dimensions; i++) {
                xLowerBounds[i] = 0;
                xUpperBounds[i] = 1;
            }
            IloIntVar[] x  = lp.intVarArray(
                dimensions, xLowerBounds, xUpperBounds
            );
            
            // sum of x[i] price[i] <= budget
            IloLinearNumExpr budgetConstraint = lp.linearNumExpr();
            for (int i = 0; i < dimensions; i++) {
                budgetConstraint.addTerm(allPrices.get(i), x[i]);
            }
            lp.addLe(budgetConstraint, budget);
            
            // sum of x[i], i for a TEAM, == 1
            IloLinearIntExpr oneTeamConstraint = lp.linearIntExpr();
            for (int i = 0; i < teamValues.size(); i++) {
                oneTeamConstraint.addTerm(1, x[i]);
            }
            lp.addEq(oneTeamConstraint, 1);
            
            // sum of x dot remainingSizesNeeded == 0
            // this ensures that whichever team is selected,
            // the correct number of free agents is taken.
            IloLinearIntExpr fullTeamConstraint = lp.linearIntExpr();
            for (int i = 0; i < dimensions; i++) {
                fullTeamConstraint.addTerm(remainingSizes.get(i), x[i]);
            }
            lp.addEq(fullTeamConstraint, 0);  
            
            // maximize the total value of the bundle
            double [] objectiveValues = new double[dimensions];
            for (int i = 0; i < dimensions; i++) {
                objectiveValues[i] = allValues.get(i);
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
                
                lp.end();
                return getRoundedColumnValues(columnValues);
            }
            
            final IloCplex.Status status = lp.getStatus();
            lp.end();
            
            throw new IllegalStateException(
                "No solution found: " + status
            ); 
        } catch (final IloException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error building model");
        }
    }
    
    /*
     * Return true if any combination of TEAM teamPrices.get(i) and
     * AGENTS of count teamAgentsNeeded.get(i) is affordable with
     * "budget".
     * teamAgentsNeeded includes the self agent, so only need to 
     * select (teamAgentsNeeded - 1) other agents.
     */
    public static boolean isFeasible(
        final List<Double> teamPrices,
        final List<Integer> teamAgentsNeeded,
        final List<Double> agentPrices,
        final double budget
    ) {
        for (int i = 0; i < teamPrices.size(); i++) {
            final double teamPrice = teamPrices.get(i);
            final double budgetLeft = budget - teamPrice;
            if (teamAgentsNeeded.get(i) > 0) {
                // kMin = team agents needed, WITHOUT subtracting 1.
                // MipChecker.isFeasible() already assumes the self agent
                // is included in the count, so don't subtract 1 here.
                final int kMin = teamAgentsNeeded.get(i);
                if (MipChecker.isFeasible(agentPrices, budgetLeft, kMin)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public static List<Integer> getRoundedColumnValues(
        final List<Double> columnValues
    ) {
        List<Integer> result = new ArrayList<Integer>();
        final double tolerance = 0.001;
        for (double columnValue: columnValues) {
            if (Math.abs(columnValue) < tolerance) {
                result.add(0);
            } else if (Math.abs(columnValue - 1.0) < tolerance) {
                result.add(1);
            } else {
                throw new IllegalStateException();
            }
        }
        
        return result;
    }
}
