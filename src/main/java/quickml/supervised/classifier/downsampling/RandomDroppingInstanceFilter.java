package quickml.supervised.classifier.downsampling;

import com.google.common.base.Predicate;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.Instance;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by ian on 4/23/14.
 */
class RandomDroppingInstanceFilter implements Predicate<Instance<AttributesMap>> {
    private final Serializable classificationToDrop;
    private final double dropProbability;

    public RandomDroppingInstanceFilter(Serializable classificationToDrop, double dropProbability) {
        this.classificationToDrop = classificationToDrop;
        this.dropProbability = dropProbability;
    }

    @Override
    public boolean apply(final Instance<AttributesMap> Instance) {
        if (Instance.getLabel().equals(classificationToDrop)) {
            final double rand = MapUtils.random.nextDouble();

            return rand > dropProbability;
        } else {
            return true;
        }
    }
}
