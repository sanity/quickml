package quickml.supervised.crossValidation;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.crossValLossFunctions.LossWithModelConfiguration;
import quickml.supervised.crossValidation.crossValLossFunctions.MultiLossFunctionWithModelConfigurations;

import java.util.*;


/**
 * Created by ian on 2/28/14.
 */
public class StationaryCrossValidator<R, P> extends CrossValidator<R, P> {
private static final  Logger logger =  LoggerFactory.getLogger(StationaryCrossValidator.class);

    protected static final int DEFAULT_NUMBER_OF_FOLDS = 4;
    protected int folds;
    protected int foldsUsed;
    protected CrossValLossFunction<P> lossFunction;


    /*
     * Create a new CrossValidator
     * @param folds The number folds to be used in the cross validation procedure
     */

    public StationaryCrossValidator(CrossValLossFunction<P> lossFunction) {
        this(DEFAULT_NUMBER_OF_FOLDS, lossFunction);
    }

    public StationaryCrossValidator(final int folds, CrossValLossFunction<P> lossFunction) {
        this(folds, folds, lossFunction);

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
            List<LabelPredictionWeight<P>> labelPredictionWeights = Utils.createLabelPredictionWeights(dataSplit.validation, predictiveModel);
            runningLoss+= lossFunction.getLoss(labelPredictionWeights);
            logger.info("running loss: "+runningLoss);

        }
        final double averageLoss = runningLoss / foldsUsed;
        logger.info("Average loss: "+averageLoss);
        return averageLoss;
    }

    public <PM extends PredictiveModel<R, P>> MultiLossFunctionWithModelConfigurations getMultipleCrossValidatedLossesWithModelConfiguration(PredictiveModelBuilder<R, PM> predictiveModelBuilder, Iterable<? extends Instance<R>> allTrainingData, MultiLossFunctionWithModelConfigurations<P> multiLossFunction) {
        DataSplit dataSplit;
        for (int currentFold = 0; currentFold < foldsUsed; currentFold++)  {
            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);
            List<LabelPredictionWeight<P>> labelPredictionWeights = Utils.createLabelPredictionWeights(dataSplit.validation, predictiveModel);
            multiLossFunction.updateRunningLosses(labelPredictionWeights);
        }
        multiLossFunction.normalizeRunningAverages();
        Map<String, LossWithModelConfiguration> lossMap = multiLossFunction.getLossesWithModelConfigurations();
        for (String lossFunctionName : lossMap.keySet()) {
            logger.info("Loss function: " + lossFunctionName + "loss: " + lossMap.get(lossFunctionName).getLoss() + ".  Weight of val set: " + multiLossFunction.getRunningWeight());
        }
        return multiLossFunction;

    }

    @Override
    public <PM extends PredictiveModel<R, P>,  PMB extends PredictiveModelBuilder<R, PM>> List<Pair<String, MultiLossFunctionWithModelConfigurations<P>>> getAttributeImportances(PredictiveModelBuilderFactory<R, PM, PMB> predictiveModelBuilderFactory, Map<String, Object> config,  Iterable<? extends Instance<R>> allTrainingData, final String primaryLossFunction, Set<String> attributes, Map<String, CrossValLossFunction<P>> lossFunctions) {
        //list of attributes are provided
        //initialize the loss functions for each attribute
        PMB predictiveModelBuilder = predictiveModelBuilderFactory.buildBuilder(config);

        Map<String, MultiLossFunctionWithModelConfigurations<P>> attributeToLossMap = Maps.newHashMap();
        for (String attribute : attributes) {
            attributeToLossMap.put(attribute, new MultiLossFunctionWithModelConfigurations<P>(lossFunctions, primaryLossFunction));
        }
        DataSplit dataSplit;
        for (int currentFold = 0; currentFold < foldsUsed; currentFold++)  {
            dataSplit = setTrainingAndValidationSets(currentFold, allTrainingData);
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(dataSplit.training);

            List<LabelPredictionWeight<P>> labelPredictionWeights;
            Set<String> attributesToIgnore = Sets.newHashSet();
            for (String attribute : attributes) {
                attributesToIgnore.add(attribute);
                labelPredictionWeights = Utils.createLabelPredictionWeightsWithoutAttributes(dataSplit.validation, predictiveModel, attributesToIgnore);
                MultiLossFunctionWithModelConfigurations<P> multiLossFunction = attributeToLossMap.get(attribute);
                multiLossFunction.updateRunningLosses(labelPredictionWeights);

                attributesToIgnore.remove(attribute);
            }
        }

        for (String attribute : attributes) {
            MultiLossFunctionWithModelConfigurations<P> multiLossFunction = attributeToLossMap.get(attribute);
            multiLossFunction.normalizeRunningAverages();
        }
        List<Pair<String, MultiLossFunctionWithModelConfigurations<P>>> attributesWithLosses = Lists.newArrayList();
        for (String attribute : attributeToLossMap.keySet()) {
            attributesWithLosses.add(new Pair<String, MultiLossFunctionWithModelConfigurations<P>>(attribute, attributeToLossMap.get(attribute)));
        }
        //sort in descending order.  The higher the primary loss, the more damage was done by removing the attribute
        Collections.sort(attributesWithLosses, new Comparator<Pair<String, MultiLossFunctionWithModelConfigurations<P>>>() {
                    @Override
                    public int compare(Pair<String, MultiLossFunctionWithModelConfigurations<P>> o1, Pair<String, MultiLossFunctionWithModelConfigurations<P>> o2) {
                        if (o1.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss() <
                                o2.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss()) {
                            return 1;
                        } else if (o1.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss() ==
                                o2.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction).getLoss()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
                }
        );

        for (Pair<String, MultiLossFunctionWithModelConfigurations<P>> pair : attributesWithLosses) {
           // logger.info("attribute Name function: " + pair.getValue0() + "losses: " + pair.getValue1().getLossesWithModelConfigurations().get(primaryLossFunction) + ".  Weight of val set: " + pair.getValue1().getRunningWeight());
        }
        return attributesWithLosses;
    }



    private DataSplit setTrainingAndValidationSets(int foldNumber, Iterable<? extends Instance<R>> data) {
        DataSplit dataSplit = new DataSplit();
        int count = 0;
        for (Instance<R> instance : data) {

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
