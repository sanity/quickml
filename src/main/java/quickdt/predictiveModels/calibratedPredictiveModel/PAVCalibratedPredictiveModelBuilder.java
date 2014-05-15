package quickdt.predictiveModels.calibratedPredictiveModel;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 3/10/14.
 * This class builds a calibrated predictive model, where the calibrator is implements the Pool Adjacent Violators algorithm.
 */
public class PAVCalibratedPredictiveModelBuilder implements PredictiveModelBuilder<CalibratedPredictiveModel> {
    private int binsInCalibrator = 5;
    private PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder;

    public PAVCalibratedPredictiveModelBuilder(PredictiveModelBuilder<? extends PredictiveModel> predictiveModelBuilder) {
        this.predictiveModelBuilder = predictiveModelBuilder;
    }

    public PAVCalibratedPredictiveModelBuilder() {
        this.predictiveModelBuilder = new RandomForestBuilder();
    }

    public PAVCalibratedPredictiveModelBuilder binsInCalibrator(Integer binsInCalibrator) {
        if (binsInCalibrator!=null) {
            this.binsInCalibrator = binsInCalibrator;
        }
        return this;
    }

    public PAVCalibratedPredictiveModelBuilder updatable(boolean updatable) {
        if (predictiveModelBuilder instanceof RandomForestBuilder) {
            RandomForestBuilder randomForestBuilder = (RandomForestBuilder) predictiveModelBuilder;
            randomForestBuilder.updatable(updatable);
        } else if (predictiveModelBuilder instanceof TreeBuilder) {
            TreeBuilder treeBuilder = (TreeBuilder) predictiveModelBuilder;
            treeBuilder.updatable(updatable);
        }
        return this;
    }

    @Override
    public CalibratedPredictiveModel buildPredictiveModel(Iterable <? extends AbstractInstance> trainingInstances) {
        PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingInstances);
        Calibrator calibrator = createCalibrator(predictiveModel, trainingInstances);
        return new CalibratedPredictiveModel(predictiveModel, calibrator);
    }

    public void updatePredictiveModel(PredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData) {
        CalibratedPredictiveModel calibratedPredictiveModel = (CalibratedPredictiveModel) predictiveModel;
        updateCalibrator(calibratedPredictiveModel, newData);
        if (predictiveModelBuilder instanceof RandomForestBuilder) {
            RandomForestBuilder randomForestBuilder = (RandomForestBuilder) predictiveModelBuilder;
            randomForestBuilder.updatePredictiveModel((RandomForest)calibratedPredictiveModel.predictiveModel, newData, trainingData);
        } else if (predictiveModelBuilder instanceof TreeBuilder) {
            TreeBuilder treeBuilder = (TreeBuilder) predictiveModelBuilder;
            treeBuilder.updatePredictiveModel((Tree)calibratedPredictiveModel.predictiveModel, newData, trainingData);
        }
    }

    private Calibrator createCalibrator(PredictiveModel predictiveModel, Iterable<? extends AbstractInstance> trainingInstances) {
        List<PAVCalibrator.Observation> mobservations = getObservations(predictiveModel, trainingInstances);
        return new PAVCalibrator(mobservations, Math.max(1, Iterables.size(trainingInstances)/binsInCalibrator));
    }

    private void updateCalibrator(CalibratedPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> trainingInstances) {
        List<PAVCalibrator.Observation> mobservations = getObservations(predictiveModel, trainingInstances);

        PAVCalibrator calibrator = (PAVCalibrator)predictiveModel.calibrator;
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