package quickml.supervised.calibratedPredictiveModel;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.UpdatablePredictiveModelBuilder;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.regressionModel.IsotonicRegression.PoolAdjacentViolatorsModel;

import java.io.Serializable;
import java.util.List;
import java.util.TreeSet;

/**
 * Created by alexanderhawk on 3/10/14.
 * This class builds a calibrated predictive model, where the calibrator is implements the Pool Adjacent Violators algorithm.
 * It currently has some severe implementation problems and it's use is not recommended.
 */
public class PAVCalibratedPredictiveModelBuilder implements UpdatablePredictiveModelBuilder<AttributesMap, CalibratedPredictiveModel> {
    private int binsInCalibrator = 5;
    private PredictiveModelBuilder<AttributesMap, ? extends Classifier> wrappedPredictiveModelBuilder;

    public PAVCalibratedPredictiveModelBuilder() {
        this(new RandomForestBuilder());
    }

    public PAVCalibratedPredictiveModelBuilder(PredictiveModelBuilder<AttributesMap, ? extends Classifier> predictiveModelBuilder) {
        this.wrappedPredictiveModelBuilder = predictiveModelBuilder;
    }

    public PAVCalibratedPredictiveModelBuilder binsInCalibrator(Integer binsInCalibrator) {
        if (binsInCalibrator!=null) {
            this.binsInCalibrator = binsInCalibrator;
        }
        return this;
    }

    @Override
    public CalibratedPredictiveModel buildPredictiveModel(Iterable<? extends Instance<AttributesMap>> trainingData) {
        Classifier predictiveModel = wrappedPredictiveModelBuilder.buildPredictiveModel(trainingData);
        PoolAdjacentViolatorsModel calibrator = createCalibrator(predictiveModel, trainingData);
        return new CalibratedPredictiveModel(predictiveModel, calibrator);
    }

    @Override
    public PredictiveModelBuilder<AttributesMap, CalibratedPredictiveModel> updatable(boolean updatable) {
        wrappedPredictiveModelBuilder.updatable(updatable);
        return this;
    }

    @Override
    public void updatePredictiveModel(CalibratedPredictiveModel predictiveModel, Iterable<? extends Instance<AttributesMap>> newData, boolean splitNodes) {
        if (wrappedPredictiveModelBuilder instanceof UpdatablePredictiveModelBuilder) {
            predictiveModel.pavFunction= updateCalibrator(predictiveModel, newData);
            ((UpdatablePredictiveModelBuilder) wrappedPredictiveModelBuilder).updatePredictiveModel(predictiveModel, newData, splitNodes);


        } else {
            throw new RuntimeException("Cannot update predictive model without UpdatablePredictiveModelBuilder");
        }
    }

    @Override
    public void stripData(CalibratedPredictiveModel predictiveModel) {
        if (wrappedPredictiveModelBuilder instanceof UpdatablePredictiveModelBuilder) {
            ((UpdatablePredictiveModelBuilder) wrappedPredictiveModelBuilder).stripData(predictiveModel);
        } else {
            throw new RuntimeException("Cannot strip data without UpdatablePredictiveModelBuilder");
        }
    }

    @Override
    public void setID(Serializable id) {
        wrappedPredictiveModelBuilder.setID(id);
    }

    private PoolAdjacentViolatorsModel updateCalibrator(CalibratedPredictiveModel predictiveModel, Iterable<? extends Instance<AttributesMap>> newData) {
        TreeSet<PoolAdjacentViolatorsModel.Observation> observations = predictiveModel.pavFunction.getCalibrationSet();
        List<PoolAdjacentViolatorsModel.Observation> mobservations = getObservations(predictiveModel, newData);
        observations.addAll(mobservations);
        return new PoolAdjacentViolatorsModel(observations, Math.max(1, observations.size()/binsInCalibrator));
        }

    private PoolAdjacentViolatorsModel createCalibrator(Classifier predictiveModel, Iterable<? extends Instance<AttributesMap>> trainingInstances) {
        List<PoolAdjacentViolatorsModel.Observation> mobservations = getObservations(predictiveModel, trainingInstances);
        return new PoolAdjacentViolatorsModel(mobservations, Math.max(1, Iterables.size(trainingInstances)/binsInCalibrator));
    }

    protected List<PoolAdjacentViolatorsModel.Observation> getObservations(Classifier predictiveModel, Iterable<? extends Instance<AttributesMap>> trainingInstances) {
        List<PoolAdjacentViolatorsModel.Observation> mobservations = Lists.<PoolAdjacentViolatorsModel.Observation>newArrayList();
        double prediction = 0;
        double groundTruth = 0;
        PoolAdjacentViolatorsModel.Observation observation;
        for(Instance<AttributesMap> instance : trainingInstances)  {
            try {
                groundTruth = getGroundTruth(instance.getLabel());
            }
            catch (RuntimeException r){
                r.printStackTrace();
                System.exit(0);
            }
            // TODO: We can't assume that the classification will be 1.0
            prediction = predictiveModel.getProbability(instance.getAttributes(), 1.0);
            observation = new PoolAdjacentViolatorsModel.Observation(prediction, groundTruth, instance.getWeight());
            mobservations.add(observation);
        }
        return mobservations;
    }

    private double getGroundTruth(Serializable classification) {
        if (!(classification instanceof Double) && !(classification instanceof Integer)) {
            throw new RuntimeException("classification is not an instance of Integer or Double.  Classification value is " + classification);
        }
        return ((Number)(classification)).doubleValue();
    }
}
