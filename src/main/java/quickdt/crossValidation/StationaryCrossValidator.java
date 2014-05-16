package quickdt.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.util.List;

//import com.javafx.tools.doclets.formats.html.SourceToHTMLConverter;

/**
 * Created by ian on 2/28/14.
 */
public class StationaryCrossValidator extends CrossValidator {
private static final  Logger logger =  LoggerFactory.getLogger(StationaryCrossValidator.class);

    private static final int DEFAULT_NUMBER_OF_FOLDS = 4;
    private int folds;
    private int foldsUsed;
    private CrossValLoss crossValLoss;

    /**
     * Create a new CrossValidator using an RMSECrossValLoss, generating a getCrossValidatedLoss
     * dataset from 1 in 10 instances selected randomly based on the has of
     * the Attributes in each Instance.
     */
    public StationaryCrossValidator() {
        this(StationaryCrossValidator.DEFAULT_NUMBER_OF_FOLDS, new RMSECrossValLoss());
     }
    public StationaryCrossValidator(int folds) {
        this(folds, new RMSECrossValLoss());
    }

    public StationaryCrossValidator(int folds, int foldsUsed) {
        this(folds, foldsUsed, new RMSECrossValLoss());
    }
    /**
     * Create a new CrossValidator
     * @param folds The number folds to be used in the cross validation procedure
     */
    public StationaryCrossValidator(final int folds, CrossValLoss crossValLoss) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = folds;
        this.crossValLoss = crossValLoss;

    }

    public StationaryCrossValidator(final int folds, final int foldsUsed, CrossValLoss crossValLoss) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = foldsUsed;
        this.crossValLoss = crossValLoss;
    }

    public double getCrossValidatedLoss(PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder, Iterable<? extends AbstractInstance> allTrainingData) {
        double runningLoss = 0;
        DataSplit dataSplit;
        for (int currentFold = 0; currentFold < foldsUsed; currentFold++)  {
            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
            PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
            runningLoss+=crossValLoss.getLoss(dataSplit.validation, predictiveModel);
            logger.info("running loss: "+runningLoss);

        }
        final double averageLoss = runningLoss / foldsUsed;
        logger.info("Average loss: "+averageLoss);
        return averageLoss;
    }

    private DataSplit setTrainingAndValidationSets(int foldNumber, Iterable<? extends AbstractInstance> data) {
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
