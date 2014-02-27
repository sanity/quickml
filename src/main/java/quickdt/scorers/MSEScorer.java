package quickdt.scorers;

import quickdt.ClassificationCounter;
import quickdt.Scorer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 2/27/14.
 */
public class MSEScorer implements Scorer {
    @Override
    public double scoreSplit(final ClassificationCounter a, final ClassificationCounter b) {
        ClassificationCounter parent = ClassificationCounter.merge(a, b);
        double parentMSE = getTotalError(parent) / parent.getTotal();
        double splitMSE = (getTotalError(a) + getTotalError(b)) / (a.getTotal() + b.getTotal());
        return parentMSE - splitMSE;
    }

    private double getTotalError(ClassificationCounter cc) {
        double totalError = 0;
        for (Map.Entry<Serializable, Double> e : cc.getCounts().entrySet()) {
            double error = 1.0 - (cc.getCount(e.getKey()) / cc.getTotal());
            double errorSquared = error*error;
            totalError += errorSquared * e.getValue();
        }
        return totalError;
    }
}
