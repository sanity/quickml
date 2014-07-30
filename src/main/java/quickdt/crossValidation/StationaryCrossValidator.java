package quickdt.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.crossValLossFunctions.ClassifierMSECrossValLossFunction;
import quickdt.crossValidation.crossValLossFunctions.ClassifierRMSECrossValLossFunction;
import quickdt.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Classifier;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.randomForest.RandomForest;

import java.util.List;

//import com.javafx.tools.doclets.formats.html.SourceToHTMLConverter;

/**
 * Created by ian on 2/28/14.
 */
public class StationaryCrossValidator<T extends PredictiveModel> extends CrossValidator<T> {
private static final  Logger logger =  LoggerFactory.getLogger(StationaryCrossValidator.class);

    private static final int DEFAULT_NUMBER_OF_FOLDS = 4;
    private int folds;
    private int foldsUsed;
    private CrossValLossFunction<T> lossFunction;


    /*
     * Create a new CrossValidator
     * @param folds The number folds to be used in the cross validation procedure
     */

    public StationaryCrossValidator(CrossValLossFunction<T> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = DEFAULT_NUMBER_OF_FOLDS;
        this.foldsUsed = DEFAULT_NUMBER_OF_FOLDS;
        this.lossFunction = lossFunction;
    }

    public StationaryCrossValidator(final int folds, CrossValLossFunction<T> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = folds;
        this.lossFunction = lossFunction;

    }

    public StationaryCrossValidator(final int folds, final int foldsUsed, CrossValLossFunction<T> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = foldsUsed;
        this.lossFunction = lossFunction;
    }

    public double getCrossValidatedLoss(PredictiveModelBuilder<T> predictiveModelBuilder, Iterable<? extends AbstractInstance> allTrainingData) {
        double runningLoss = 0;
        DataSplit dataSplit;
        for (int currentFold = 0; currentFold < foldsUsed; currentFold++)  {
            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
            T predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
            runningLoss+= lossFunction.getLoss(dataSplit.validation, predictiveModel);
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
