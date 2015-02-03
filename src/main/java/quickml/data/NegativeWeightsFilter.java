package quickml.data;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.util.HashSet;

/**
 * Created by alexanderhawk on 5/1/14.
 */
public class NegativeWeightsFilter {

    //TODO[mk] is this being used?
    //parametrize training data or subtype it to have right params
    public static <R,L> Iterable<? extends Instance<R, L>> filterNegativeWeights(Iterable<? extends Instance<R, L>> trainingData) {
        final HashSet<R> instanceLookUp = new HashSet<R>();
        for (Instance<R,L> instance : trainingData)
            if (instance.getWeight() < 0)
                instanceLookUp.add(instance.getAttributes());

        Predicate<Instance<R,L>> predicate = new Predicate<Instance<R, L>>() {
            @Override
            public boolean apply(final Instance<R, L> instance) {
                if (instanceLookUp.contains(instance.getAttributes()))
                    return false;
                else
                    return true;
            }
        };
        return Iterables.filter(trainingData, predicate);
    }
}

