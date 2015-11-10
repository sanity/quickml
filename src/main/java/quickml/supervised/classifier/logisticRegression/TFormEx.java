package quickml.supervised.classifier.logisticRegression;

import java.util.List;

/**
 * Created by alexanderhawk on 11/10/15.
 */
public class TFormEx extends TransformedData<SparseClassifierInstance, TFormEx> {
    @Override
    public TFormEx copyWithJustTrainingSet(List<SparseClassifierInstance> trainingSet) {
        return null;
    }

    @Override
    public List<SparseClassifierInstance> getTransformedInstances() {
        return null;
    }
}
