package quickdt.predictiveModels.calibratedPredictiveModel;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;


/**
 * Created by Chris on 5/14/2014.
 */
public class UpdatablePAVCalibratedPredictiveModelBuilder extends UpdatablePredictiveModelBuilder<CalibratedPredictiveModel> {
    private final PAVCalibratedPredictiveModelBuilder pavCalibratedPredictiveModelBuilder;

    public UpdatablePAVCalibratedPredictiveModelBuilder(PAVCalibratedPredictiveModelBuilder randomForestBuilder) {
        this(randomForestBuilder, null);
    }

    public UpdatablePAVCalibratedPredictiveModelBuilder(PAVCalibratedPredictiveModelBuilder randomForestBuilder, Integer rebuildThreshold) {
        super(rebuildThreshold);
        this.pavCalibratedPredictiveModelBuilder = randomForestBuilder;
    }

    @Override
    public CalibratedPredictiveModel buildUpdatablePredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return pavCalibratedPredictiveModelBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(CalibratedPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> trainingData) {
        pavCalibratedPredictiveModelBuilder.updatePredictiveModel(predictiveModel, trainingData);
    }

}
