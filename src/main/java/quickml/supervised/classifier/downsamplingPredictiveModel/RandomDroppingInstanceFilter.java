package quickml.supervised.classifier.downsamplingPredictiveModel;

import com.google.common.base.Predicate;
import quickml.supervised.Misc;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 4/23/14.
 */
class RandomDroppingInstanceFilter implements Predicate<Instance<Map<String, Serializable>>> {
    private final Serializable classificationToDrop;
    private final double dropProbability;

    public RandomDroppingInstanceFilter(Serializable classificationToDrop, double dropProbability) {
        this.classificationToDrop = classificationToDrop;
        this.dropProbability = dropProbability;
    }

    @Override
    public boolean apply(final Instance instance) {
        if (instance.getLabel().equals(classificationToDrop)) {
            final double rand = Misc.random.nextDouble();
            return rand > dropProbability;
        } else {
            return true;
        }
    }
}
