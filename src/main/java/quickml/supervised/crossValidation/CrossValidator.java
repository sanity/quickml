package quickml.supervised.crossValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.data.TrainingDataCycler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static quickml.supervised.Utils.getInstanceWeights;

public class CrossValidator<PM extends PredictiveModel<AttributesMap, ?>, I extends InstanceWithAttributesMap<?>>  {

    private static final Logger logger = LoggerFactory.getLogger(CrossValidator.class);


    private LossChecker<PM, I> lossChecker;
    private TrainingDataCycler<I> dataCycler;
    private final PredictiveModelBuilder<AttributesMap, PM, I> modelBuilder;

    public CrossValidator(PredictiveModelBuilder<AttributesMap, PM, I> modelBuilder, LossChecker<PM, I> lossChecker, TrainingDataCycler<I> dataCycler) {
        this.lossChecker = lossChecker;
        this.dataCycler = dataCycler;
        this.modelBuilder = modelBuilder;
    }


    /**
     * Get the loss for a model without updating the model config
     */
    public double getLossForModel() {
        return getLossForModel(new HashMap<String, Object>());
    }

    public double getLossForModel(Map<String, Object> config) {
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
