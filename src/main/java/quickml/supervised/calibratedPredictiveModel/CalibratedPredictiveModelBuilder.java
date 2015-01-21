package quickml.supervised.calibratedPredictiveModel;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.dateTimeExtractors.DateTimeExtractor;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alexanderhawk on 3/10/14.
 * This class builds a calibrated predictive model, where the calibrator is implements the Pool Adjacent Violators algorithm.
 * It currently has some severe implementation problems and it's use is not recommended.
 */
public class CalibratedPredictiveModelBuilder implements PredictiveModelBuilder<AttributesMap, CalibratedPredictiveModel> {
    private static final Logger logger = LoggerFactory.getLogger(CalibratedPredictiveModelBuilder.class);
    private int binsInCalibrator = 25;
    private PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedPredictiveModelBuilder;
    boolean temporallyCalibrate = false;
    int hoursToCalibrateOver;
    int foldsForCalibrationSet = 6;
    private DateTimeExtractor<AttributesMap> dateTimeExtractor = new defaultDateTimeExtractor();
    Optional<? extends Classifier> wrappedPredictiveModel = Optional.absent();
    private List<PoolAdjacentViolatorsModel.Observation> storedObservations = Lists.newArrayList();
    Serializable positiveClassLabel = Double.valueOf(1.0);

    public CalibratedPredictiveModelBuilder() {
        this(new RandomForestBuilder());
    }

    public CalibratedPredictiveModelBuilder(PredictiveModelBuilder<AttributesMap, ? extends Classifier> predictiveModelBuilder) {
        this.wrappedPredictiveModelBuilder = predictiveModelBuilder;
    }

    public CalibratedPredictiveModelBuilder(Classifier wrappedPredictiveModel) {
        this.wrappedPredictiveModel = Optional.fromNullable(wrappedPredictiveModel);
    }

    public CalibratedPredictiveModelBuilder foldsForCalibrationSet(int foldsForCalibrationSet) {
        this.foldsForCalibrationSet = foldsForCalibrationSet;
        return this;
    }
    public CalibratedPredictiveModelBuilder positiveClassLabel(Serializable positiveClassLabel) {
        this.positiveClassLabel = positiveClassLabel;
        return this;
    }


    public CalibratedPredictiveModelBuilder binsInCalibrator(int binsInCalibrator) {
        this.binsInCalibrator = binsInCalibrator;
        return this;
    }

    public CalibratedPredictiveModelBuilder hoursToCalibrateOver(int hoursToCalibrateOver) {
        this.temporallyCalibrate = true;
        this.hoursToCalibrateOver = hoursToCalibrateOver;
        return this;
    }

    public CalibratedPredictiveModelBuilder dateTimeExtractor(DateTimeExtractor<AttributesMap> dateTimeExtractor) {
        this.dateTimeExtractor = dateTimeExtractor;
        return this;
    }

    @Override
    public CalibratedPredictiveModel buildPredictiveModel(Iterable<? extends Instance<AttributesMap>> trainingData) {
        Classifier predictiveModel;
        if (wrappedPredictiveModel.isPresent()) {
            predictiveModel = wrappedPredictiveModel.get();
        } else {
            predictiveModel = wrappedPredictiveModelBuilder.buildPredictiveModel(trainingData);
        }
        PoolAdjacentViolatorsModel calibrator;
        calibrator = createCalibrator(trainingData);

        logger.info("calibration set: " + calibrator.getCalibrationSet().toString());
        return new CalibratedPredictiveModel(predictiveModel, calibrator);
    }


    @Override
    public void setID(Serializable id) {
        wrappedPredictiveModelBuilder.setID(id);
    }

