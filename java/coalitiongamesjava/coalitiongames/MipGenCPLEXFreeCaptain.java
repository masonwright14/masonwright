package coalitiongames;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloLinearIntExpr;
import ilog.concert.IloLinearNumExpr;
import ilog.cplex.IloCplex;

import java.util.ArrayList;
import java.util.List;

public final class MipGenCPLEXFreeCaptain {

    /**
     * @param teamValues a list of the values of current teams.
     * produced by summing the values to the captain of agents
     * on each team. include only teams with room left.
     * @param teamPrices a list of the prices of current teams.
     * produced by summing the prices of agents on each team.
     * include only teams with room left.
     * @param teamAgentsNeeded a list of how many agents must be
     * added to fill each team, in order. number includes the self
     * agent, so if a team needs 4 agents, the captain's demand 
     * for other free agents must be 3 if choosing that team.
     * each entry must be positive.
     * @param meanOtherFreeAgentValue mean value of free agents other
     * than the captain, to the captain, or 0.0 if none.
     * @param agentPrices a list of the prices of free agents other
     * than the captain
     * @param budget captain's budget
     * @return a list of integers in {0, 1} with length
     * teamValues.size() + # of other free agents left. each integer indicates
     * whether the captain demands that team or agent.
     * if the captain can't afford any team plus enough free
     * agents to fill the team, it demands the empty bundle.
     * else, it demands exactly 1 team plus exactly the number of
     * other agents needed to fill the team, counting itself as
     * a member.
     */
    public List<Integer> getFreeCaptainDemand(
        final List<Double> teamValues,
        final List<Double> teamPrices,
        final List<Integer> teamAgentsNeeded,
        final double meanOtherFreeAgentValue,
        final List<Double> agentPrices,
        final double budget
    ) {
        // must be a team left to join
        assert teamValues.size() >= 1;
        assert budget >= MipGenerator.MIN_BUDGET;
        assert teamValues.size() == teamPrices.size();
        assert teamValues.size() == teamAgentsNeeded.size();
        // there shouldn't be more unfilled teams than free agents.
        assert teamValues.size() <= 1 + agentPrices.size();
        for (final Integer agentsNeeded: teamAgentsNeeded) {
            // all teams should not be full.
            assert agentsNeeded > 0;
            // there should be enough agents left to fill each team.
            assert agentsNeeded <= 1 + agentPrices.size();
        }

        if (!MipGenCPLEXFreeDummy.isFeasible(
            teamPrices, teamAgentsNeeded, agentPrices, budget)
        ) {
            // can't afford any bundle with 1 team and teamAgentsNeeded agents
            // return 0 demand for teams and agents
            return DemandGeneratorOneCTakenCaptain.
                zerosList(teamValues.size() + agentPrices.size());
        }
        
        // the problem is feasible, so run the MIP
        // store items as teams first, then agents.
        final List<Double> allPrices = new ArrayList<Double>();
        allPrices.addAll(teamPrices);
        allPrices.addAll(agentPrices);
        final int dimensions = allPrices.size();
        
        // value of a team is sum of values of players plus
        // space left after self added times mean value of other
        // free agents
        final List<Double> allValues = new ArrayList<Double>();
        for (int i = 0; i < teamValues.size(); i++) {
            final double teamValue = teamValues.get(i);
            final int extraSpaceAfterSelf = teamAgentsNeeded.get(i) - 1;
            final double expectedTeamValue = 
                teamValue + extraSpaceAfterSelf * meanOtherFreeAgentValue;
            allValues.add(expectedTeamValue);
        }
        // value of an agent is 0, so add 0.0 for all agents
        while (allValues.size() < allPrices.size()) {
            allValues.add(0.0);
        }
        
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
            
            // maximize the expected value of the chosen team.
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
                return MipGenCPLEXFreeDummy.
                    getRoundedColumnValues(columnValues);
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
}
