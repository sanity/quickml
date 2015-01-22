package quickml.supervised;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.Instance;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;

import java.util.List;
import java.util.Set;

/**
 * Created by alexanderhawk on 7/31/14.
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static <R, P> List<LabelPredictionWeight<P>> createLabelPredictionWeights(List<? extends Instance<R>> instances, PredictiveModel<R, P> predictiveModel) {
        List<LabelPredictionWeight<P>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<R> instance : instances) {
            LabelPredictionWeight<P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(), predictiveModel.predict(instance.getAttributes()), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }

    public static <R, P> List<LabelPredictionWeight<P>> createLabelPredictionWeightsWithoutAttributes(List<? extends Instance<R>> instances, PredictiveModel<R, P> predictiveModel, Set<String> attributesToIgnore) {
        List<LabelPredictionWeight<P>> labelPredictionWeights = Lists.newArrayList();
        for (Instance<R> instance : instances) {
            LabelPredictionWeight<P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(),
                    predictiveModel.predictWithoutAttributes(instance.getAttributes(), attributesToIgnore),  instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
        }
        return labelPredictionWeights;
    }

}

