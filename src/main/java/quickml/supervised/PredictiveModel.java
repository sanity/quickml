package quickml.supervised;

import quickml.data.Instance;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;

import java.io.Serializable;
import java.util.List;

/**
 * A predictive model, typically created by a supervised learning algorithm.
 * Given a set of regressors, will generate a prediction.
 */
public interface PredictiveModel<R, P> extends Serializable {

    P predict(R regressors);
    void dump(Appendable appendable);
    List<LabelPredictionWeight<P>> createLabelPredictionWeights(List<Instance<R>> instances);
}
