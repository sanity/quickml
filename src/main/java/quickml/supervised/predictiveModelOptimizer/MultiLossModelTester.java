package quickml.supervised.predictiveModelOptimizer;

import quickml.data.AttributesMap;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.attributeImportance.LossFunctionTracker;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLossFunction;
import quickml.supervised.classifier.Classifier;

import java.util.List;

import static quickml.supervised.Utils.calcResultPredictions;

public class MultiLossModelTester<I extends InstanceWithAttributesMap<?>> {

    private TrainingDataCycler<I> dataCycler;
    private final PredictiveModelBuilder<AttributesMap, ? extends Classifier, I> modelBuilder;

    public MultiLossModelTester(PredictiveModelBuilder<AttributesMap, ? extends Classifier, I> modelBuilder, TrainingDataCycler<I> dataCycler) {
        this.dataCycler = dataCycler;
        this.modelBuilder = modelBuilder;
    }

    public LossFunctionTracker getMultilossForModel(List<ClassifierLossFunction> lossFunctions) {

        dataCycler.reset();
        LossFunctionTracker lossFunctionTracker = new LossFunctionTracker(lossFunctions);

        do {
            List<? extends InstanceWithAttributesMap<?>> validationSet = dataCycler.getValidationSet();
            Classifier predictiveModel = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
            lossFunctionTracker.updateLosses(calcResultPredictions(predictiveModel, validationSet));
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return lossFunctionTracker;
    }

}
