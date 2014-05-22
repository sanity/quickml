package quickdt.predictiveModels.calibratedPredictiveModel;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilder;

import java.util.List;


/**
 * Created by Chris on 5/14/2014.
 */
public class UpdatablePAVCalibratedPredictiveModelBuilder extends UpdatablePredictiveModelBuilder<CalibratedPredictiveModel> {
    private final PAVCalibratedPredictiveModelBuilder pavCalibratedPredictiveModelBuilder;

    public UpdatablePAVCalibratedPredictiveModelBuilder(PAVCalibratedPredictiveModelBuilder calibratedPredictiveModelBuilder) {
        this(calibratedPredictiveModelBuilder, null);
    }

    public UpdatablePAVCalibratedPredictiveModelBuilder(PAVCalibratedPredictiveModelBuilder calibratedPredictiveModelBuilder, CalibratedPredictiveModel calibratedPredictiveModel) {
        super(calibratedPredictiveModel);
        calibratedPredictiveModelBuilder.updatable(true);
        this.pavCalibratedPredictiveModelBuilder = calibratedPredictiveModelBuilder;
    }

    @Override
    public CalibratedPredictiveModel buildUpdatablePredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return pavCalibratedPredictiveModelBuilder.buildPredictiveModel(trainingData);
    }

    @Override
    public void updatePredictiveModel(CalibratedPredictiveModel predictiveModel, Iterable<? extends AbstractInstance> newData, List<? extends AbstractInstance> trainingData, boolean splitNodes) {
        pavCalibratedPredictiveModelBuilder.updatePredictiveModel(predictiveModel, newData, trainingData, splitNodes);
    }

    @Override
    public void stripData(CalibratedPredictiveModel predictiveModel) {
        pavCalibratedPredictiveModelBuilder.stripData(predictiveModel);
    }

}
