package quickdt.crossValidation.crossValLossFunctions;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;

import java.util.List;

/**
 * Created by alexanderhawk on 7/29/14.
 */
public interface ClassifierCVLossFunction<T extends Classifier> extends CrossValLossFunction<T> {
    @Override
    double getLoss(List<? extends AbstractInstance> crossValSet, T classifier);
}
