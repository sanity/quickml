package quickml.supervised.crossValidation.crossValLossFunctions;
import org.apache.mahout.classifier.evaluation.Auc;
import quickml.data.PredictionMap;

import java.util.List;

/**
 * Created by alexanderhawk on 5/17/14.
 */
public class NonWeightedAUCCrossValLossFunction implements CrossValLossFunction<PredictionMap> {

    @Override
    public double getLoss(List<LabelPredictionWeight<PredictionMap>> labelPredictionWeights)  {
        Auc auc = new Auc();
        for (LabelPredictionWeight<PredictionMap> labelPredictionWeight : labelPredictionWeights) {
            int trueValue = (Double) labelPredictionWeight.getLabel() == 1.0 ? 1 : 0;
            PredictionMap classifierPrediction = labelPredictionWeight.getPrediction();
            double probabilityOfCorrectInstance = classifierPrediction.get(labelPredictionWeight.getLabel());
            double score = 0;
            score = (trueValue == 1) ? probabilityOfCorrectInstance: 1 - probabilityOfCorrectInstance;
            auc.add(trueValue, score);
        }
        return 1 - auc.auc();
    }
}