package quickdt.predictiveModels.downsamplingPredictiveModel;

import com.google.common.base.Predicate;
import quickdt.Misc;
import quickdt.data.Instance;

import java.io.Serializable;

/**
 * Created by ian on 4/23/14.
 */
class RandomDroppingInstanceFilter implements Predicate<Instance> {
    private final Serializable classificationToDrop;
    private final double dropProbability;

    public RandomDroppingInstanceFilter(Serializable classificationToDrop, double dropProbability) {
        this.classificationToDrop = classificationToDrop;
        this.dropProbability = dropProbability;
    }

    @Override
    public boolean apply(final Instance abstractInstance) {
        if (abstractInstance.getLabel().equals(classificationToDrop)) {
            final double rand = Misc.random.nextDouble();
            return rand > dropProbability;
        } else {
            return true;
        }
    }
}
