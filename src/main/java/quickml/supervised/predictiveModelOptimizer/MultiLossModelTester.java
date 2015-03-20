package quickml.supervised.predictiveModelOptimizer;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.attributeImportance.LossFunctionTracker;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;
import quickml.supervised.classifier.Classifier;

import java.util.List;

import static quickml.supervised.Utils.calcResultPredictions;

public class MultiLossModelTester {

    private TrainingDataCycler<InstanceWithAttributesMap> dataCycler;
    private final PredictiveModelBuilder<? extends Classifier, InstanceWithAttributesMap> modelBuilder;

    public MultiLossModelTester(PredictiveModelBuilder<? extends Classifier, InstanceWithAttributesMap> modelBuilder, TrainingDataCycler<InstanceWithAttributesMap> dataCycler) {
        this.dataCycler = dataCycler;
        this.modelBuilder = modelBuilder;
    }

    public LossFunctionTracker getMultilossForModel(List<ClassifierLossFunction> lossFunctions) {

        dataCycler.reset();
        LossFunctionTracker lossFunctionTracker = new LossFunctionTracker(lossFunctions);

        do {
            List<InstanceWithAttributesMap> validationSet = dataCycler.getValidationSet();
            Classifier predictiveModel = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
            lossFunctionTracker.updateLosses(calcResultPredictions(predictiveModel, validationSet));
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return lossFunctionTracker;
    }

}
