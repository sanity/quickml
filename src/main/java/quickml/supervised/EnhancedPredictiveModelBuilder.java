package quickml.supervised;

import quickml.data.instances.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.classifier.logisticRegression.DataTransformer;
import quickml.supervised.classifier.logisticRegression.TransformedData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 10/30/15.
 */
//Transformed Data might need another gen
public interface EnhancedPredictiveModelBuilder<P extends PredictiveModel, I extends Instance, R extends Instance, D extends TransformedData<R, D>>
        extends DataTransformer<I, R, D> {

    P buildPredictiveModel(D transformedData);
    void updateBuilderConfig(final Map<String, Serializable> config);

   }
