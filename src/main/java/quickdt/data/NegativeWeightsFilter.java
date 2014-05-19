package quickdt.data;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.HashSet;

/**
 * Created by alexanderhawk on 5/1/14.
 */
public class NegativeWeightsFilter {
    public static Iterable<? extends AbstractInstance> filterNegativeWeights(Iterable<? extends AbstractInstance> trainingData) {
        final HashSet<Attributes> instanceLookUp = new HashSet<Attributes>();
        for (AbstractInstance instance : trainingData)
            if (instance.getWeight() < 0)
                instanceLookUp.add(instance.getAttributes());

        Predicate<AbstractInstance> predicate = new Predicate<AbstractInstance>() {
            @Override
            public boolean apply(final AbstractInstance instance) {
                if (instanceLookUp.contains(instance.getAttributes()))
                    return false;
                else
                    return true;
            }
        };
        return Iterables.filter(trainingData, predicate);
    }
}

