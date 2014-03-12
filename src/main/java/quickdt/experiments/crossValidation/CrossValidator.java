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

    }

    public double getCrossValidatedLoss(PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder, Iterable<AbstractInstance> allTrainingData) {
        CrossValLoss<?> crossValLoss;
        double runningLoss = 0;
        DataSplit dataSplit;
        for (int currentFold = 0; currentFold < folds; currentFold++)  {
            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
            PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
            crossValLoss = lossObjectSupplier.get();
            for (AbstractInstance instance : dataSplit.validation) {
                crossValLoss.addLossFromInstance(predictiveModel.getProbability(instance.getAttributes(), instance.getClassification()), instance.getWeight());
            }
            runningLoss+=crossValLoss.getTotalLoss();

        }
        final double averageLoss = runningLoss / folds;
        logger.info("Average loss: "+averageLoss);
        return averageLoss;
    }

    private DataSplit setTrainingAndValidationSets(int foldNumber, Iterable<AbstractInstance> data) {
        DataSplit dataSplit = new DataSplit();
        int count = 0;
        for (AbstractInstance instance : data) {
            if (count%folds == foldNumber) //(count > testSetLowerBound && count < testSetUpperBound)//
                dataSplit.validation.add(instance);
            else
                dataSplit.training.add(instance);
            count++;
        }
        return dataSplit;
    }

    class DataSplit  {
        public List<AbstractInstance> training;
        public List<AbstractInstance> validation;

        public DataSplit() {
            training = Lists.<AbstractInstance>newArrayList();
            validation = Lists.<AbstractInstance>newArrayList();
        }
    }
}
