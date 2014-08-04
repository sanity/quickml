package quickdt.crossValidation.crossValLossFunctions;
import org.apache.mahout.classifier.evaluation.Auc;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;
import quickdt.predictiveModels.ClassifierPrediction;

import java.util.List;

/**
 * Created by alexanderhawk on 5/17/14.
 */
public class NonWeightedAUCCrossValLossFunction<CP extends ClassifierPrediction> implements CrossValLossFunction<CP> {

    @Override
    public double getLoss(List<LabelPredictionWeight<CP>> labelPredictionWeights)  {
        Auc auc = new Auc();
        for (LabelPredictionWeight<CP> labelPredictionWeight : labelPredictionWeights) {
            int trueValue = (Double) labelPredictionWeight.getLabel() == 1.0 ? 1 : 0;
            CP classifierPrediction = labelPredictionWeight.getPrediction();
            double probabilityOfCorrectInstance = classifierPrediction.getPredictionForLabel(labelPredictionWeight.getLabel());
            double score = 0;
            score = (trueValue > 1) ? probabilityOfCorrectInstance: 1 - probabilityOfCorrectInstance;
            auc.add(trueValue, score);
        }
        return 1 - auc.auc();
    }
}