package quickml.supervised.crossValidation;

import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.Utils;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;

import java.util.List;

public class ClassifierLossChecker<I extends ClassifierInstance> implements LossChecker<Classifier, I> {

    private ClassifierLossFunction lossFunction;

    public ClassifierLossChecker(ClassifierLossFunction lossFunction) {
        this.lossFunction = lossFunction;
    }

    @Override
    public double calculateLoss(Classifier predictiveModel, List<I> validationSet) {
        return lossFunction.getLoss(Utils.calcResultPredictions(predictiveModel, validationSet));
    }

}
