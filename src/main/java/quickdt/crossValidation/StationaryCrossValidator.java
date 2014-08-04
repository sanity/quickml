package quickdt.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickdt.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.Prediction;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.util.List;

//import com.javafx.tools.doclets.formats.html.SourceToHTMLConverter;

/**
 * Created by ian on 2/28/14.
 */
public class StationaryCrossValidator<Pr extends Prediction> extends CrossValidator<Pr> {
private static final  Logger logger =  LoggerFactory.getLogger(StationaryCrossValidator.class);

    private static final int DEFAULT_NUMBER_OF_FOLDS = 4;
    private int folds;
    private int foldsUsed;
    private CrossValLossFunction<Pr> lossFunction;


    /*
     * Create a new CrossValidator
     * @param folds The number folds to be used in the cross validation procedure
     */

    public StationaryCrossValidator(CrossValLossFunction<Pr> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = DEFAULT_NUMBER_OF_FOLDS;
        this.foldsUsed = DEFAULT_NUMBER_OF_FOLDS;
        this.lossFunction = lossFunction;
    }

    public StationaryCrossValidator(final int folds, CrossValLossFunction<Pr> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = folds;
        this.lossFunction = lossFunction;

    }

    public StationaryCrossValidator(final int folds, final int foldsUsed, CrossValLossFunction<Pr> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = foldsUsed;
        this.lossFunction = lossFunction;
    }

    public double getCrossValidatedLoss(PredictiveModelBuilder<PredictiveModel<Pr>> predictiveModelBuilder, Iterable<? extends AbstractInstance> allTrainingData) {
        double runningLoss = 0;
        DataSplit dataSplit;
        for (int currentFold = 0; currentFold < foldsUsed; currentFold++)  {
            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
            PredictiveModel<Pr> predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
            List<LabelPredictionWeight<Pr>> labelPredictionWeights = predictiveModel.createLabelPredictionWeights(dataSplit.validation);
            runningLoss+= lossFunction.getLoss(labelPredictionWeights);
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
