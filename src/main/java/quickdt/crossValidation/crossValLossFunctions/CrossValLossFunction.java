package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 4/24/14.
 */
public interface CrossValLossFunction<P>  {
    double getLoss(List<LabelPredictionWeight<P>> labelPredictionWeights);
}