    private PoolAdjacentViolatorsModel createCalibrator(Iterable<? extends Instance<AttributesMap>> allInstancesIterable) {
        List<Instance<AttributesMap>> allInstances = Lists.newArrayList();
        for (Instance<AttributesMap> instance : allInstancesIterable) {
            allInstances.add(instance);
        }
        if (temporallyCalibrate)
            allInstances = sortInstances(allInstances);
        storedObservations = Lists.newArrayList();
        for (int fold = 1; fold <= foldsForCalibrationSet; fold++) {
            List<Instance<AttributesMap>> trainingInstances = Lists.newArrayList();
            List<Instance<AttributesMap>> calibrationInstances = Lists.newArrayList();
            createTrainingAndCalibrationSets(trainingInstances, calibrationInstances, allInstances, fold);
            Classifier predictiveModel = wrappedPredictiveModelBuilder.buildPredictiveModel(trainingInstances);
            List<PoolAdjacentViolatorsModel.Observation> foldObservations = getObservations(predictiveModel, calibrationInstances);
            storedObservations.addAll(foldObservations);
        }

        return new PoolAdjacentViolatorsModel(storedObservations, (int)Math.max(1, storedObservations.size() /(double) binsInCalibrator));
    }

    private void createTrainingAndCalibrationSets(List<Instance<AttributesMap>> trainingInstances, List<Instance<AttributesMap>> validationInstances, List<? extends Instance<AttributesMap>> allInstances, int fold) {
        DateTime lastInstance = dateTimeExtractor.extractDateTime(allInstances.get(0));
        if (temporallyCalibrate) {
            for (int i = 0; i < allInstances.size(); i++) {
                Instance<AttributesMap> instance = allInstances.get(i);
                DateTime dateTime = dateTimeExtractor.extractDateTime(instance);
                Period timeSinceLastInstance = new Period(dateTime, lastInstance);
                if (timeSinceLastInstance.getHours() < hoursToCalibrateOver) {
                    if (i % foldsForCalibrationSet == fold - 1) {
                        validationInstances.add(instance);
                    } else {
                        trainingInstances.add(instance);
                    }
                } else {
                    trainingInstances.add(instance);
                }
            }
        } else {
            for (int i = 0; i < allInstances.size(); i++) {
                Instance<AttributesMap> instance = allInstances.get(i);
                if (i % fold == 0) {
                    validationInstances.add(instance);
                } else {
                    trainingInstances.add(instance);
                }
            }
        }

    }

    protected List<PoolAdjacentViolatorsModel.Observation> getObservations(Classifier predictiveModel, Iterable<? extends Instance<AttributesMap>> trainingInstances) {
        List<PoolAdjacentViolatorsModel.Observation> mobservations = Lists.<PoolAdjacentViolatorsModel.Observation>newArrayList();
        double prediction = 0;
        double groundTruth = 0;
        PoolAdjacentViolatorsModel.Observation observation;
        for (Instance<AttributesMap> instance : trainingInstances) {
            try {
                groundTruth = getGroundTruth(instance.getLabel());
            } catch (RuntimeException r) {
                r.printStackTrace();
                System.exit(0);
            }
            prediction = predictiveModel.getProbability(instance.getAttributes(), positiveClassLabel);
            observation = new PoolAdjacentViolatorsModel.Observation(prediction, groundTruth, instance.getWeight());
            mobservations.add(observation);
        }
        return mobservations;
    }


    private double getGroundTruth(Serializable classification) {
        if (!(classification instanceof Double) && !(classification instanceof Integer)) {
            throw new RuntimeException("classification is not an instance of Integer or Double.  Classification value is " + classification);
        }
        return ((Number) (classification)).doubleValue();
    }

    //TODO: make the version of this in the out of time cross validator a utility method that can be shared between classes
    private List<Instance<AttributesMap>> sortInstances(List<Instance<AttributesMap>> instances) {

        Comparator<Instance<AttributesMap>> comparator = new Comparator<Instance<AttributesMap>>() {
            @Override
            public int compare(Instance<AttributesMap> o1, Instance<AttributesMap> o2) {
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

        Collections.sort(instances, comparator);
        return instances;
    }

    class defaultDateTimeExtractor implements DateTimeExtractor<AttributesMap> {
        @Override
        public DateTime extractDateTime(Instance<AttributesMap> instance) {
            AttributesMap attributes = instance.getAttributes();
            int year = (Integer) attributes.get("timeOfArrival-year");
            int month = (Integer) attributes.get("timeOfArrival-monthOfYear");
            int day = (Integer) attributes.get("timeOfArrival-dayOfMonth");
            int hour = (Integer) attributes.get("timeOfArrival-hourOfDay");
            int minute = (Integer) attributes.get("timeOfArrival-minuteOfHour");
            return new DateTime(year, month, day, hour, minute, 0, 0);
        }
    }
}
