package quickml.supervised.classifier.decisionTree.tree;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import quickml.data.ClassifierInstance;

import java.util.List;

/**
 * Created by alexanderhawk on 2/22/15.
 */
public class NumericNodeDataCyclerWithCrossValCorrection<T extends ClassifierInstance> extends NumericNodeDataCycler {

    private List<T> testInSet;
    private List<T> testOutSet;

    public NumericNodeDataCyclerWithCrossValCorrection(String attribute, Iterable<T> trainingData, Iterable<T> testData) {
        super(trainingData, attribute);
        testInSet = Lists.newArrayListWithCapacity(Iterables.size(testData));
        testOutSet = Lists.newArrayList();
        for (T instance : testData) {
            testOutSet.add(instance);
        }
    }

    public List<T> getTestInSet() {
        return testInSet;
    }

    public List<T> getTestOutSet() {
        return testOutSet;
    }
    @Override
    public void transferDataFromOutsetToInset(double threshold) {
        super.transferDataFromOutsetToInset(threshold);
        List<T> newOutSet = Lists.newArrayList();
        GreaterThanThresholdPredicate greaterThanThresholdPredicate = new GreaterThanThresholdPredicate(attribute, threshold);
        for (int i = 0; i < testOutSet.size(); i++) {
            T instance = testOutSet.get(i);
            if (greaterThanThresholdPredicate.apply(instance)) {
                testInSet.add(instance);
            } else {
                newOutSet.add(instance);
            }
        }
        testOutSet = newOutSet;
    }
}
