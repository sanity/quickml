package quickdt.crossValidation;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.util.List;

/**
 * Created by alexanderhawk on 5/5/14.
 */
public class OutOfTimeCrossValidator extends CrossValidator {

    private static final Logger logger =  LoggerFactory.getLogger(StationaryCrossValidator.class);

    private CrossValLoss crossValLoss;
    private double fractionOfDataForCrossValidation = 0.25;
    private double validationTimeSliceInMinutes;
    private DateTimeExtractor dateTimeExtractor;
    private int previousTrainingDataSetSize = 0;
    private  DateTime maxTrainingTime;
    private  DateTime maxMinutesInTrainSet;

    List<AbstractInstance> allTrainingData;
    List<AbstractInstance> additionalTrainingData;
    List<AbstractInstance> currentValidationSet;


    public OutOfTimeCrossValidator(CrossValLoss crossValLoss, double fractionOfDataForCrossValidation, double validationTimeSliceInMinutes, DateTimeExtractor dateTimeExtractor) {
        this.crossValLoss = crossValLoss;
        this.fractionOfDataForCrossValidation = fractionOfDataForCrossValidation;
        this.dateTimeExtractor = dateTimeExtractor;
        this.validationTimeSliceInMinutes = validationTimeSliceInMinutes;
    }

    @Override
    public double getCrossValidatedLoss(PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder, Iterable<? extends AbstractInstance> allTrainingData) {

        double runningLoss = 0;
        double runningWeightOfValidationSet = 0;
        double weightOfValidationSet = initializeTrainingAndValidationSets(allTrainingData);

        while (true) {
            PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(additionalTrainingData); //use online predictiveModelBuilder
            runningLoss += crossValLoss.getLoss(currentValidationSet, predictiveModel)*weightOfValidationSet;
            runningWeightOfValidationSet += weightOfValidationSet;
            logger.info("running Loss: " + runningLoss/runningWeightOfValidationSet);
            weightOfValidationSet = updateCrossValidationAndTrainingSets();
            if (currentValidationSet.size() == 0)
                break;
        }
        final double averageLoss = runningLoss / runningWeightOfValidationSet;
        logger.info("Average loss: "+averageLoss);
        return averageLoss;
    }

    private double initializeTrainingAndValidationSets(Iterable<? extends AbstractInstance> trainingData) {
        allTrainingData = Lists.<AbstractInstance>newArrayList();
        for (AbstractInstance instance : trainingData)
            allTrainingData.add(instance);
        maxTrainingTime = dateTimeExtractor.extractDateTime(allTrainingData.get(allTrainingData.size()-1)).minus(new Period(0,(int)validationTimeSliceInMinutes,0,0));//date time of last instannce  - reduced by the timeSlicOfthe ValidationSet


        additionalTrainingData = Lists.<AbstractInstance>newArrayList();
        currentValidationSet = Lists.<AbstractInstance>newArrayList();

        int count = 0;
        DateTime firstValidationInstanceTime = new DateTime();
        DateTime currentTime;
        int initialTrainingSetSize = (int)(allTrainingData.size()*(1-fractionOfDataForCrossValidation));
        if (initialTrainingSetSize < allTrainingData.size())
            firstValidationInstanceTime = dateTimeExtractor.extractDateTime(allTrainingData.get(initialTrainingSetSize));
        else
            logger.warn("fractionOfDataForCrossValidation must be non zero");

        double weightOfValidationSet = 0;
        for (AbstractInstance instance : allTrainingData) {
            currentTime = dateTimeExtractor.extractDateTime(instance);
            if (count < initialTrainingSetSize) {
                additionalTrainingData.add(instance);
            } else if (Minutes.minutesBetween(firstValidationInstanceTime,currentTime).getMinutes() <= validationTimeSliceInMinutes) {
                currentValidationSet.add(instance);
                weightOfValidationSet += instance.getWeight() ;
            }
            count++;
        }
        return weightOfValidationSet;
    }

    private double updateCrossValidationAndTrainingSets() {
        double weightOfValidationSet = 0;
        previousTrainingDataSetSize += additionalTrainingData.size();
        additionalTrainingData = currentValidationSet;
        currentValidationSet = Lists.<AbstractInstance>newArrayList();


        DateTime timeOfInstance;
        if (previousTrainingDataSetSize < allTrainingData.size()) {
            DateTime validationSetStartTime = dateTimeExtractor.extractDateTime(allTrainingData.get(previousTrainingDataSetSize));
            AbstractInstance instance;
            for (int i = previousTrainingDataSetSize; i < allTrainingData.size(); i++) {
                instance = allTrainingData.get(i);
                timeOfInstance = dateTimeExtractor.extractDateTime(instance);
                if (Minutes.minutesBetween(validationSetStartTime, timeOfInstance).getMinutes() > validationTimeSliceInMinutes || timeOfInstance.isAfter(maxTrainingTime))
                    break;
                currentValidationSet.add(instance);
                weightOfValidationSet += instance.getWeight();
            }
            return weightOfValidationSet;
        }
        else
            return 0;
    }
    }
