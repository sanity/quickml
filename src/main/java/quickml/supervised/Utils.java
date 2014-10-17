package quickml.supervised;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.data.Instance;

import java.util.List;

/**
 * Created by alexanderhawk on 7/31/14.
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static <R, P> List<LabelPredictionWeight<P>> createLabelPredictionWeights(List<? extends Instance<R>> instances, PredictiveModel<R, P> predictiveModel) {
        List<LabelPredictionWeight<P>> labelPredictionWeights = Lists.newArrayList();
        int numPositives = 0;
        double avWeightOfPositivePrediction = 0;
        double avWeightOfNegativePrediction = 0;

        for (Instance<R> instance : instances) {
            LabelPredictionWeight<P> labelPredictionWeight = new LabelPredictionWeight<>(instance.getLabel(),
                    predictiveModel.predict(instance.getAttributes()), instance.getWeight());
            labelPredictionWeights.add(labelPredictionWeight);
            if (instance.getLabel() instanceof Double && ((Double) instance.getLabel()).doubleValue() == 1.0) {
                numPositives++;
            }
            avWeightOfPositivePrediction += ((PredictionMap)(labelPredictionWeight.getPrediction())).get(1.0);
            avWeightOfNegativePrediction += ((PredictionMap)(labelPredictionWeight.getPrediction())).get(0.0);

        }
        logger.info("num Positives: " + numPositives);
        logger.info("avWeightOfPositivePrediction: " + avWeightOfPositivePrediction);


        return labelPredictionWeights;
    }
}

