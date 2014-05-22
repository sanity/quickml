package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.wrappedPredictiveModel.WrappedPredictiveModel;
import quickdt.predictiveModels.wrappedPredictiveModel.WrappedPredictiveModelBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chrisreeves on 5/22/14.
 */
public class PAVCalibratedPredictiveModelBuilder extends WrappedPredictiveModelBuilder {
    private int binsInCalibrator = 5;

    public PAVCalibratedPredictiveModelBuilder(PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder) {
        super(predictiveModelBuilder);
    }

    public PAVCalibratedPredictiveModelBuilder binsInCalibrator(Integer binsInCalibrator) {
        if (binsInCalibrator!=null) {
            this.binsInCalibrator = binsInCalibrator;
        }
        return this;
    }

    @Override
    public CalibratedPredictiveModel buildPredictiveModel(Iterable<? extends AbstractInstance> trainingData) {
        PredictiveModel predictiveModel = wrappedPredictiveModelBuilder.buildPredictiveModel(trainingData);
        Calibrator calibrator = createCalibrator(predictiveModel, trainingData);
        return new CalibratedPredictiveModel(predictiveModel, calibrator);
    }

    @Override
    public void updatePredictiveModel(WrappedPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        updateCalibrator(predictiveModel, newData);
        super.updatePredictiveModel(predictiveModel, newData, trainingData, splitNodes);
    }

    private Calibrator createCalibrator(PredictiveModel predictiveModel, Iterable<? extends AbstractInstance> trainingInstances) {
        List<PAVCalibrator.Observation> mobservations = getObservations(predictiveModel, trainingInstances);
        return new PAVCalibrator(mobservations, Math.max(1, Iterables.size(trainingInstances)/binsInCalibrator));
    }

    private void updateCalibrator(PredictiveModel predictiveModel, Iterable<? extends AbstractInstance> trainingInstances) {
        List<PAVCalibrator.Observation> mobservations = getObservations(predictiveModel, trainingInstances);

        PAVCalibrator calibrator = (PAVCalibrator)((CalibratedPredictiveModel)predictiveModel).calibrator;
        for(PAVCalibrator.Observation observation : mobservations) {
            calibrator.addObservation(observation);
        }
    }

    private List<PAVCalibrator.Observation> getObservations(PredictiveModel predictiveModel, Iterable<? extends AbstractInstance> trainingInstances) {
        List<PAVCalibrator.Observation> mobservations = Lists.<PAVCalibrator.Observation>newArrayList();
        double prediction = 0;
        double groundTruth = 0;
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
        return mobservations;
    }

    private double getGroundTruth(Serializable classification) {
        if (!(classification instanceof Double) && !(classification instanceof Integer)) {
            throw new RuntimeException("classification is not an instance of Integer or Double.  Classification value is " + classification);
        }
        return ((Number)(classification)).doubleValue();
    }
}
