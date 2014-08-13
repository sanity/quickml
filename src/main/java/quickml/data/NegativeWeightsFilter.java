package quickml.data;

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
    public static <R> Iterable<? extends Instance<R>> filterNegativeWeights(Iterable<? extends Instance<R>> trainingData) {
        final HashSet<R> instanceLookUp = new HashSet<R>();
        for (Instance<R> instance : trainingData)
            if (instance.getWeight() < 0)
                instanceLookUp.add(instance.getRegressors());

        Predicate<Instance<R>> predicate = new Predicate<Instance<R>>() {
            @Override
            public boolean apply(final Instance<R> instance) {
                if (instanceLookUp.contains(instance.getRegressors()))
                    return false;
                else
                    return true;
            }
        };
        return Iterables.filter(trainingData, predicate);
    }
}

