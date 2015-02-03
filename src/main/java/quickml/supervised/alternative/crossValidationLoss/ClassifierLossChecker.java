package quickml.supervised.alternative.crossValidationLoss;

import quickml.supervised.Utils;
import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.classifier.Classifier;

import java.util.List;

public class ClassifierLossChecker implements LossChecker<Classifier, ClassifierInstance> {

    private ClassifierLossFunction lossFunction;

    public ClassifierLossChecker(ClassifierLossFunction lossFunction) {
        this.lossFunction = lossFunction;
    }

    @Override
    public double calculateLoss(Classifier predictiveModel, List<ClassifierInstance> validationSet) {
        return lossFunction.getLoss(Utils.calcResultPredictions(predictiveModel, validationSet));
    }

}
