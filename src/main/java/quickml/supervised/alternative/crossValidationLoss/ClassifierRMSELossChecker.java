package quickml.supervised.alternative.crossValidationLoss;

import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.classifier.Classifier;

import java.util.List;

public class ClassifierRMSELossChecker extends ClassifierLossChecker {

    @Override
    public double calculateLoss(Classifier predictiveModel, List<ClassifierInstance> validationSet) {
        return LossFunctions.rmseLoss(getPredictionResults(predictiveModel, validationSet));
    }


}
