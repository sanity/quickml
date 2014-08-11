package quickdt.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.util.List;

/**
 * Created by ian on 2/28/14.
 */
public class StationaryCrossValidator<R, P> extends CrossValidator<R, P> {
private static final  Logger logger =  LoggerFactory.getLogger(StationaryCrossValidator.class);

    private static final int DEFAULT_NUMBER_OF_FOLDS = 4;
    private int folds;
    private int foldsUsed;
    private CrossValLossFunction<P> lossFunction;


    /*
     * Create a new CrossValidator
     * @param folds The number folds to be used in the cross validation procedure
     */

    public StationaryCrossValidator(CrossValLossFunction<P> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = DEFAULT_NUMBER_OF_FOLDS;
        this.foldsUsed = DEFAULT_NUMBER_OF_FOLDS;
        this.lossFunction = lossFunction;
    }

    public StationaryCrossValidator(final int folds, CrossValLossFunction<P> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = folds;
        this.lossFunction = lossFunction;

    }

    public StationaryCrossValidator(final int folds, final int foldsUsed, CrossValLossFunction<P> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = foldsUsed;
        this.lossFunction = lossFunction;
    }

    @Override
    public <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R, PM> predictiveModelBuilder, Iterable<? extends Instance<R>> allTrainingData) {
        double runningLoss = 0;
        DataSplit dataSplit;
        for (int currentFold = 0; currentFold < foldsUsed; currentFold++)  {
            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
            List<LabelPredictionWeight<P>> labelPredictionWeights = predictiveModel.createLabelPredictionWeights(dataSplit.validation);
            runningLoss+= lossFunction.getLoss(labelPredictionWeights);
            logger.info("running loss: "+runningLoss);

        }
        final double averageLoss = runningLoss / foldsUsed;
        logger.info("Average loss: "+averageLoss);
        return averageLoss;
    }

    private DataSplit setTrainingAndValidationSets(int foldNumber, Iterable<? extends Instance<R>> data) {
        DataSplit dataSplit = new DataSplit();
        int count = 0;
        for (Instance instance : data) {
            if (count%folds == foldNumber) //(count > testSetLowerBound && count < testSetUpperBound)//
                dataSplit.validation.add(instance);
            else
                dataSplit.training.add(instance);
            count++;
        }
        return dataSplit;
    }

    class DataSplit  {
        public List<Instance<R>> training;
        public List<Instance<R>> validation;

        public DataSplit() {
            training = Lists.<Instance<R>>newArrayList();
            validation = Lists.<Instance<R>>newArrayList();
        }
    }
}
