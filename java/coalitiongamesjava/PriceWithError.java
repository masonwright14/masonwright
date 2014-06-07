package coalitiongames;

import java.util.ArrayList;
import java.util.List;

public final class PriceWithError implements 
    Comparable<PriceWithError> {

    private final List<Double> prices;
    private final List<Double> error;
    private final List<List<Integer>> demand;
    private final double errorValue;
    
    public PriceWithError(
        final List<Double> aPrices,
        final List<Double> aError,
        final List<List<Integer>> aDemand,
        final double aErrorValue
    ) {
        this.prices = new ArrayList<Double>();
        for (double aPrice: aPrices) {
            this.prices.add(aPrice);
        }
        this.error = new ArrayList<Double>();
        for (double item: aError) {
            this.error.add(item);
        }
        this.demand = new ArrayList<List<Integer>>();
        for (List<Integer> row: aDemand) {
            final List<Integer> newRow = new ArrayList<Integer>();
            newRow.addAll(row);
            this.demand.add(newRow);
        }
        this.errorValue = aErrorValue;
    }

    public List<Double> getPrices() {
        return this.prices;
    }
    
    public List<Double> getError() {
        return this.error;
    }
    
    public List<List<Integer>> getDemand() {
        return this.demand;
    }

    public double getErrorValue() {
        return this.errorValue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PriceWithError [prices=");
        builder.append(prices);
        builder.append(", error=");
        builder.append(error);
        builder.append(", demand=");
        builder.append(demand);
        builder.append(", errorValue=");
        builder.append(errorValue);
        builder.append("]");
        return builder.toString();
    }

    /*
     * Compare based only on errorValue, for sorting.
     */
    @Override
    public int compareTo(final PriceWithError that) {
        final int before = -1;
        final int equal = 0;
        final int after = 1;
        if (this == that) {
            return equal;
        }
        if (this.errorValue < that.errorValue) {
            return after;
        } else if (this.errorValue > that.errorValue) {
            return before;
        }
        
        return equal;
    }

    /**
     * Just check if the price vector is the same for both.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime;
        if (prices != null) {
            result += prices.hashCode();
        }
        return result;
    }

    /**
     * Just check if the price vector is the same for both.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PriceWithError other = (PriceWithError) obj;
        if (prices == null) {
            if (other.prices != null) {
                return false;
            }
        } else if (!prices.equals(other.prices)) {
            return false;
        }
        return true;
    }
}
