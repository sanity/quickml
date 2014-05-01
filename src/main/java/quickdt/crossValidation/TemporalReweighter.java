package quickdt.crossValidation;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexanderhawk on 4/17/14.
 */
public class TemporalReweighter {

        private Iterable<? extends AbstractInstance> trainingData;
        private final double testWindow = 7;
        private double minutesInValidationSet;
        private double testFraction;
        private CrossValLoss lossObject;
        private double trainingSetLifeTime;
        private static final DateTime timeSince2012 = new DateTime(2013,1,1,0,0,0,0);
        private List<InstanceWithTime> allTrainingDataWithTimes = Lists.<InstanceWithTime>newArrayList();
        private List<AbstractInstance> trainingSet = Lists.<AbstractInstance>newArrayList();
        private List<AbstractInstance> validationSet = Lists.<AbstractInstance>newArrayList();
        private List<InstanceWithTime> testSet = Lists.<InstanceWithTime>newArrayList();
        PredictiveModelBuilder<PredictiveModel> predictiveModelBuilder;

        private double timeIntervalOfData;

        public TemporalReweighter(PredictiveModelBuilder<PredictiveModel> predictiveModelBuilder, Iterable<? extends AbstractInstance> trainingData, CrossValLoss lossObject){
            this.predictiveModelBuilder = predictiveModelBuilder;
            this.trainingData = trainingData;
            this.lossObject = lossObject;
            this.timeIntervalOfData = createTimedInstances();
        }

        private double createTimedInstances() {
            double mostRecent;
            double arrivalTimeInMinutes;
            for(AbstractInstance instance : trainingData)  {
                 arrivalTimeInMinutes = getArrivalTimeInMinutes(instance);
                 allTrainingDataWithTimes.add(new InstanceWithTime(arrivalTimeInMinutes, instance));
            }

            double startTime = allTrainingDataWithTimes.get(0).time;
            double endTime   = allTrainingDataWithTimes.get(0).time;
            double presentTime;
            for(InstanceWithTime instanceWithTime : allTrainingDataWithTimes) {
                 presentTime = instanceWithTime.time;
                 if(presentTime < startTime)
                     startTime = presentTime;
                 if (presentTime > endTime)
                     endTime = presentTime;
            }

            for (InstanceWithTime instanceWithTime : allTrainingDataWithTimes)
                instanceWithTime.time = instanceWithTime.time - startTime;

            Collections.sort(allTrainingDataWithTimes);
            double timeIntervalOfData = endTime - startTime;
            return timeIntervalOfData;
        }

        private double getArrivalTimeInMinutes(AbstractInstance instance) {
            int year = (Integer)instance.getAttributes().get("timeOfArrival-year");
            int month = (Integer)instance.getAttributes().get("timeOfArrival-monthOfYear");
            int day = (Integer)instance.getAttributes().get("timeOfArrival-dayOfMonth");
            DateTime dateTime = new DateTime(year, month, day, 0, 0,0,0);
            return Minutes.minutesBetween(dateTime, timeSince2012).getMinutes();
        }


    private void createTrainingAndValidationSets(double trainingEndTime, double testEndTime) {
        trainingSet = Lists.<AbstractInstance>newArrayList();
        validationSet = Lists.<AbstractInstance>newArrayList();
        for (InstanceWithTime instanceWithTime : allTrainingDataWithTimes) {
            if (instanceWithTime.time < trainingEndTime)
                trainingSet.add(instanceWithTime.instance);
            else if (instanceWithTime.time <= testEndTime)
                validationSet.add(instanceWithTime.instance);
            else
                break;
        }
    }

    private double getOptimalWeightingConstantForValidationWindow(CrossValLoss crossValLoss, double trainingEndTime, double validationEndTime) {

        double weightingConstant, currentLoss;
        do {
            weightingConstant = weightingConstantRecommender.get();
            createTrainingAndValidationSets(trainingEndTime, validationEndTime);
            reweightTrainingSet(weightingConstant);
            PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingSet);
            currentLoss =  crossValLoss.getLoss(validationSet, predictiveModel);
            validationEndTime += minutesInValidationSet;
            trainingEndTime += minutesInValidationSet;
        } while (weightingConstantRecommender.get())
        updateMovingAverage(weightingConstantRecommender.getBest());

        //what do we do with the weighting constant?  we use it in a running average (which might want to make a holt winters).
        // At training time, the value of the moving average is used to reweight the entire training set

        //In experiments however, the value of the moving average is used to get an error on a test set.  With the average error on
        //on each test set (for each moving avearge that is used) gives a total error.  This is repeated for different window sizes
        //moving average schemes (e.g. Holt winters)
    }

    public double getAveragedWeightingConstant(MovingAverage movingAverage, CrossValLoss crossValLoss, double minutesInValidaitonWindow, int numValidationWindows) {
        ArrayList<Double> weightingConstants = Lists.<Double>newArrayList();

        for (int i = 0; i< numValidationWindows; i++) {
            double trainingEndTime = timeIntervalOfData + i*minutesInValidaitonWindow;
            double validationEndTime = trainingEndTime + minutesInValidaitonWindow;
            double optimalWeightingConstantForWindow = getOptimalWeightingConstantForValidationWindow(crossValLoss, trainingEndTime, validationEndTime);
            weightingConstants.add(optimalWeightingConstantForWindow);
        }
        return movingAverage.getAverage(weightingConstants);

    }

    public void optimizeWindowSize() {

    }




        private double newWeight(double time, double reweightFactor) {
            return 1.0 - reweightFactor * time / trainingSetLifeTime;
        }


        class InstanceWithTime implements Comparable<InstanceWithTime>{
            public double time;
            public AbstractInstance instance;
            InstanceWithTime(double time, AbstractInstance instance) {
                this.time = time;
                this.instance = instance;
            }
            @Override
            public int compareTo(InstanceWithTime instanceWithTime){
                if(this.time > instanceWithTime.time)
                    return 1;
                else if (this.time == instanceWithTime.time)
                    return 0;
                return -1;
            }

        }

    }

