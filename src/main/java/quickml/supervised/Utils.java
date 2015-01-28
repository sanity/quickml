package quickml.supervised;

import com.google.common.collect.Lists;
import quickml.data.Instance;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 7/31/14.
 */
public class Utils {

    public static <R, L, P> List<LabelPredictionWeight<L, P>> createLabelPredictionWeights(List<? extends Instance<R, L>> instances, PredictiveModel<R, P> predictiveModel) {
        List<LabelPredictionWeight<L, P>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<R, L> instance : instances) {
            LabelPredictionWeight<L, P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(), predictiveModel.predict(instance.getAttributes()), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }

    public static <R, L, P> List<LabelPredictionWeight<L, P>> createLabelPredictionWeightsWithoutAttributes(List<? extends Instance<R, L>> instances, PredictiveModel<R, P> predictiveModel, Set<String> attributesToIgnore) {
        List<LabelPredictionWeight<L, P>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<R, L> instance : instances) {
            LabelPredictionWeight<L, P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(),
                    predictiveModel.predictWithoutAttributes(instance.getAttributes(), attributesToIgnore), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }


}

