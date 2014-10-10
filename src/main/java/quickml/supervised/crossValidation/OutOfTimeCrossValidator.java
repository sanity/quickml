package quickml.supervised.crossValidation;

import com.google.common.collect.Lists;
import org.javatuples.Pair;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.InstanceImpl;
import quickml.data.PredictionMap;
import quickml.experiments.TwoStageModel;
import quickml.experiments.TwoStageModelBuilder;
import quickml.supervised.Utils;
import quickml.supervised.crossValidation.crossValLossFunctions.LabelPredictionWeight;
import quickml.supervised.crossValidation.crossValLossFunctions.WeightedAUCCrossValLossFunction;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.data.Instance;
import quickml.supervised.PredictiveModel;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.crossValidation.crossValLossFunctions.CrossValLossFunction;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public class OutOfTimeCrossValidator<R, P> extends CrossValidator<R, P>{

    private static final Logger logger = LoggerFactory.getLogger(OutOfTimeCrossValidator.class);

    List<Instance<R>> allTrainingData;
    List<Instance<R>> trainingDataToAddToPredictiveModel;
    List<Instance<R>> validationSet;

    final private CrossValLossFunction<P> crossValLossFunction;
    private double fractionOfDataForCrossValidation = 0.25;

    private final DateTimeExtractor<R> dateTimeExtractor;
    private  DateTimeExtractor<AttributesMap> dateTimeExtractorTwoStage = new TestDateTimeExtractor();
    DateTime timeOfFirstInstanceInValidationSet;
    DateTime leastOuterBoundOfValidationSet;

    final Period durationOfValidationSet;
    private DateTime maxTime;
    private double weightOfValidationSet;
    private int currentTrainingSetSize = 0;
    boolean doNotSort = false;

    private double weightOfValidationSet1=0;
    private int currentTrainingSetSize1 = 0;
    private double weightOfValidationSet2;
    private int currentTrainingSetSize2 = 0;
    int clicksInValSet = 0;
    List<Instance<AttributesMap>> allTwoStageValidationData;
    List<Instance<AttributesMap>> allT1;
    List<Instance<AttributesMap>> trainingDataToAddToPredictiveModel1;
    List<Instance<AttributesMap>> validationSet1;
    List<Instance<AttributesMap>> allT2;
    List<Instance<AttributesMap>> trainingDataToAddToPredictiveModel2;
    List<Instance<AttributesMap>> validationSet2;
    DateTime timeOfFirstInstanceInValidationSetR;
    DateTime leastUpperBoundOfValidationSetR;
    int lastIndexInValSet =0;
    int lastIndexInAllT1 = 0;
    int lastIndexInAllT2 = 0;



    public OutOfTimeCrossValidator(CrossValLossFunction<P> crossValLossFunction, double fractionOfDataForCrossValidation, int validationTimeSliceHours, DateTimeExtractor dateTimeExtractor) {
        this.crossValLossFunction = crossValLossFunction;
        this.fractionOfDataForCrossValidation = fractionOfDataForCrossValidation;
        this.dateTimeExtractor = dateTimeExtractor;
        this.durationOfValidationSet = new Period(validationTimeSliceHours, 0, 0, 0);
    }

    public OutOfTimeCrossValidator<R, P> doNotSort(boolean doNotSort) {
        this.doNotSort = doNotSort;
        return this;
    }

    @Override
    public <PM extends PredictiveModel<R, P>> double getCrossValidatedLoss(PredictiveModelBuilder<R, PM> predictiveModelBuilder, Iterable<? extends Instance<R>> rawTrainingData) {

        initializeTrainingAndValidationSets(rawTrainingData);

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        while (!validationSet.isEmpty()) {
            PM predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingDataToAddToPredictiveModel);
            List<LabelPredictionWeight<P>> labelPredictionWeights = Utils.createLabelPredictionWeights(validationSet, predictiveModel);
            runningLoss += crossValLossFunction.getLoss(labelPredictionWeights) * weightOfValidationSet;
            runningWeightOfValidationSet += weightOfValidationSet;
            logger.debug("Running average Loss: " + runningLoss / runningWeightOfValidationSet + ", running weight: " + runningWeightOfValidationSet);

            updateTrainingSet();
            updateCrossValidationSet();
        }
        final double averageLoss = runningLoss / runningWeightOfValidationSet;
        logger.info("Average loss: " + averageLoss + ", runningWeight: " + runningWeightOfValidationSet);

        return averageLoss;
    }

    public double getTwoStageCrossValidatedLoss(TwoStageModelBuilder twoStageModelBuilder, Iterable<? extends Instance<AttributesMap>> t1,  Iterable<? extends Instance<AttributesMap>> t2, Iterable<? extends Instance<AttributesMap>> valIt ) {

        initializeTrainingAndValidationSetsTwoStageModel(t1, t2, valIt);
        WeightedAUCCrossValLossFunction lossFunction = new WeightedAUCCrossValLossFunction(1.0);

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        while (!validationSet1.isEmpty()) {
            List<Instance<AttributesMap>> mergedData  = mergeDataSets(trainingDataToAddToPredictiveModel1, trainingDataToAddToPredictiveModel2);
            TwoStageModel predictiveModel = twoStageModelBuilder.buildPredictiveModel(mergedData);
            List<LabelPredictionWeight<PredictionMap>> labelPredictionWeights = Utils.createLabelPredictionWeights(validationSet1, predictiveModel);
            runningLoss += lossFunction.getLoss(labelPredictionWeights) * weightOfValidationSet1;
            runningWeightOfValidationSet += weightOfValidationSet1;
            logger.debug("Running average Loss: " + runningLoss / runningWeightOfValidationSet + ", running weight: " + runningWeightOfValidationSet);
         //   logger.info("first val set instance: " + timeOfFirstInstanceInValidationSetR + "\n time of last instance " + leastUpperBoundOfValidationSetR + "\nclicks in val set: " + clicksInValSet);
            logger.info("clicks in val set: " + clicksInValSet);
            updateTrainingAndValidationSetTwoStage();
        }
        final double averageLoss = runningLoss / runningWeightOfValidationSet;
        logger.info("Average loss: " + averageLoss + ", runningWeight: " + runningWeightOfValidationSet);
        //logger.info("last index in val set: "+ lastIndexInValSet);
        return averageLoss;
    }

    private List<Instance<AttributesMap>> mergeDataSets(List<Instance<AttributesMap>> l1, List<Instance<AttributesMap>> l2) {
        List<Instance<AttributesMap>> twoStageInstances = Lists.newArrayList();
        twoStageInstances.addAll(l1);
        twoStageInstances.add(new InstanceImpl<AttributesMap>(new AttributesMap(), Double.valueOf(-100)));
        twoStageInstances.addAll(l2);
        return twoStageInstances;
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
        for (Instance<R> instance : allTrainingData) {
            timeOfInstance = dateTimeExtractor.extractDateTime(instance);
            if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSet)) {
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
      //  logger.info("timeOfFirstInstanceInValidationSet: " + timeOfFirstInstanceInValidationSet.toString() + "\nleastOuterBoundOfValidationSet: " + leastOuterBoundOfValidationSet.toString());
      //  logger.info("initial clicks in valset: " + clicksInValSet);
        currentTrainingSetSize = trainingDataToAddToPredictiveModel.size();
    }

    private void initializeTrainingAndValidationSetsTwoStageModel(Iterable<? extends Instance<AttributesMap>> trainingData1, Iterable<? extends Instance<AttributesMap>> trainingData2, Iterable<? extends Instance<AttributesMap>> validationDataTwoStage) {
        setAndSortAllTrainingDataTwoStageModel(trainingData1, trainingData2, validationDataTwoStage);
        setMaxValidationTimeTwoStage();

        Pair<Integer, Integer> initialTrainingSetSizes = getInitialSizesForTrainDataTwoStage();
        trainingDataToAddToPredictiveModel1 = Lists.<Instance<AttributesMap>>newArrayListWithExpectedSize(initialTrainingSetSizes.getValue0());
        trainingDataToAddToPredictiveModel2 = Lists.<Instance<AttributesMap>>newArrayListWithExpectedSize(initialTrainingSetSizes.getValue1());

        validationSet1 = Lists.<Instance<AttributesMap>>newArrayList();

        DateTime timeOfInstance;
        timeOfFirstInstanceInValidationSetR = dateTimeExtractorTwoStage.extractDateTime(allT1.get(initialTrainingSetSizes.getValue0()));
        leastUpperBoundOfValidationSetR = timeOfFirstInstanceInValidationSetR.plus(durationOfValidationSet);

        weightOfValidationSet1 = 0;
        //create training instances
        for (Instance<AttributesMap> instance : allT1) {
            timeOfInstance = dateTimeExtractorTwoStage.extractDateTime(instance);
            if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSetR)) {
                trainingDataToAddToPredictiveModel1.add(instance);
            } else {
                break;
            }
        }
        currentTrainingSetSize1 = trainingDataToAddToPredictiveModel1.size();
        lastIndexInAllT2 = trainingDataToAddToPredictiveModel2.size() -1;


        for (Instance<AttributesMap> instance : allT2) {
            timeOfInstance = dateTimeExtractorTwoStage.extractDateTime(instance);
            if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSetR)) {
                trainingDataToAddToPredictiveModel2.add(instance);
            } else {
                break;
            }
        }
        currentTrainingSetSize2 = trainingDataToAddToPredictiveModel2.size();
        lastIndexInAllT2 = trainingDataToAddToPredictiveModel2.size() -1;
        int initialInstancesToSkip = 0;
        clicksInValSet = 0;
        //create validation
        for (Instance<AttributesMap> instance : allTwoStageValidationData)     {
            timeOfInstance = dateTimeExtractorTwoStage.extractDateTime(instance);

            if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSetR)) {
                initialInstancesToSkip++;
                continue;
            } else if (timeOfInstance.isBefore(leastUpperBoundOfValidationSetR))  {
                validationSet1.add(instance);
                weightOfValidationSet1 += instance.getWeight();
                if (((Double)instance.getLabel()).equals(1.0))  {
                    clicksInValSet++;
                }
            } else  {
                break;
            }
        }
     lastIndexInValSet  = initialInstancesToSkip + validationSet1.size()-1;

    }

    private void updateTrainingSet() {
        trainingDataToAddToPredictiveModel = validationSet;
        currentTrainingSetSize += trainingDataToAddToPredictiveModel.size();
    }

    private void updateTrainingAndValidationSetTwoStage() {
        timeOfFirstInstanceInValidationSetR = leastUpperBoundOfValidationSetR;
        leastUpperBoundOfValidationSetR = timeOfFirstInstanceInValidationSetR.plus(durationOfValidationSet);
        trainingDataToAddToPredictiveModel1 = Lists.newArrayList();
        trainingDataToAddToPredictiveModel2 = Lists.newArrayList();
        validationSet1 = Lists.newArrayList();
        if (lastIndexInValSet >= allTwoStageValidationData.size()-1)
            return;

        for (int i = lastIndexInAllT1+1; i<allT1.size(); i++) {//<AttributesMap> instance : allT1)     {
            Instance<AttributesMap> instance = allT1.get(i);
            DateTime timeOfInstance = dateTimeExtractorTwoStage.extractDateTime(instance);

            if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSetR)) {
                trainingDataToAddToPredictiveModel1.add(instance);
            } else {
                break;
            }
        }
        lastIndexInAllT1 += trainingDataToAddToPredictiveModel1.size();

        for (int i = lastIndexInAllT2+1; i<allT2.size(); i++) {//<AttributesMap> instance : allT1)     {
            Instance<AttributesMap> instance = allT2.get(i);
            DateTime timeOfInstance = dateTimeExtractorTwoStage.extractDateTime(instance);

            if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSetR)) {
                trainingDataToAddToPredictiveModel2.add(instance);
            } else {
                break;
            }
        }

        lastIndexInAllT2 += trainingDataToAddToPredictiveModel2.size();

       // logger.info("weight before update " + weightOfValidationSet1);
        weightOfValidationSet1 = 0;
        for (int i = lastIndexInValSet+1; i<allTwoStageValidationData.size(); i++) {//<AttributesMap> instance : allT1)     {
            Instance<AttributesMap> instance = allTwoStageValidationData.get(i);
            DateTime timeOfInstance = dateTimeExtractorTwoStage.extractDateTime(instance);

            if (timeOfInstance.isBefore(timeOfFirstInstanceInValidationSetR))
                throw new RuntimeException("validation instances not sorted or traversed propperly \n ideal first instance time" + timeOfFirstInstanceInValidationSetR.toString() + "\n least upper bound: " + leastUpperBoundOfValidationSetR.toString() + "\ninstance time: " + timeOfInstance.toString() );

            if (timeOfInstance.isBefore(leastUpperBoundOfValidationSetR)) {
                validationSet1.add(instance);
                weightOfValidationSet1 += instance.getWeight();
                if (((Double)instance.getLabel()).equals(1.0))  {
                    clicksInValSet++;
                }
            } else {
                break;
            }
        }
        lastIndexInValSet += validationSet1.size();
    }

    private void updateCrossValidationSet() {
        clearValidationSet();
        if (!newValidationSetExists()) {
            return;
        }
        timeOfFirstInstanceInValidationSet = leastOuterBoundOfValidationSet;
        leastOuterBoundOfValidationSet = timeOfFirstInstanceInValidationSet.plus(durationOfValidationSet);
   //     logger.info("first val set instance: " + timeOfFirstInstanceInValidationSet + "\n time of last instance " + leastOuterBoundOfValidationSet);

        while(validationSet.isEmpty()) {
            for (int i = currentTrainingSetSize; i < allTrainingData.size(); i++) {
                Instance<R> instance = allTrainingData.get(i);
                DateTime timeOfInstance = dateTimeExtractor.extractDateTime(instance);
                if (timeOfInstance.isBefore(leastOuterBoundOfValidationSet)) {
                    validationSet.add(instance);
                    weightOfValidationSet += instance.getWeight();
                    if (instance.getLabel().equals(Double.valueOf(1.0)))
                        clicksInValSet++;
                } else
                    break;
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

    private void setMaxValidationTimeTwoStage() {
        //don't need the other stage
        Instance<AttributesMap> latestInstance = allT1.get(allT1.size() - 1);
        maxTime = dateTimeExtractorTwoStage.extractDateTime(latestInstance);
    }

    private int getInitialSizeForTrainData() {
        int initialTrainingSetSize = (int) (allTrainingData.size() * (1 - fractionOfDataForCrossValidation));
        verifyInitialValidationSetExists(initialTrainingSetSize);
        return initialTrainingSetSize;
    }

    private Pair<Integer, Integer> getInitialSizesForTrainDataTwoStage() {
        int initialTrainingSetSize1 = (int) (allT1.size() * (1 - fractionOfDataForCrossValidation));
        verifyInitialValidationSetExistsTwoStage(initialTrainingSetSize1, allT1);

        int initialTrainingSetSize2 = (int) (allT2.size() * (1 - fractionOfDataForCrossValidation));
        verifyInitialValidationSetExistsTwoStage(initialTrainingSetSize2, allT2);

        return new Pair<Integer, Integer>(initialTrainingSetSize1, initialTrainingSetSize2);
    }

    private void verifyInitialValidationSetExists(int initialTrainingSetSize) {
        if (initialTrainingSetSize == allTrainingData.size()) {
            throw new RuntimeException("fractionOfDataForCrossValidation must be non zero");
        }
    }

    private void verifyInitialValidationSetExistsTwoStage(int initialTrainingSetSize, List<Instance<AttributesMap>> testList) {
        if (initialTrainingSetSize == testList.size()) {
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

    private void setAndSortAllTrainingDataTwoStageModel(Iterable<? extends Instance<AttributesMap>> trainingData1, Iterable<? extends Instance<AttributesMap>> trainingData2, Iterable<? extends Instance<AttributesMap>> validationDataTwoStage) {
        this.allTwoStageValidationData = Lists.<Instance<AttributesMap>>newArrayList();
        this.allT1 = Lists.<Instance<AttributesMap>>newArrayList();
        this.allT2 = Lists.<Instance<AttributesMap>>newArrayList();
        for (Instance<AttributesMap> instance : trainingData1) {
            this.allT1.add(instance);
        }

        for (Instance<AttributesMap> instance : trainingData2) {
            this.allT2.add(instance);
        }
        for (Instance<AttributesMap> instance : validationDataTwoStage) {
            this.allTwoStageValidationData.add(instance);
        }

        Comparator<Instance<AttributesMap>> comparator = new Comparator<Instance<AttributesMap>>() {
            @Override
            public int compare(Instance<AttributesMap> o1, Instance<AttributesMap> o2) {
                DateTime firstInstance = dateTimeExtractorTwoStage.extractDateTime(o1);
                DateTime secondInstance = dateTimeExtractorTwoStage.extractDateTime(o2);
                if (firstInstance.isAfter(secondInstance)) {
                    return 1;
                } else if (firstInstance.isEqual(secondInstance)) {
                    return 0;
                } else {
                    return -1;
                }
            }
        };

        Collections.sort(this.allT1, comparator);
        Collections.sort(this.allT2, comparator);
        Collections.sort(this.allTwoStageValidationData, comparator);

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

