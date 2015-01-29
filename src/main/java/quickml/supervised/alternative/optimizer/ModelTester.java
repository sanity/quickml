package quickml.supervised.alternative.optimizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.alternative.crossValidationLoss.LossChecker;

import java.util.List;
import java.util.Map;

import static quickml.supervised.Utils.getInstanceWeights;

public class ModelTester<PM extends PredictiveModel, T extends Instance> {

    private static final Logger logger = LoggerFactory.getLogger(ModelTester.class);


    private LossChecker<PM, T> lossChecker;
    private TrainingDataCycler<T> trainingData;
    private final PredictiveModelBuilder<PM, T> modelBuilder;

    public ModelTester(PredictiveModelBuilder<PM, T> modelBuilder, LossChecker<PM, T> lossChecker, TrainingDataCycler<T> trainingData) {
        this.lossChecker = lossChecker;
        this.trainingData = trainingData;
        this.modelBuilder = modelBuilder;
    }

    public double getLossForModel(Map<String, Object> config) {
        trainingData.reset();
        modelBuilder.updateBuilderConfig(config);
        double loss = testModel();
        logger.info("Loss {} for config {}", loss, config.toString());
        return loss;
    }

    /**
     * We keep cycling through the test data, updating the running losses for each run.
     */
    private double testModel() {
        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;

        do {
            List<T> validationSet = trainingData.getValidationSet();
            double validationSetWeight = getInstanceWeights(validationSet);
            PM predictiveModel = modelBuilder.buildPredictiveModel(trainingData.getTrainingSet());
            runningLoss += lossChecker.calculateLoss(predictiveModel, validationSet) * validationSetWeight;
            runningWeightOfValidationSet += validationSetWeight;
            trainingData.nextCycle();
        } while (trainingData.hasMore());

        return runningLoss / runningWeightOfValidationSet;
    }
}
