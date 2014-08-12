package quickml.supervised.classifier;

import com.google.common.collect.Lists;
import quickml.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.data.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 7/31/14.
 */
public abstract class AbstractPredictiveModel<R, P> implements PredictiveModel<R, P> {

    @Override
    public List<LabelPredictionWeight<P>> createLabelPredictionWeights(List<Instance<R>> instances) {
        List<LabelPredictionWeight<P>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<R> instance : instances) {
            LabelPredictionWeight<P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(),
                    this.predict(instance.getRegressors()), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }
}
