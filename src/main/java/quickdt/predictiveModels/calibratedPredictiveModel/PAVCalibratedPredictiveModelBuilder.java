package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.collect.Lists;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;
import com.google.common.collect.Iterables;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 3/10/14.
 * This class builds a calibrated predictive model, where the calibrator is implements the Pool Adjacent Violators algorithm.
 */
public class PAVCalibratedPredictiveModelBuilder implements PredictiveModelBuilder<CalibratedPredictiveModel> {
    private int binsInCalibrator = 5;
    private PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder;
    public PAVCalibrator calibrator;
    public PredictiveModel predictiveModel;

    public PAVCalibratedPredictiveModelBuilder(PredictiveModelBuilder predictiveModelBuilder) {
        this.predictiveModelBuilder = predictiveModelBuilder;
    }

    public PAVCalibratedPredictiveModelBuilder() {
        this.predictiveModelBuilder = new RandomForestBuilder();
    }

    public PAVCalibratedPredictiveModelBuilder binsInCalibrator(Integer binsInCalibrator) {
        if (binsInCalibrator!=null)
            this.binsInCalibrator = binsInCalibrator;
        return this;
    }

    @Override
    public CalibratedPredictiveModel buildPredictiveModel(Iterable <? extends AbstractInstance> trainingInstances) {
        predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingInstances);
        createCalibrator(trainingInstances);
        return new CalibratedPredictiveModel(predictiveModel, calibrator);
    }

    private void createCalibrator(Iterable<? extends AbstractInstance> trainingInstances) {
            List mobservations = Lists.<PAVCalibrator.Observation>newArrayList();
            double prediction = 0;
            double groundTruth = 0;
            Instance currentInstance;
            PAVCalibrator.Observation observation;
            for(AbstractInstance instance : trainingInstances)  {
                try {
                    groundTruth = getGroundTruth(instance.getClassification());
                }
                catch (RuntimeException r){
                    r.printStackTrace();
                    System.exit(0);
                }
                prediction = predictiveModel.getProbability(instance.getAttributes(), 1.0);
                observation = new PAVCalibrator.Observation(prediction, groundTruth, instance.getWeight());
                mobservations.add(observation);
            }
            this.calibrator = new PAVCalibrator(mobservations, Math.max(1, Iterables.size(trainingInstances)/binsInCalibrator));
    }

    private double getGroundTruth(Serializable classification) {
        if (!(classification instanceof Double) && !(classification instanceof Integer))
            throw new RuntimeException("classification is not an instance of Integer or Double.  Classification value is " + classification);
        return ((Number)(classification)).doubleValue();
    }
}