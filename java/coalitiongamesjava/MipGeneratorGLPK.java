package coalitiongames;

import java.util.ArrayList;
import java.util.List;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_iocp;
import org.gnu.glpk.glp_prob;

public final class MipGeneratorGLPK implements MipGenerator {
    
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
        
        // number of agents is 1 more than the size of values, because
        // values does not include the self agent.
        assert TabuSearch.checkKRange(values.size() + 1, kMin, kMax);
        
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
        final glp_prob lp = GLPK.glp_create_prob();
        
        // each column represents the demand for 1 other agent
        GLPK.glp_add_cols(lp, otherAgentCount);
        for (int i = 1; i <= otherAgentCount; i++) {
            GLPK.glp_set_col_name(lp, i, "x" + i);
            // each demand must be an integer value
            GLPK.glp_set_col_kind(lp, i, GLPKConstants.GLP_IV);
            // each demand must be in [0, 1]
            GLPK.glp_set_col_bnds(lp, i, GLPKConstants.GLP_DB, 0, 1);
        }
        // there are 2 constraints if kMin is 0 or 1
        int countRows = 2;
        // if kMin > 1, we need 1 more constraint
        if (kMin > 1) {
            countRows++;
        }
        GLPK.glp_add_rows(lp, countRows);
        
        SWIGTYPE_p_int ind;
        SWIGTYPE_p_double val;
        
        // can't demand more than (kMax - 1) other agents.
        // sum i from 1->n: x_i <= kMax - 1
        GLPK.glp_set_row_name(lp, 1, "c1");
        // constraint is upper bounded by (kMax - 1)
        GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_UP, 0, kMax - 1);
        ind = GLPK.new_intArray(otherAgentCount);
        val = GLPK.new_doubleArray(otherAgentCount);
        for (int i = 1; i <= otherAgentCount; i++) {
            // multiply the demand for this item . . .
            GLPK.intArray_setitem(ind, i, i);
            // . . . by 1.
            GLPK.doubleArray_setitem(val, i, 1);
        }
        GLPK.glp_set_mat_row(lp, 1, otherAgentCount, ind, val);
        
        // can't demand a bundle of other agents that is over budget.
        // sum i from 1->n: x_i prices_i <= budget
        GLPK.glp_set_row_name(lp, 2, "c2");
        // constraint is upper bounded by budget
        GLPK.glp_set_row_bnds(lp, 2, GLPKConstants.GLP_UP, 0, budget);
        ind = GLPK.new_intArray(otherAgentCount);
        val = GLPK.new_doubleArray(otherAgentCount);
        for (int i = 1; i <= otherAgentCount; i++) {
            // multiply the demand for this item . . .
            GLPK.intArray_setitem(ind, i, i);
            // . . . by the price of the item
            GLPK.doubleArray_setitem(val, i, prices.get(i - 1));
        }
        GLPK.glp_set_mat_row(lp, 2, otherAgentCount, ind, val);
        
        if (kMin > 1) {
            // can't demand fewer than (kMin - 1) other agents.
            // sum i from 1->n: x_i >= kMin - 1
            final int thirdColNumber = 3;
            GLPK.glp_set_row_name(lp, thirdColNumber, "c3");
            // constraint is lower bounded by (kMin - 1)
            GLPK.glp_set_row_bnds(
                lp, 
                thirdColNumber, 
                GLPKConstants.GLP_LO, 
                kMin - 1, // lower bound
                kMax - 1 // upper bound (ignored)
            );
            ind = GLPK.new_intArray(otherAgentCount);
            val = GLPK.new_doubleArray(otherAgentCount);
            for (int i = 1; i <= otherAgentCount; i++) {
                // multiply demand for the item . . .
                GLPK.intArray_setitem(ind, i, i);
                // . . . by 1
                GLPK.doubleArray_setitem(val, i, 1);
            }
            GLPK.glp_set_mat_row(lp, thirdColNumber, otherAgentCount, ind, val);
        }
        
        GLPK.glp_set_obj_name(lp, "obj");
        // maximize the total value of the bundle
        GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
        // bias objective value = 0
        GLPK.glp_set_obj_coef(lp, 0, 0);
        for (int i = 1; i <= otherAgentCount; i++) {
            // set objective value of each other agent
            // columns are 1-based after the bias, so start i from 1.
            GLPK.glp_set_obj_coef(lp, i, values.get(i - 1));
        }
        final glp_iocp iocp = new glp_iocp();
        GLPK.glp_init_iocp(iocp);
        iocp.setPresolve(GLPKConstants.GLP_ON);
        // turn off messages
        iocp.setMsg_lev(GLPKConstants.GLP_MSG_OFF);
        final int ret = GLPK.glp_intopt(lp, iocp);
        
        if (ret == 0) {
            final List<Double> columnValues = new ArrayList<Double>();
            for (int i = 1; i <= otherAgentCount; i++) {
                columnValues.add(GLPK.glp_mip_col_val(lp, i));
            }
            final MipResult result = new MipResult(
                GLPK.glp_get_obj_name(lp),
                GLPK.glp_mip_obj_val(lp),
                columnValues
            );
            GLPK.glp_delete_prob(lp);
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
          
        GLPK.glp_delete_prob(lp);
        throw new IllegalStateException();
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
