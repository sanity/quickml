package quickml.supervised.crossValidation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.supervised.crossValidation.crossValLossFunctions.LossWithModelConfiguration;
import quickml.supervised.crossValidation.crossValLossFunctions.MultiLossFunctionWithModelConfigurations;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.supervised.inspection.AttributeWithLossComparator;

import java.io.Serializable;
import java.util.*;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public class OutOfTimeCrossValidator<R, L, P> extends CrossValidator<R, L, P> {

    private static final Logger logger = LoggerFactory.getLogger(OutOfTimeCrossValidator.class);

    List<Instance<R, L>> allTrainingData;
    List<Instance<R, L>> trainingDataToAddToPredictiveModel;
    List<Instance<R, L>> validationSet = Lists.<Instance<R,L>>newArrayList();

    final private CrossValLossFunction<L,P> crossValLossFunction;
    private double fractionOfDataForCrossValidation = 0.25;

    private final DateTimeExtractor<R,L> dateTimeExtractor;
    DateTime timeOfFirstInstanceInValidationSet;
    DateTime leastOuterBoundOfValidationSet;

    final Period durationOfValidationSet;
    private DateTime maxTime;
    private double weightOfValidationSet;
    private int currentTrainingSetSize = 0;
    int clicksInValSet = 0;

    public OutOfTimeCrossValidator(CrossValLossFunction<L,P> crossValLossFunction, double fractionOfDataForCrossValidation, int validationTimeSliceHours, DateTimeExtractor dateTimeExtractor) {
        this.crossValLossFunction = crossValLossFunction;
        this.fractionOfDataForCrossValidation = fractionOfDataForCrossValidation;
        this.dateTimeExtractor = dateTimeExtractor;
        this.durationOfValidationSet = new Period(validationTimeSliceHours, 0, 0, 0);
    }


    @Override
    public <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R, L, PM> predictiveModelBuilder, Iterable<? extends Instance<R, L>> rawTrainingData) {

        initializeTrainingAndValidationSets(rawTrainingData);

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        while (!validationSet.isEmpty()) {
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingDataToAddToPredictiveModel);
            List<LabelPredictionWeight<L,P>> labelPredictionWeights;
            List<Instance<R,L>> convertedValSet = validationSet;
            labelPredictionWeights = Utils.createLabelPredictionWeights(convertedValSet, predictiveModel);
            int positiveInstances = 0;
            for (LabelPredictionWeight<L, P> labelPredictionWeight : labelPredictionWeights) {
                if (labelPredictionWeight.getLabel().equals(Double.valueOf(1.0)))
                    positiveInstances++;
            }

            runningLoss += crossValLossFunction.getLoss(labelPredictionWeights) * weightOfValidationSet;

            runningWeightOfValidationSet += weightOfValidationSet;
            logger.info("Running average Loss: " + runningLoss / runningWeightOfValidationSet + ", running weight: " + runningWeightOfValidationSet + ". pos instances: " + positiveInstances);

            updateTrainingSet();
            updateCrossValidationSet();
        }
        final double averageLoss = runningLoss / runningWeightOfValidationSet;
        logger.info("Average loss: " + averageLoss + ", runningWeight: " + runningWeightOfValidationSet);

        return averageLoss;
    }



    public <PM extends PredictiveModel<R, P>> MultiLossFunctionWithModelConfigurations getMultipleCrossValidatedLossesWithModelConfiguration(PredictiveModelBuilder<R, L, PM> predictiveModelBuilder, Iterable<? extends Instance<R, L>> rawTrainingData, MultiLossFunctionWithModelConfigurations<L,P> multiLossFunction) {

        initializeTrainingAndValidationSets(rawTrainingData);

        while (!validationSet.isEmpty()) {
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingDataToAddToPredictiveModel);

            List<LabelPredictionWeight<L,P>> labelPredictionWeights;
            List<Instance<R,L>> convertedValSet = validationSet;
            labelPredictionWeights = Utils.createLabelPredictionWeights(convertedValSet, predictiveModel);

            multiLossFunction.updateRunningLosses(labelPredictionWeights);
            updateTrainingSet();
            updateCrossValidationSet();
        }
        multiLossFunction.normalizeRunningAverages();
        Map<String, LossWithModelConfiguration> lossMap = multiLossFunction.getLossesWithModelConfigurations();
        for (String lossFunctionName : lossMap.keySet()) {
            logger.info("Loss function: " + lossFunctionName + "loss: " + lossMap.get(lossFunctionName).getLoss() + ".  Weight of val set: " + multiLossFunction.getRunningWeight());
        }
        return multiLossFunction;
    }

    @Override
    public <PM extends PredictiveModel<R, P>,  PMB extends PredictiveModelBuilder<R, L, PM>> List<Pair<String, MultiLossFunctionWithModelConfigurations<L,P>>> getAttributeImportances(PredictiveModelBuilderFactory<R, L, PM, PMB> predictiveModelBuilderFactory, Map<String, Object> config,  Iterable<? extends Instance<R,L>> rawTrainingData, final String primaryLossFunction, Set<String> attributes, Map<String, CrossValLossFunction<L,P>> lossFunctions) {
        //list of attributes are provided
        //initialize the loss functions for each attribute
        PMB predictiveModelBuilder = predictiveModelBuilderFactory.buildBuilder(config);
        Map<String, MultiLossFunctionWithModelConfigurations<L,P>> attributeToLossMap = Maps.newHashMap();

        for (String attribute : attributes) {
            attributeToLossMap.put(attribute, new MultiLossFunctionWithModelConfigurations<>(lossFunctions, primaryLossFunction));
        }
        initializeTrainingAndValidationSets(rawTrainingData);
        while (!validationSet.isEmpty()) {
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingDataToAddToPredictiveModel);

            List<LabelPredictionWeight<L,P>> labelPredictionWeights;
            Set<String> attributesToIgnore = Sets.newHashSet();
            for (String attribute : attributes) {
                attributesToIgnore.add(attribute);
                labelPredictionWeights = Utils.createLabelPredictionWeightsWithoutAttributes(validationSet, predictiveModel, attributesToIgnore);
                MultiLossFunctionWithModelConfigurations<L,P> multiLossFunction = attributeToLossMap.get(attribute);
                multiLossFunction.updateRunningLosses(labelPredictionWeights);

                attributesToIgnore.remove(attribute);
            }
            updateTrainingSet();
            updateCrossValidationSet();
        }

        for (String attribute : attributes) {
            MultiLossFunctionWithModelConfigurations<L,P> multiLossFunction = attributeToLossMap.get(attribute);
            multiLossFunction.normalizeRunningAverages();
        }
        List<Pair<String, MultiLossFunctionWithModelConfigurations<L,P>>> attributesWithLosses = Lists.newArrayList();
        for (String attribute : attributeToLossMap.keySet()) {
            attributesWithLosses.add(new Pair<>(attribute, attributeToLossMap.get(attribute)));
        }
        //sort in descending order.  The higher the loss, the more damage was done by removing the attribute
        Collections.sort(attributesWithLosses, new AttributeWithLossComparator<L,P>(primaryLossFunction));
        return attributesWithLosses;
    }


    private void initializeTrainingAndValidationSets(Iterable<? extends Instance<R,L>> rawTrainingData) {
        setAndSortAllTrainingData(rawTrainingData);

        int initialTrainingSetSize = getInitialSizeForTrainData();
        trainingDataToAddToPredictiveModel = Lists.newArrayListWithExpectedSize(initialTrainingSetSize);
        validationSet = Lists.newArrayList();

        DateTime timeOfInstance;

        timeOfFirstInstanceInValidationSet = dateTimeExtractor.extractDateTime(allTrainingData.get(initialTrainingSetSize));
        leastOuterBoundOfValidationSet = timeOfFirstInstanceInValidationSet.plus(durationOfValidationSet);

        weightOfValidationSet = 0;
        clicksInValSet = 0;
        boolean secondPass = false;
        while (validationSet.isEmpty()) {
            for (Instance<R,L> instance : allTrainingData) {
                timeOfInstance = dateTimeExtractor.extractDateTime(instance);
                if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSet) && !secondPass) {
                    trainingDataToAddToPredictiveModel.add(instance);
                } else if (timeOfInstance.isBefore(leastOuterBoundOfValidationSet)) {
                    validationSet.add(instance);
                    weightOfValidationSet += instance.getWeight();
                    if (instance.getLabel().equals(Double.valueOf(1.0))) {
                        clicksInValSet++;
                    }

                } else {
                    break;
                }
            }
            if (validationSet.isEmpty()) {
                secondPass = true;
                timeOfFirstInstanceInValidationSet = leastOuterBoundOfValidationSet;
                leastOuterBoundOfValidationSet = timeOfFirstInstanceInValidationSet.plus(durationOfValidationSet);
                logger.info("bumping boundaries: currentTrainingSetSize: " + currentTrainingSetSize + ", allTrainingData.size" + allTrainingData.size());
            }

        }
        //  logger.info("timeOfFirstInstanceInValidationSet: " + timeOfFirstInstanceInValidationSet.toString() + "\nleastOuterBoundOfValidationSet: " + leastOuterBoundOfValidationSet.toString());
        //  logger.info("initial clicks in valset: " + clicksInValSet);
        currentTrainingSetSize = trainingDataToAddToPredictiveModel.size();
    }

    private void updateTrainingSet() {
        trainingDataToAddToPredictiveModel = validationSet;
        currentTrainingSetSize += trainingDataToAddToPredictiveModel.size();
    }

    private void updateCrossValidationSet() {

        clearValidationSet();
        if (!newValidationSetExists()) {
            return;
        }
        logger.info("cleared validation set");
        timeOfFirstInstanceInValidationSet = leastOuterBoundOfValidationSet;
        leastOuterBoundOfValidationSet = timeOfFirstInstanceInValidationSet.plus(durationOfValidationSet);
        logger.info("first val set instance: " + timeOfFirstInstanceInValidationSet.toString() + "\n time of last instance " + leastOuterBoundOfValidationSet.toString());
        logger.info("currentTrainingSetSize: " + currentTrainingSetSize + "\nallTrainingData.size" + allTrainingData.size());
        while (validationSet.isEmpty()) {
            for (int i = currentTrainingSetSize; i < allTrainingData.size(); i++) {
                Instance<R, L> instance = allTrainingData.get(i);
                DateTime timeOfInstance = dateTimeExtractor.extractDateTime(instance);
                if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSet))
                    throw new RuntimeException("val instance before earliest possible boundary: " + timeOfInstance.toString());
                if (timeOfInstance.isBefore(leastOuterBoundOfValidationSet)) {
                    validationSet.add(instance);
                    weightOfValidationSet += instance.getWeight();
                    if (instance.getLabel().equals(Double.valueOf(1.0)))
                        clicksInValSet++;
                } else
                    break;
            }
            if (validationSet.isEmpty()) {
                timeOfFirstInstanceInValidationSet = leastOuterBoundOfValidationSet;
                leastOuterBoundOfValidationSet = timeOfFirstInstanceInValidationSet.plus(durationOfValidationSet);
                logger.info("bumping boundaries: currentTrainingSetSize: " + currentTrainingSetSize + ", allTrainingData.size" + allTrainingData.size());
            }
        }
        logger.info("clicks in val set: " + clicksInValSet);
    }

    private void clearValidationSet() {
        weightOfValidationSet = 0;
        validationSet = Lists.newArrayList();
    }



    private int getInitialSizeForTrainData() {
        int initialTrainingSetSize = (int) (allTrainingData.size() * (1 - fractionOfDataForCrossValidation));
        verifyInitialValidationSetExists(initialTrainingSetSize);
        return initialTrainingSetSize;
    }


    private void verifyInitialValidationSetExists(int initialTrainingSetSize) {
        if (initialTrainingSetSize == allTrainingData.size()) {
            throw new RuntimeException("fractionOfDataForCrossValidation must be non zero");
        }
    }

    private boolean newValidationSetExists() {
        return currentTrainingSetSize < allTrainingData.size();
    }

    private void setAndSortAllTrainingData(Iterable<? extends Instance<R,L>> rawTrainingData) {

        this.allTrainingData = Lists.newArrayList();
        for (Instance<R,L> instance : rawTrainingData) {
            this.allTrainingData.add(instance);
        }

        Comparator<Instance<R,L>> comparator = new Comparator<Instance<R,L>>() {
            @Override
            public int compare(Instance<R,L> o1, Instance<R,L> o2) {
                DateTime firstInstance = dateTimeExtractor.extractDateTime(o1);
                DateTime secondInstance = dateTimeExtractor.extractDateTime(o2);
                if (firstInstance.isAfter(secondInstance)) {
                    return 1;
                } else if (firstInstance.isEqual(secondInstance)) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };

        Collections.sort(this.allTrainingData, comparator);
    }

    //TODO[mk] maybe move this
    public static class TestDateTimeExtractor implements DateTimeExtractor<AttributesMap, Serializable> {
        @Override
        public DateTime extractDateTime(Instance<AttributesMap, Serializable> instance) {
            AttributesMap attributes = instance.getAttributes();
            int year = ((Double) attributes.get("timeOfArrival-year")).intValue();
            int month = ((Double) attributes.get("timeOfArrival-monthOfYear")).intValue();
            int day = ((Double) attributes.get("timeOfArrival-dayOfMonth")).intValue();
            int hour = ((Double) attributes.get("timeOfArrival-hourOfDay")).intValue();
            int minute = ((Double) attributes.get("timeOfArrival-minuteOfHour")).intValue();
            return new DateTime(year, month, day, hour, minute, 0, 0);
        }
    }

}

