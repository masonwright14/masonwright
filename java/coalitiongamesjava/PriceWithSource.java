package coalitiongames;
import java.util.ArrayList;
import java.util.List;

import coalitiongames.PriceWithError.PriceUpdateSource;

public final class PriceWithSource {

    private final List<Double> price;
    private final PriceUpdateSource priceUpdateSource;
    
    public PriceWithSource(
        final List<Double> aPrice,
        final PriceUpdateSource aPriceUpdateSource
    ) {
        this.price = new ArrayList<Double>();
        for (Double currentPrice: aPrice) {
            this.price.add(currentPrice);
        }
        this.priceUpdateSource = aPriceUpdateSource;
    }

    List<Double> getPrice() {
        return price;
    }

    PriceUpdateSource getPriceUpdateSource() {
        return priceUpdateSource;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PriceWithSource [price=");
        builder.append(price);
        builder.append(", priceUpdateSource=");
        builder.append(priceUpdateSource);
        builder.append("]");
        return builder.toString();
    }
}
