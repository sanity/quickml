package quickdt.data;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/1/14.
 */
public class NegativeWeightsFilter {

    //parametrize training data or subtype it to have right params
    public static <R> Iterable<? extends AbstractInstance<R>> filterNegativeWeights(Iterable<? extends AbstractInstance<R>> trainingData) {
        final HashSet<R> instanceLookUp = new HashSet<R>();
        for (AbstractInstance<R> instance : trainingData)
            if (instance.getWeight() < 0)
                instanceLookUp.add(instance.getRegressors());

        Predicate<AbstractInstance> predicate = new Predicate<AbstractInstance>() {
            @Override
            public boolean apply(final AbstractInstance instance) {
                if (instanceLookUp.contains(instance.getRegressors()))
                    return false;
                else
                    return true;
            }
        };
        return Iterables.filter(trainingData, predicate);
    }
}

