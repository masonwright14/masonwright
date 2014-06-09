package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public final class MipResult {
    
    private final String lpName;
    
    private final double objectiveValue;
    
    private final List<Double> columnValues;
    
    public MipResult(
        final String aLpName,
        final double aObjectiveValue,
        final List<Double> aColumnValues
    ) {
        this.lpName = aLpName;
        this.objectiveValue = aObjectiveValue;
        this.columnValues = new ArrayList<Double>();
        for (final Double columnValue: aColumnValues) {
            this.columnValues.add(columnValue);
        }
    }

    public String getLpName() {
        return lpName;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }
    
    public List<Integer> getRoundedColumnValues() {
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
        builder.append(", \nroundedColumnValues=");
        builder.append(getRoundedColumnValues());
        builder.append("\n]");
        return builder.toString();
    }
}
