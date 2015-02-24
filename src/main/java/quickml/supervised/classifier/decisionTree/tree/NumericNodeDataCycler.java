package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import quickml.data.ClassifierInstance;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 2/22/15.
 */
public class NumericNodeDataCycler<T extends ClassifierInstance> {
    protected Iterable<T> trainingData;
    protected List<T> inSet;
    protected List<T> outSet;
    protected String attribute;

    public List<T> getTestInSet() {
        return inSet;
    }

    public List<T> getTestOutSet() {
        return outSet;
    }


    public NumericNodeDataCycler(Iterable<T> trainingData, String attribute) {
        this.attribute = attribute;
        this.trainingData = trainingData;
        inSet = Lists.newArrayListWithCapacity(Iterables.size(trainingData));
        outSet = Lists.newArrayList();
        for (T instance : trainingData) {
            outSet.add(instance);
        }

    }

    public void transferDataFromOutsetToInset(double threshold) {

        List<T> newOutSet = Lists.newArrayList();
        GreaterThanThresholdPredicate greaterThanThresholdPredicate = new GreaterThanThresholdPredicate(attribute, threshold);
        for (int i = 0; i < outSet.size(); i++) {
            T instance = outSet.get(i);
            if (greaterThanThresholdPredicate.apply(instance)) {
                inSet.add(instance);
            } else {
                newOutSet.add(instance);
            }
        }
        outSet = newOutSet;
        }





    protected class GreaterThanThresholdPredicate implements Predicate<T> {

        private final String attribute;
        private final double threshold;

        public GreaterThanThresholdPredicate(String attribute, double threshold) {
            this.attribute = attribute;
            this.threshold = threshold;
        }

        @Override
        public boolean apply(@Nullable T input) {
            try {
                if (input == null) {//consider deleting
                    return false;
                }
                Serializable value = input.getAttributes().get(attribute);
                if (value == null) {
                    value = 0;
                }
                return ((Number) value).doubleValue() > threshold;
            } catch (final ClassCastException e) { // Kludge, need to
                // handle better
                return false;
            }
        }
    }

}