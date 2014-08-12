package quickdt.crossValidation.crossValLossFunctions;
import org.apache.mahout.classifier.evaluation.Auc;
import quickdt.data.MapWithDefaultOfZero;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 5/17/14.
 */
public class NonWeightedAUCCrossValLossFunction implements CrossValLossFunction<MapWithDefaultOfZero> {

    @Override
    public double getLoss(List<LabelPredictionWeight<MapWithDefaultOfZero>> labelPredictionWeights)  {
        Auc auc = new Auc();
        for (LabelPredictionWeight<MapWithDefaultOfZero> labelPredictionWeight : labelPredictionWeights) {
            int trueValue = (Double) labelPredictionWeight.getLabel() == 1.0 ? 1 : 0;
            MapWithDefaultOfZero classifierPrediction = labelPredictionWeight.getPrediction();
            double probabilityOfCorrectInstance = classifierPrediction.get(labelPredictionWeight.getLabel());
            double score = 0;
            score = (trueValue > 1) ? probabilityOfCorrectInstance: 1 - probabilityOfCorrectInstance;
            auc.add(trueValue, score);
        }
        return 1 - auc.auc();
    }
}