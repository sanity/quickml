package quickml.supervised.classifier.downsampling;

import com.google.common.base.Predicate;
import quickml.collections.MapUtils;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceWithAttributesMap;

import java.io.Serializable;


/**
 * Created by ian on 4/23/14.
 */
class RandomDroppingInstanceFilter implements Predicate<InstanceWithAttributesMap<Serializable>> {
    private final Serializable classificationToDrop;
    private final double dropProbability;

    public RandomDroppingInstanceFilter(Serializable classificationToDrop, double dropProbability) {
        this.classificationToDrop = classificationToDrop;
        this.dropProbability = dropProbability;
    }

    @Override
    public boolean apply(final InstanceWithAttributesMap<Serializable> Instance) {
        if (Instance.getLabel().equals(classificationToDrop)) {
            final double rand = MapUtils.random.nextDouble();

            return rand > dropProbability;
        } else {
            return true;
        }
    }
}
