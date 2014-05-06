package quickdt.crossValidation;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.mahout.classifier.evaluation.Auc;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.calibratedPredictiveModel.Calibrator;

import java.util.HashSet;
import java.util.List;

/**
 * Created by alexanderhawk on 4/24/14.
 */
public class aucCrossValLoss implements CrossValLoss
{
    @Override
    public double getLoss(List<AbstractInstance> crossValSet, PredictiveModel predictiveModel) {
        Auc auc = new Auc();
        for (AbstractInstance instance : crossValSet) {
            auc.add((Double) instance.getClassification() == 1.0 ? 1 : 0, predictiveModel.getProbability(instance.getAttributes(), 1.0));
        }
        return auc.auc();
    }
}