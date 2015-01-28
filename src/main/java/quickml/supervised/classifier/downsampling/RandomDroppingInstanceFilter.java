package quickml.supervised.classifier.downsampling;

import com.google.common.base.Predicate;
import quickml.collections.MapUtils;
import quickml.supervised.alternative.optimizer.ClassifierInstance;

import java.io.Serializable;

/**
 * Created by ian on 4/23/14.
 */
class RandomDroppingInstanceFilter implements Predicate<ClassifierInstance> {
    private final Serializable classificationToDrop;
    private final double dropProbability;

    public RandomDroppingInstanceFilter(Serializable classificationToDrop, double dropProbability) {
        this.classificationToDrop = classificationToDrop;
        this.dropProbability = dropProbability;
    }

    @Override
    public boolean apply(final ClassifierInstance Instance) {
        if (Instance.getLabel().equals(classificationToDrop)) {
            final double rand = MapUtils.random.nextDouble();

            return rand > dropProbability;
        } else {
            return true;
        }
    }
}
