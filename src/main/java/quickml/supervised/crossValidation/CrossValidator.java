package quickml.supervised.crossValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.data.TrainingDataCycler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static quickml.supervised.Utils.getInstanceWeights;

public class CrossValidator<A, PM extends PredictiveModel<A, ?>, I extends Instance<A, ?>>  {

    private static final Logger logger = LoggerFactory.getLogger(CrossValidator.class);


    private LossChecker<A, PM, I> lossChecker;
    private TrainingDataCycler<I> dataCycler;
    private final PredictiveModelBuilder<A, PM, I> modelBuilder;

    public CrossValidator(PredictiveModelBuilder<A, PM, I> modelBuilder, LossChecker<A, PM, I> lossChecker, TrainingDataCycler<I> dataCycler) {
        this.lossChecker = lossChecker;
        this.dataCycler = dataCycler;
        this.modelBuilder = modelBuilder;
    }


    /**
     * Get the loss for a model without updating the model config
     */
    public double getLossForModel() {
        return getLossForModel(new HashMap<String, Serializable>());
    }

    public double getLossForModel(Map<String, Serializable> config) {
        dataCycler.reset();
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
            List<I> validationSet = dataCycler.getValidationSet();
            double validationSetWeight = getInstanceWeights(validationSet);
            PM predictiveModel = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
            runningLoss += lossChecker.calculateLoss(predictiveModel, validationSet) * validationSetWeight;
            runningWeightOfValidationSet += validationSetWeight;
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return runningLoss / runningWeightOfValidationSet;
    }
}
