package quickml.supervised.crossValidation;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.supervised.crossValidation.crossValLossFunctions.LossWithModelConfiguration;
import quickml.supervised.crossValidation.crossValLossFunctions.MultiLossFunctionWithModelConfigurations;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;
import quickml.supervised.inspection.AttributeWithLossComparator;

import java.util.*;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public class OutOfTimeCrossValidator<R, P> extends CrossValidator<R, P> {

    private static final Logger logger = LoggerFactory.getLogger(OutOfTimeCrossValidator.class);

    List<Instance<R>> allTrainingData;
    List<Instance<R>> trainingDataToAddToPredictiveModel;
    List<Instance<R>> validationSet;
    Optional<LabelConverter<R>> labelConverter = Optional.absent();

    final private CrossValLossFunction<P> crossValLossFunction;
    private double fractionOfDataForCrossValidation = 0.25;

    private final DateTimeExtractor<R> dateTimeExtractor;
    DateTime timeOfFirstInstanceInValidationSet;
    DateTime leastOuterBoundOfValidationSet;

    final Period durationOfValidationSet;
    private DateTime maxTime;
    private double weightOfValidationSet;
    private int currentTrainingSetSize = 0;
    int clicksInValSet = 0;

    public OutOfTimeCrossValidator(CrossValLossFunction<P> crossValLossFunction, double fractionOfDataForCrossValidation, int validationTimeSliceHours, DateTimeExtractor dateTimeExtractor) {
        this.crossValLossFunction = crossValLossFunction;
        this.fractionOfDataForCrossValidation = fractionOfDataForCrossValidation;
        this.dateTimeExtractor = dateTimeExtractor;
        this.durationOfValidationSet = new Period(validationTimeSliceHours, 0, 0, 0);
    }

    public OutOfTimeCrossValidator<R, P> labelConverter(LabelConverter<R> labelConverter) {
        this.labelConverter = Optional.of(labelConverter);
        return this;
    }

    @Override
    public <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R, PM> predictiveModelBuilder, Iterable<? extends Instance<R>> rawTrainingData) {

        initializeTrainingAndValidationSets(rawTrainingData);

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        while (!validationSet.isEmpty()) {
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingDataToAddToPredictiveModel);
            List<LabelPredictionWeight<P>> labelPredictionWeights;
            List<Instance<R>> convertedValSet = validationSet;
            if (labelConverter.isPresent()) {
                convertedValSet = labelConverter.get().convertLabels(validationSet);
            }
            labelPredictionWeights = Utils.createLabelPredictionWeights(convertedValSet, predictiveModel);
            int positiveInstances = 0;
            for (LabelPredictionWeight<P> labelPredictionWeight : labelPredictionWeights) {
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

    public <PM extends PredictiveModel<R, P>> MultiLossFunctionWithModelConfigurations getMultipleCrossValidatedLossesWithModelConfiguration(PredictiveModelBuilder<R, PM> predictiveModelBuilder, Iterable<? extends Instance<R>> rawTrainingData, MultiLossFunctionWithModelConfigurations<P> multiLossFunction) {

        initializeTrainingAndValidationSets(rawTrainingData);

        while (!validationSet.isEmpty()) {
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingDataToAddToPredictiveModel);

            List<LabelPredictionWeight<P>> labelPredictionWeights;
            List<Instance<R>> convertedValSet = validationSet;
            if (labelConverter.isPresent()) {
                convertedValSet = labelConverter.get().convertLabels(validationSet);
            }

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
    public <PM extends PredictiveModel<R, P>,  PMB extends PredictiveModelBuilder<R, PM>> List<Pair<String, MultiLossFunctionWithModelConfigurations<P>>> getAttributeImportances(PredictiveModelBuilderFactory<R, PM, PMB> predictiveModelBuilderFactory, Map<String, Object> config,  Iterable<? extends Instance<R>> rawTrainingData, final String primaryLossFunction, Set<String> attributes, Map<String, CrossValLossFunction<P>> lossFunctions) {
        //list of attributes are provided
        //initialize the loss functions for each attribute
        PMB predictiveModelBuilder = predictiveModelBuilderFactory.buildBuilder(config);
        Map<String, MultiLossFunctionWithModelConfigurations<P>> attributeToLossMap = Maps.newHashMap();

        for (String attribute : attributes) {
            attributeToLossMap.put(attribute, new MultiLossFunctionWithModelConfigurations<P>(lossFunctions, primaryLossFunction));
        }
        initializeTrainingAndValidationSets(rawTrainingData);
        while (!validationSet.isEmpty()) {
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingDataToAddToPredictiveModel);

            List<LabelPredictionWeight<P>> labelPredictionWeights;
            Set<String> attributesToIgnore = Sets.newHashSet();
            for (String attribute : attributes) {
                attributesToIgnore.add(attribute);
                labelPredictionWeights = Utils.createLabelPredictionWeightsWithoutAttributes(validationSet, predictiveModel, attributesToIgnore);
                double positives = 0;
                for(LabelPredictionWeight<P> labelPredictionWeight: labelPredictionWeights) {
                    positives+=(Double)(labelPredictionWeight.getLabel()); //tracked for debugging purposes
                }
                MultiLossFunctionWithModelConfigurations<P> multiLossFunction = attributeToLossMap.get(attribute);
                multiLossFunction.updateRunningLosses(labelPredictionWeights);

                attributesToIgnore.remove(attribute);
            }
            updateTrainingSet();
            updateCrossValidationSet();
        }

        for (String attribute : attributes) {
            MultiLossFunctionWithModelConfigurations<P> multiLossFunction = attributeToLossMap.get(attribute);
            multiLossFunction.normalizeRunningAverages();
        }
        List<Pair<String, MultiLossFunctionWithModelConfigurations<P>>> attributesWithLosses = Lists.newArrayList();
        for (String attribute : attributeToLossMap.keySet()) {
            attributesWithLosses.add(new Pair<String, MultiLossFunctionWithModelConfigurations<P>>(attribute, attributeToLossMap.get(attribute)));
        }
        //sort in descending order.  The higher the loss, the more damage was done by removing the attribute
        Collections.sort(attributesWithLosses, new AttributeWithLossComparator<P>(primaryLossFunction));
        return attributesWithLosses;
    }


    private void initializeTrainingAndValidationSets(Iterable<? extends Instance<R>> rawTrainingData) {
        setAndSortAllTrainingData(rawTrainingData);
        setMaxValidationTime();

        int initialTrainingSetSize = getInitialSizeForTrainData();
        trainingDataToAddToPredictiveModel = Lists.<Instance<R>>newArrayListWithExpectedSize(initialTrainingSetSize);
        validationSet = Lists.<Instance<R>>newArrayList();

        DateTime timeOfInstance;

        timeOfFirstInstanceInValidationSet = dateTimeExtractor.extractDateTime(allTrainingData.get(initialTrainingSetSize));
        leastOuterBoundOfValidationSet = timeOfFirstInstanceInValidationSet.plus(durationOfValidationSet);

        weightOfValidationSet = 0;
        clicksInValSet = 0;
        boolean secondPass = false;
        while (validationSet.isEmpty()) {
            for (Instance<R> instance : allTrainingData) {
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

    private Set<String> getAllAttributesInTrainingSet(Iterable<? extends Instance<AttributesMap>> trainingData) {
        Set<String> attributes = Sets.newHashSet();
        for (Instance<AttributesMap> instance : trainingData) {
            attributes.addAll(instance.getAttributes().keySet());
        }
        return attributes;
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
                Instance<R> instance = allTrainingData.get(i);
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
        validationSet = Lists.<Instance<R>>newArrayList();
    }


    private void setMaxValidationTime() {
        Instance<R> latestInstance = allTrainingData.get(allTrainingData.size() - 1);
        maxTime = dateTimeExtractor.extractDateTime(latestInstance);
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

    private void setAndSortAllTrainingData(Iterable<? extends Instance<R>> rawTrainingData) {

        this.allTrainingData = Lists.<Instance<R>>newArrayList();
        for (Instance<R> instance : rawTrainingData) {
            this.allTrainingData.add(instance);
        }

        Comparator<Instance<R>> comparator = new Comparator<Instance<R>>() {
            @Override
            public int compare(Instance<R> o1, Instance<R> o2) {
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

    private void resetTrainingDataVariables() {
        weightOfValidationSet = 0;
        currentTrainingSetSize = 0;
        clicksInValSet = 0;

    }

    static class TestDateTimeExtractor implements DateTimeExtractor<AttributesMap> {
        @Override
        public DateTime extractDateTime(Instance<AttributesMap> instance) {
            AttributesMap attributes = instance.getAttributes();
            int year = ((Long) attributes.get("timeOfArrival-year")).intValue();
            int month = ((Long) attributes.get("timeOfArrival-monthOfYear")).intValue();
            int day = ((Long) attributes.get("timeOfArrival-dayOfMonth")).intValue();
            int hour = ((Long) attributes.get("timeOfArrival-hourOfDay")).intValue();
            int minute = ((Long) attributes.get("timeOfArrival-minuteOfHour")).intValue();
            return new DateTime(year, month, day, hour, minute, 0, 0);
        }
    }

}

