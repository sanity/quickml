package quickdt.crossValidation;

import com.google.common.collect.Iterables;
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
    private int onlineUpdatesBeforeRebuild = 0;

    List<AbstractInstance> allTrainingData;
    List<AbstractInstance> currentTrainingSet;
    List<AbstractInstance> currentValidationSet;


    public OutOfTimeCrossValidator(CrossValLoss crossValLoss, double fractionOfDataForCrossValidation, double validationTimeSliceInMinutes, int onlineUpdatesBeforeRebuild, DateTimeExtractor dateTimeExtractor) {
        this.crossValLoss = crossValLoss;
        this.fractionOfDataForCrossValidation = fractionOfDataForCrossValidation;
        this.dateTimeExtractor = dateTimeExtractor;
        this.validationTimeSliceInMinutes = validationTimeSliceInMinutes;
        this.onlineUpdatesBeforeRebuild = onlineUpdatesBeforeRebuild;
    }

    @Override
    public double getCrossValidatedLoss(PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder, Iterable<? extends AbstractInstance> allTrainingData) {

        double runningLoss = 0;
        double runningWeightOfValidatioSet = 0;
        double weightOfValidationSet = initializeTrainingAndValidationSets(allTrainingData);

        while (true) {

            PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(currentTrainingSet); //use online predictiveModelBuilder
            runningLoss+=crossValLoss.getLoss(currentValidationSet, predictiveModel)*weightOfValidationSet;
            runningWeightOfValidatioSet += weightOfValidationSet;
            weightOfValidationSet = updateCrossValidationAndTrainingSets();

            if (currentValidationSet.size() > 0)
                break;
        }
        final double averageLoss = runningLoss / runningWeightOfValidatioSet;
        logger.info("Average loss: "+averageLoss);
        return averageLoss;
    }

    private double initializeTrainingAndValidationSets(Iterable<? extends AbstractInstance> trainingData) {
        allTrainingData = Lists.<AbstractInstance>newArrayList();
        currentTrainingSet = Lists.<AbstractInstance>newArrayList();
        currentValidationSet = Lists.<AbstractInstance>newArrayList();

        int count = 0;
        int traningDataSize = Iterables.size(trainingData);
        int currentTrainingDataSize = (int) (traningDataSize * fractionOfDataForCrossValidation);
        DateTime lastTrainingInstanceTime = new DateTime();
        DateTime currentTime;
        double weightOfValidationSet = 0;
        for (AbstractInstance instance : trainingData) {
            allTrainingData.add(instance);
            currentTime = dateTimeExtractor.extractDateTime(instance);
            if (count < currentTrainingDataSize) {
                currentTrainingSet.add(instance);
                lastTrainingInstanceTime = currentTime;
            } else if (Minutes.minutesBetween(currentTime, lastTrainingInstanceTime).getMinutes() > validationTimeSliceInMinutes) {
                currentValidationSet.add(instance);
                weightOfValidationSet += instance.getWeight() ;
            }
            count++;
        }
        return weightOfValidationSet;
    }

    private double updateCrossValidationAndTrainingSets() {
        double weightOfValidationSet = 0;
        for (AbstractInstance instance : currentValidationSet)
            currentTrainingSet.add(instance);

        DateTime timeOfInstance;
        DateTime validationSetStartTime = dateTimeExtractor.extractDateTime(allTrainingData.get(currentTrainingSet.size());
        currentValidationSet = Lists.<AbstractInstance>newArrayList();
        AbstractInstance instance;
        for (int i=currentTrainingSet.size(); i < allTrainingData.size(); i++) {
            instance = allTrainingData.get(i);
            timeOfInstance = dateTimeExtractor.extractDateTime(instance);

            if ( Minutes.minutesBetween(validationSetStartTime, timeOfInstance).getMinutes() > validationTimeSliceInMinutes)
                break;
            currentValidationSet.add(instance);
            weightOfValidationSet += instance.getWeight();
        }
        return weightOfValidationSet;
    }
    }
