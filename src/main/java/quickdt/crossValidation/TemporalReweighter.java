package quickdt.crossValidation;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import quickdt.data.AbstractInstance;

import java.util.Collections;
import java.util.List;

/**
 * Created by alexanderhawk on 4/17/14.
 */
public class TemporalReweighter {

        private Iterable<? extends AbstractInstance> trainingData;
        private double crossValidationFraction;
        private double testFraction;
        private CrossValLoss<?> lossObject;
        private double trainingSetLifeTime;
        private static final DateTime timeSince2012 = new DateTime(2013,1,1,0,0,0,0);
        private List<InstanceWithTime> allTrainingDataWithTimes = Lists.<InstanceWithTime>newArrayList();
        private List<InstanceWithTime> trainingSet = Lists.<InstanceWithTime>newArrayList();
        private List<InstanceWithTime> validationSet = Lists.<InstanceWithTime>newArrayList();
        private List<InstanceWithTime> testSet = Lists.<InstanceWithTime>newArrayList();

        private double timeIntervalOfData;

        public TemporalReweighter(Iterable<? extends AbstractInstance> trainingData, CrossValLoss<?> lossObject, double crossValidationFraction, double testFraction){
            this.trainingData = trainingData;
            this.crossValidationFraction = crossValidationFraction;
            this.testFraction = testFraction;
            this.lossObject = lossObject;
            createTimedInstances();
            createTrainingValidationAndTestSets();
        }

        private void createTimedInstances() {
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
            this.timeIntervalOfData = endTime - startTime;

            for (InstanceWithTime instanceWithTime : allTrainingDataWithTimes)
                instanceWithTime.time = instanceWithTime.time - startTime;

            Collections.sort(allTrainingDataWithTimes);
        }

        private double getArrivalTimeInMinutes(AbstractInstance instance) {
            int year = (Integer)instance.getAttributes().get("timeOfArrival-year");
            int month = (Integer)instance.getAttributes().get("timeOfArrival-monthOfYear");
            int day = (Integer)instance.getAttributes().get("timeOfArrival-dayOfMonth");
            DateTime dateTime = new DateTime(year, month, day, 0, 0,0,0);
            return Minutes.minutesBetween(dateTime, timeSince2012).getMinutes();
        }

        private void createTrainingValidationAndTestSets() {
            int trainingSetSize = (int)((1.0 - testFraction - crossValidationFraction)* allTrainingDataWithTimes.size());
            int validationSetSize = (int)(crossValidationFraction*allTrainingDataWithTimes.size());

            for (int i = 0; i< allTrainingDataWithTimes.size(); i++) {
                if(i < trainingSetSize )
                    trainingSet.add(allTrainingDataWithTimes.get(i));
                else if(i < trainingSetSize + validationSetSize)
                    validationSet.add(allTrainingDataWithTimes.get(i));
                else
                    testSet.add(allTrainingDataWithTimes.get(i));
            }
        }

        public Iterable<? extends AbstractInstance> verifyReweightingOfTrainingData() { // create a similar method that reweights just the test data.
              double reWeightFactor = 0;
              List<Double> reweigtingFactors = getReweightingFactorsForVariousRegularizationConstants();
              reWeightFactor = getBestCrossValidatedReweightingFactor(reweigtingFactors);
              List<? extends AbstractInstance> reweightedInstances = reweightEachInstance(); //reweights everything in training the trainingAndValidationSet
              verifyNoOverfit(reweightedInstances);
        }

        private  List<Double> getReweightingFactorsForVariousRegularizationConstants() {
        List<Double> reweigtingFactors = Lists.<Double>newArrayList();
        double regularizationConstants[] = {.001, .003, .01, .03, .06, .1, .3};
        for (double regularizationConstant : regularizationConstants) {
               reweigtingFactors.add(getBestReweightFactor(regularizationConstant));
        }
        return reweigtingFactors;
        }

        private getBestReweightFactor

        private double newWeight(double time, double reweightFactor) {
            return 1.0 - reweightFactor * time / trainingSetLifeTime;
        }

        public void verifyNoOverfit(){}

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

