package quickml.supervised.alternative.optimizer;

import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.alternative.attributeImportanceFinder.LossFunctionTracker;
import quickml.supervised.alternative.crossValidationLoss.ClassifierLossFunction;
import quickml.supervised.classifier.Classifier;

import java.util.List;

import static quickml.supervised.Utils.calcResultPredictions;

public class MultiLossModelTester {

    private TrainingDataCycler<ClassifierInstance> dataCycler;
    private final PredictiveModelBuilder<? extends Classifier, ClassifierInstance> modelBuilder;

    public MultiLossModelTester(PredictiveModelBuilder<? extends Classifier, ClassifierInstance> modelBuilder, TrainingDataCycler<ClassifierInstance> dataCycler) {
        this.dataCycler = dataCycler;
        this.modelBuilder = modelBuilder;
    }

    public LossFunctionTracker getMultilossForModel(List<ClassifierLossFunction> lossFunctions) {

        dataCycler.reset();
        LossFunctionTracker lossFunctionTracker = new LossFunctionTracker(lossFunctions);

        do {
            List<ClassifierInstance> validationSet = dataCycler.getValidationSet();
            Classifier predictiveModel = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
            lossFunctionTracker.updateLosses(calcResultPredictions(predictiveModel, validationSet));
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return lossFunctionTracker;
    }

}
