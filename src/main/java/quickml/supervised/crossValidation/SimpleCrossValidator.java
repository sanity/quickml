package quickml.supervised.crossValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.instances.Instance;
import quickml.data.instances.RegressionInstance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.data.TrainingDataCycler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static quickml.supervised.Utils.getInstanceWeights;

public class SimpleCrossValidator<PM extends PredictiveModel, T extends Instance> implements CrossValidator {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCrossValidator.class);


    private LossChecker<PM, T> lossChecker;
    private TrainingDataCycler<T> dataCycler;
    private final PredictiveModelBuilder<PM, T> modelBuilder;

    public SimpleCrossValidator(PredictiveModelBuilder<PM, T> modelBuilder, LossChecker<PM, T> lossChecker, TrainingDataCycler<T> dataCycler) {
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
        try  {

            double runningLoss = 0;
            double runningWeightOfValidationSet = 0;
            boolean gotNextCycle = false;
            int cycle = 0;
            while (dataCycler.hasMore() || gotNextCycle) {
                BufferedWriter trainingWriter = new BufferedWriter(new FileWriter("training" + cycle));
                trainingWriter.write("FileNo");

                for (T instance : dataCycler.getValidationSet()) {
                    trainingWriter.write("" + ((RegressionInstance) instance).id + "\n");
                }
                BufferedWriter testWriter = new BufferedWriter(new FileWriter("test" + cycle));
                testWriter.write("FileNo,actual,predicted\n");
                testWriter = new BufferedWriter(new FileWriter("validation" + cycle));
                List<T> validationSet = dataCycler.getValidationSet();
                double validationSetWeight = getInstanceWeights(validationSet);
                PM predictiveModel = modelBuilder.buildPredictiveModel(dataCycler.getTrainingSet());
                runningLoss += ((RegressionLossChecker) lossChecker).calculateLoss(predictiveModel, validationSet, testWriter) * validationSetWeight;
                runningWeightOfValidationSet += validationSetWeight;
                gotNextCycle = dataCycler.nextCycle();
                cycle++;

                testWriter.flush();
                trainingWriter.flush();
                testWriter.close();
                trainingWriter.close();
            }
            return runningLoss / runningWeightOfValidationSet;
        } catch (IOException e) {
            logger.error("couldn't write");
        }
        return 0.0;
    }
}