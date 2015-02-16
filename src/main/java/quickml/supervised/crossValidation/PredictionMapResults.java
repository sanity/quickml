package quickml.supervised.crossValidation;

import java.util.Iterator;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

public class PredictionMapResults implements Iterable<PredictionMapResult>{

    private final List<PredictionMapResult> results;
    private final double totalWeight;

    public PredictionMapResults(List<PredictionMapResult> results) {
        checkArgument(!results.isEmpty(), "Prediction results must not be empty");

        this.results = results;
        this.totalWeight = calcTotalWeight();
    }

    private double calcTotalWeight() {
        double totalWeight = 0;
        for (PredictionMapResult result : results) {
            totalWeight += result.getWeight();
        }
        return totalWeight;
    }

    public double totalWeight() {
        return totalWeight;
    }

    @Override
    public Iterator<PredictionMapResult> iterator() {
        return results.iterator();
    }
}
