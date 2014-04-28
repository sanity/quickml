package quickdt.crossValidation;
import org.apache.mahout.classifier.evaluation.Auc;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.calibratedPredictiveModel.Calibrator;

import java.util.List;

/**
 * Created by alexanderhawk on 4/24/14.
 */
public class aucCrossValLoss implements CrossValLoss
{

    @Override
    public double getTotalLoss(List<AbstractInstance> crossValSet, PredictiveModel predictiveModel) {
        Auc auc = new Auc();

        for (AbstractInstance instance : crossValSet) {
            auc.add((Double)instance.getClassification() == 1.0 ? 1 : 0, predictiveModel.getProbability(instance.getAttributes(), 1.0));
        }
        return auc.auc();
    }
}