package quickml.supervised.crossValidation.data;

import quickml.data.instances.Instance;
import quickml.supervised.classifier.logisticRegression.TransformedData;

import java.util.List;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public interface TrainingDataCyclerFactory<R extends Instance, D extends TransformedData<R,D>> {
    TrainingDataCycler<R> getTrainingDataCycler(D dataDTO);//also depends on the Date Time extractor
}
