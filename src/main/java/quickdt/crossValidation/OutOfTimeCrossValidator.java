package quickdt.crossValidation;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
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
            weightOfValidationSet = updateCrossValidationAndTrainingSets();
            if (currentValidationSet.size() > 0)
                break;
        }
        final double averageLoss = runningLoss / runningWeightOfValidationSet;
        logger.info("Average loss: "+averageLoss);
        return averageLoss;
    }

    private double initializeTrainingAndValidationSets(Iterable<? extends AbstractInstance> trainingData) {
        allTrainingData = Lists.<AbstractInstance>newArrayList();
        additionalTrainingData = Lists.<AbstractInstance>newArrayList();
        currentValidationSet = Lists.<AbstractInstance>newArrayList();

        int count = 0;
        DateTime lastTrainingInstanceTime = new DateTime();
        DateTime currentTime;
        double weightOfValidationSet = 0;
        for (AbstractInstance instance : trainingData) {
            allTrainingData.add(instance);
            currentTime = dateTimeExtractor.extractDateTime(instance);
            if (count < previousTrainingDataSetSize) {
                additionalTrainingData.add(instance);
                lastTrainingInstanceTime = currentTime;
            } else if (Minutes.minutesBetween(currentTime, lastTrainingInstanceTime).getMinutes() > validationTimeSliceInMinutes) {
                currentValidationSet.add(instance);
                weightOfValidationSet += instance.getWeight() ;
            }
            count++;
        }
        previousTrainingDataSetSize = additionalTrainingData.size();
        return weightOfValidationSet;
    }

    private double updateCrossValidationAndTrainingSets() {
        double weightOfValidationSet = 0;
        previousTrainingDataSetSize += additionalTrainingData.size();
        additionalTrainingData = currentValidationSet;

        DateTime timeOfInstance;
        DateTime validationSetStartTime = dateTimeExtractor.extractDateTime(allTrainingData.get(additionalTrainingData.size()));
        currentValidationSet = Lists.<AbstractInstance>newArrayList();
        AbstractInstance instance;
        for (int i = previousTrainingDataSetSize; i < allTrainingData.size(); i++) {
            instance = allTrainingData.get(i);
            timeOfInstance = dateTimeExtractor.extractDateTime(instance);

            if (Minutes.minutesBetween(validationSetStartTime, timeOfInstance).getMinutes() > validationTimeSliceInMinutes)
                break;
            currentValidationSet.add(instance);
            weightOfValidationSet += instance.getWeight();
        }
        return weightOfValidationSet;
    }
    }
