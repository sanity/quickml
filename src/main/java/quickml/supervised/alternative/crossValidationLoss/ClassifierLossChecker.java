package quickml.supervised.alternative.crossValidationLoss;

import quickml.supervised.alternative.optimizer.ClassifierInstance;
import quickml.supervised.classifier.Classifier;

import java.util.List;

public abstract class ClassifierLossChecker implements LossChecker<Classifier, ClassifierInstance> {


    protected PredictionMapResults getPredictionResults(Classifier predictiveModel, List<ClassifierInstance> validationSet) {
        PredictionMapResults results = new PredictionMapResults();
        for (ClassifierInstance instance : validationSet) {
            results.addResult(predictiveModel.predict(instance.getAttributes()), instance.getLabel(), instance.getWeight());
        }
        return results;
    }
}
