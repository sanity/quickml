package quickdt.crossValidation;
import org.apache.mahout.classifier.evaluation.Auc;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import java.util.List;

/**
 * Created by alexanderhawk on 5/17/14.
 */
public class NonWeightedAUCCrossValLoss  implements CrossValLoss {

    @Override
    public double getLoss(List<? extends AbstractInstance> crossValSet, PredictiveModel predictiveModel) {
        Auc auc = new Auc();
        for (AbstractInstance instance : crossValSet) {
            auc.add((Double) instance.getClassification() == 1.0 ? 1 : 0, predictiveModel.getProbability(instance.getAttributes(), 1.0));
        }
        return 1 - auc.auc();
    }
}