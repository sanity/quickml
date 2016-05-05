package quickml.supervised.crossValidation.data;

import quickml.data.instances.Instance;
import quickml.supervised.classifier.logisticRegression.TransformedData;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public class FoldedDataFactory<R extends Instance, D extends TransformedData<R,D>> implements TrainingDataCyclerFactory<R,D> {
    private int numFolds;
    private int foldsUsed;

    public FoldedDataFactory(int numFolds, int foldsUsed) {
        this.numFolds = numFolds;
        this.foldsUsed = foldsUsed;
    }

    @Override
    public FoldedData<R> getTrainingDataCycler(D data) {
        return new FoldedData<>(data.getTransformedInstances(), numFolds, foldsUsed);
    }
}
