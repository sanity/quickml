package quickdt.crossValidation.crossValLossFunctions;
import org.apache.mahout.classifier.evaluation.Auc;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/17/14.
 */
public class NonWeightedAUCCrossValLossFunction implements CrossValLossFunction<Map<Serializable, Double>> {

    @Override
    public double getLoss(List<LabelPredictionWeight<Map<Serializable, Double>>> labelPredictionWeights)  {
        Auc auc = new Auc();
        for (LabelPredictionWeight<Map<Serializable, Double>> labelPredictionWeight : labelPredictionWeights) {
            int trueValue = (Double) labelPredictionWeight.getLabel() == 1.0 ? 1 : 0;
            Map<Serializable, Double> classifierPrediction = labelPredictionWeight.getPrediction();
            double probabilityOfCorrectInstance = classifierPrediction.get(labelPredictionWeight.getLabel());
            double score = 0;
            score = (trueValue > 1) ? probabilityOfCorrectInstance: 1 - probabilityOfCorrectInstance;
            auc.add(trueValue, score);
        }
        return 1 - auc.auc();
    }
}