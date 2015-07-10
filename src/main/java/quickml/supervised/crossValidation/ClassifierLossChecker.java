
package quickml.supervised.crossValidation;

import quickml.supervised.Utils;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;

import java.util.List;

public class ClassifierLossChecker<T extends ClassifierInstance, PM extends Classifier> implements LossChecker<PM, T> {

    private ClassifierLossFunction lossFunction;

    public ClassifierLossChecker(ClassifierLossFunction lossFunction) {
        this.lossFunction = lossFunction;
    }

    @Override
    public double calculateLoss(PM predictiveModel, List<T> validationSet) {
        return lossFunction.getLoss(Utils.calcResultPredictions(predictiveModel, validationSet));
    }

}
