package quickdt.crossValidation.crossValLossFunctions;
import org.apache.mahout.classifier.evaluation.Auc;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;

import java.util.List;

/**
 * Created by alexanderhawk on 5/17/14.
 */
public class NonWeightedAUCCrossValLossFunction<C extends Classifier> implements CrossValLossFunction<C> {

    @Override
    public double getLoss(List<? extends AbstractInstance> crossValSet, C classifier)  {
        Auc auc = new Auc();
        for (AbstractInstance instance : crossValSet) {
            auc.add((Double) instance.getLabel() == 1.0 ? 1 : 0, classifier.getProbability(instance.getAttributes(), 1.0));
        }
        return 1 - auc.auc();
    }
}