package coalitiongames;

import java.util.ArrayList;
import java.util.List;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.glp_prob;

public final class MipResult {
    
    private final String lpName;
    
    private final double objectiveValue;
    
    private final List<Double> columnValues;
    
    public MipResult(
        final glp_prob lp,
        final int n
    ) {
        this.lpName = GLPK.glp_get_obj_name(lp);
        this.objectiveValue = GLPK.glp_mip_obj_val(lp);
        this.columnValues = new ArrayList<Double>();
        for (int i = 1; i <= n; i++) {
            this.columnValues.add(GLPK.glp_mip_col_val(lp, i));
        }
    }

    public String getLpName() {
        return lpName;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public List<Double> getColumnValues() {
        return columnValues;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MipResult [lpName=");
        builder.append(lpName);
        builder.append(", \nobjectiveValue=");
        builder.append(objectiveValue);
        builder.append(", \ncolumnValues=");
        builder.append(columnValues);
        builder.append("\n]");
        return builder.toString();
    }
}
