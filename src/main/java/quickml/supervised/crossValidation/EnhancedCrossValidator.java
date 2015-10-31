package quickml.supervised.crossValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.instances.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.EnhancedPredictiveModelBuilder;
import quickml.supervised.classifier.logisticRegression.TransformedData;
import quickml.supervised.crossValidation.data.TrainingDataCycler;
import quickml.supervised.crossValidation.data.TrainingDataCyclerFactory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static quickml.supervised.Utils.getInstanceWeights;

/**
 * Created by alexanderhawk on 10/30/15.
 */
public class EnhancedCrossValidator<PM extends PredictiveModel, I extends Instance, R extends Instance, D extends TransformedData<R,D>> implements CrossValidator {


    private static final Logger logger = LoggerFactory.getLogger(SimpleCrossValidator.class);


    private LossChecker<PM, R> lossChecker;
    private TrainingDataCyclerFactory<R, D> dataCyclerFactory;
    private final EnhancedPredictiveModelBuilder<PM, I, R, D> modelBuilder;
    private final List<I> rawInstances;

    public EnhancedCrossValidator(EnhancedPredictiveModelBuilder<PM, I, R, D>  modelBuilder, LossChecker<PM, R> lossChecker, TrainingDataCyclerFactory<R, D> dataCyclerFactory,  List<I> rawInstances) {
        this.lossChecker = lossChecker;
        this.dataCyclerFactory = dataCyclerFactory;
        this.modelBuilder = modelBuilder;
        this.rawInstances = rawInstances;
    }


    /**
     * Get the loss for a model without updating the model config
     */
    public double getLossForModel() {
        return getLossForModel(new HashMap<String, Serializable>());
    }

    public double getLossForModel(Map<String, Serializable> config) {
        if (config.size()!=0) {
            modelBuilder.updateBuilderConfig(config);
        }
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
        D transformedData = modelBuilder.transformData(rawInstances);
        TrainingDataCycler<R> dataCycler = dataCyclerFactory.getTrainingDataCycler(transformedData);

        do {
            List<R> validationSet = dataCycler.getValidationSet();
            D transformedTrainingData = transformedData.copyWithJustTraniningSet(dataCycler.getTrainingSet());
            PM predictiveModel = modelBuilder.buildPredictiveModel(transformedTrainingData);
            double validationSetWeight = getInstanceWeights(validationSet);
            runningLoss += lossChecker.calculateLoss(predictiveModel, validationSet) * validationSetWeight;
            runningWeightOfValidationSet += validationSetWeight;
            dataCycler.nextCycle();
        } while (dataCycler.hasMore());

        return runningLoss / runningWeightOfValidationSet;
    }

}
