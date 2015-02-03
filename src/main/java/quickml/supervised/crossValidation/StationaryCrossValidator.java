package quickml.supervised.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.supervised.crossValidation.crossValLossFunctions.LossWithModelConfiguration;
import quickml.supervised.crossValidation.crossValLossFunctions.MultiLossFunctionWithModelConfigurations;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by ian on 2/28/14.
 */

//TODO[mk] Create Stationary Time data in the same manner as OutOfTimeData
public class StationaryCrossValidator<R, L, P>  {
    private static final Logger logger = LoggerFactory.getLogger(StationaryCrossValidator.class);

    protected static final int DEFAULT_NUMBER_OF_FOLDS = 4;
    protected final int folds;
    protected final int foldsUsed;
    protected CrossValLossFunction<L, P> lossFunction;


    /*
     * Create a new CrossValidator
     * @param folds The number folds to be used in the cross validation procedure
     */

    public StationaryCrossValidator(CrossValLossFunction<L,P> lossFunction) {
        this(DEFAULT_NUMBER_OF_FOLDS, lossFunction);
    }

    public StationaryCrossValidator(final int folds, CrossValLossFunction<L,P> lossFunction) {
        this(folds, folds, lossFunction);

    }

    public StationaryCrossValidator(final int folds, final int foldsUsed, CrossValLossFunction<L,P> lossFunction) {
        Preconditions.checkArgument(folds > 1, "Minimum number of folds is 2");
        this.folds = folds;
        this.foldsUsed = foldsUsed;
        this.lossFunction = lossFunction;
    }

////    @Override
//    public <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R, L, PM> predictiveModelBuilder, Iterable<? extends Instance<R, L>> allTrainingData) {
//        double runningLoss = 0;
//        DataSplit dataSplit;
//        for (int currentFold = 0; currentFold < foldsUsed; currentFold++) {
//            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
//            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
//            List<LabelPredictionWeight<L,P>> labelPredictionWeights = Utils.createLabelPredictionWeights(dataSplit.validation, predictiveModel);
//            runningLoss += lossFunction.getLoss(labelPredictionWeights);
//            logger.info("running loss: " + runningLoss);
//
//        }
//        final double averageLoss = runningLoss / foldsUsed;
//        logger.info("Average loss: " + averageLoss);
//        return averageLoss;
//    }

//    public <PM extends PredictiveModel<R, P>> MultiLossFunctionWithModelConfigurations getMultipleCrossValidatedLossesWithModelConfiguration(PredictiveModelBuilder<R, L, PM> predictiveModelBuilder, Iterable<? extends Instance<R, L>> allTrainingData, MultiLossFunctionWithModelConfigurations<L,P> multiLossFunction) {
//        DataSplit dataSplit;
//        for (int currentFold = 0; currentFold < foldsUsed; currentFold++) {
//            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
//            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
//            List<LabelPredictionWeight<L,P>> labelPredictionWeights = Utils.createLabelPredictionWeights(dataSplit.validation, predictiveModel);
//            multiLossFunction.updateRunningLosses(labelPredictionWeights);
//        }
//        multiLossFunction.normalizeRunningAverages();
//        Map<String, LossWithModelConfiguration> lossMap = multiLossFunction.getLossesWithModelConfigurations();
//        for (String lossFunctionName : lossMap.keySet()) {
//            logger.info("Loss function: " + lossFunctionName + "loss: " + lossMap.get(lossFunctionName).getLoss() + ".  Weight of val set: " + multiLossFunction.getRunningWeight());
//        }
//        return multiLossFunction;
//
//    }

//    @Override
//    public <PM extends PredictiveModel<R, P>, PMB extends PredictiveModelBuilder<R, L, PM>> List<Pair<String, MultiLossFunctionWithModelConfigurations<L,P>>> getAttributeImportances(PredictiveModelBuilderFactory<R, L, PM, PMB> predictiveModelBuilderFactory, Map<String, Object> config, Iterable<? extends Instance<R,L>> allTrainingData, final String primaryLossFunction, Set<String> attributes, Map<String, CrossValLossFunction<L,P>> lossFunctions) {
//        //list of attributes are provided
//        //initialize the loss functions for each attribute
//        PMB predictiveModelBuilder = predictiveModelBuilderFactory.buildBuilder(config);
//
//        Map<String, MultiLossFunctionWithModelConfigurations<L,P>> attributeToLossMap = Maps.newHashMap();
//        for (String attribute : attributes) {
//            attributeToLossMap.put(attribute, new MultiLossFunctionWithModelConfigurations<L,P>(lossFunctions, primaryLossFunction));
//        }
//        DataSplit dataSplit;
//        for (int currentFold = 0; currentFold < foldsUsed; currentFold++) {
//            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
//            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
//
//            List<LabelPredictionWeight<L,P>> labelPredictionWeights;
//            Set<String> attributesToIgnore = Sets.newHashSet();
//            for (String attribute : attributes) {
//                attributesToIgnore.add(attribute);
//                labelPredictionWeights = Utils.createLabelPredictionWeightsWithoutAttributes(dataSplit.validation, predictiveModel, attributesToIgnore);
//                MultiLossFunctionWithModelConfigurations<L,P> multiLossFunction = attributeToLossMap.get(attribute);
//                multiLossFunction.updateRunningLosses(labelPredictionWeights);
//
//                attributesToIgnore.remove(attribute);
//            }
//        }
//
//        for (String attribute : attributes) {
//            MultiLossFunctionWithModelConfigurations<L,P> multiLossFunction = attributeToLossMap.get(attribute);
//            multiLossFunction.normalizeRunningAverages();
//        }
//        List<Pair<String, MultiLossFunctionWithModelConfigurations<L,P>>> attributesWithLosses = Lists.newArrayList();
//        for (String attribute : attributeToLossMap.keySet()) {
//            attributesWithLosses.add(new Pair<>(attribute, attributeToLossMap.get(attribute)));
//        }
//        //sort in descending order.  The higher the primary loss, the more damage was done by removing the attribute
//        Collections.sort(attributesWithLosses, new AttributeWithLossComparator<L,P>(primaryLossFunction));
//        return attributesWithLosses;
//    }


    private DataSplit setTrainingAndValidationSets(int foldNumber, Iterable<? extends Instance<R,L>> data) {
        DataSplit dataSplit = new DataSplit();
        int count = 0;
        for (Instance<R, L> instance : data) {

            if (count % folds == foldNumber) //(count > testSetLowerBound && count < testSetUpperBound)//
                dataSplit.validation.add(instance);
            else
                dataSplit.training.add(instance);
            count++;
        }
        return dataSplit;
    }

    class DataSplit {
        public List<Instance<R, L>> training;
        public List<Instance<R, L>> validation;

        public DataSplit() {
            training = Lists.newArrayList();
            validation = Lists.newArrayList();
        }
    }
}
