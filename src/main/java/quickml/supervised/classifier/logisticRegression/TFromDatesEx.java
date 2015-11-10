package quickml.supervised.classifier.logisticRegression;

import quickml.data.instances.Instance;
import quickml.supervised.crossValidation.utils.DateTimeExtractor;

import java.util.List;

/**
 * Created by alexanderhawk on 11/10/15.
 */
public class TFromDatesEx extends TransformedDataWithDates<SparseClassifierInstance, TFromDatesEx> {
    @Override
    public TFromDatesEx copyWithJustTrainingSet(List<SparseClassifierInstance> trainingSet) {
        return null;
    }

    @Override
    public List<SparseClassifierInstance> getTransformedInstances() {
        return null;
    }

    @Override
    public DateTimeExtractor<SparseClassifierInstance> getDateTimeExtractor() {
        return null;
    }

    public TFromDatesEx() {
        super();
    }
}
