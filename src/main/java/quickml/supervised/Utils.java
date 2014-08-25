package quickml.supervised;

import com.google.common.collect.Lists;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.data.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 7/31/14.
 */
public class Utils {

    public static <R, P> List<LabelPredictionWeight<P>> createLabelPredictionWeights(List<? extends Instance<R>> instances, PredictiveModel<R, P> predictiveModel) {
        List<LabelPredictionWeight<P>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<R> instance : instances) {
            LabelPredictionWeight<P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(),
                    predictiveModel.predict(instance.getRegressors()), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }
}

