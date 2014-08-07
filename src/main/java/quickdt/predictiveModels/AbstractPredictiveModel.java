package quickdt.predictiveModels;

import com.google.common.collect.Lists;
import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.AbstractInstance;

import java.util.List;

/**
 * Created by alexanderhawk on 7/31/14.
 */
public abstract class AbstractPredictiveModel<R, P> implements PredictiveModel<R, P> {

    @Override
    public List<LabelPredictionWeight<P>> createLabelPredictionWeights(List<AbstractInstance<R>> instances) {
        List<LabelPredictionWeight<P>> labelPredictionWeights = Lists.newArrayList();
        for (AbstractInstance<R> instance : instances) {
            LabelPredictionWeight<P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(),
                    this.predict(instance.getRegressors()), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }
}
