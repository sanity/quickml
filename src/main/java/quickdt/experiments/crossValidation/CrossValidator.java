package quickdt.experiments.crossValidation;

import com.google.common.base.*;
import com.google.common.collect.Lists;
//import com.javafx.tools.doclets.formats.html.SourceToHTMLConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.*;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public class CrossValidator {
    private static final  Logger logger =  LoggerFactory.getLogger(CrossValidator.class);
    private static final int DEFAULT_NUMBER_OF_FOLDS = 4;
    private int folds;
    private final Supplier<? extends CrossValLoss<?>> lossObjectSupplier;
    private List<Instance> trainingData;
    private List<Instance> validationData;
    /**
     * Create a new CrossValidator using an RMSECrossValLoss, generating a getCrossValidatedLoss
     * dataset from 1 in 10 instances selected randomly based on the has of
     * the Attributes in each Instance.
     */
    public CrossValidator() {
        this(CrossValidator.DEFAULT_NUMBER_OF_FOLDS, RMSECrossValLoss.supplier);
     }
    public CrossValidator(int folds) {
        this(folds, RMSECrossValLoss.supplier);
    }
    /**
     * Create a new CrossValidator
     * @param folds The number folds to be used in the cross validation procedure
     * @param lossObjectSupplier A supplier of CrossValScorers that should be used to getCrossValidatedLoss the
     *                       predictive model's performance
     */
    public CrossValidator(final int folds, Supplier<? extends CrossValLoss<?>> lossObjectSupplier) {
        this.folds = folds;
        this.lossObjectSupplier = lossObjectSupplier;
        this.validationData = Lists.newArrayList();
        this.trainingData = Lists.newArrayList();
    }

    public double getCrossValidatedLoss(PredictiveModelBuilder<?> predictiveModelBuilder, Iterable<Instance> data) {
        CrossValLoss<?> crossValLoss;
        double runningLoss = 0;
        for (int currentFold = 0; currentFold < folds; currentFold++)  {
            setTrainingAndValidationSets(currentFold, data);
            logger.info("Training set contains "+trainingData.size());
            logger.info("Testing set contains "+validationData.size());
            PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingData);
            logger.info("Predictive model hash: "+predictiveModel.hashCode());
            crossValLoss = lossObjectSupplier.get();
            for (Instance instance : validationData) {
                crossValLoss.addLossFromInstance(predictiveModel.getProbability(instance.getAttributes(), instance.getClassification()), instance.getWeight());
            }
            runningLoss+=crossValLoss.getTotalLoss();
            System.out.println("runningTotal " + runningLoss);
        }
        return runningLoss/folds;
    }

    private void setTrainingAndValidationSets(int foldNumber, Iterable<Instance> data) {
        int count = 0;
        for (Instance instance : data) {
            if (count%folds == foldNumber) //(count > testSetLowerBound && count < testSetUpperBound)//
                validationData.add(instance);
            else
                trainingData.add(instance);
            count++;
        }
    }
}
