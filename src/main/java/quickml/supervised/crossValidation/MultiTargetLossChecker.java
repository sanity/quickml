
package quickml.supervised.crossValidation;

        import com.google.common.collect.Lists;
        import quickml.data.ClassifierInstance;
        import quickml.supervised.Utils;
        import quickml.supervised.classifier.Classifier;
        import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierLossFunction;

        import java.util.List;

public class MultiTargetLossChecker<T extends ClassifierInstance> implements LossChecker<Classifier, T> {

    private ClassifierLossFunction lossFunction;
    private InstanceTargetSelector instanceTargetSelector;

    public MultiTargetLossChecker(ClassifierLossFunction lossFunction, InstanceTargetSelector instanceTargets) {
        this.lossFunction = lossFunction;
        this.instanceTargetSelector = instanceTargets;
    }

    @Override
    public double calculateLoss(Classifier predictiveModel, List<T> validationSet) {
        List<ClassifierInstance> singleTargetValidationSet = Lists.newArrayList();
        for(T instance : validationSet) {
            singleTargetValidationSet.add(new ClassifierInstance(instance.getAttributes(), instanceTargetSelector.getSingleLabel(instance), instance.getWeight()));
        }
        return lossFunction.getLoss(Utils.calcResultPredictions(predictiveModel, singleTargetValidationSet));
    }

}