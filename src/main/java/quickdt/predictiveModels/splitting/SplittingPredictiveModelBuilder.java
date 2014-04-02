package quickdt.predictiveModels.splitting;

import quickdt.data.AbstractInstance;
import quickdt.predictiveModels.PredictiveModelBuilder;

/**
 * Created by ian on 4/1/14.
 */
public class SplittingPredictiveModelBuilder implements PredictiveModelBuilder<SplittingPredictiveModel> {
    private final String attributeToSplitOn;

    public SplittingPredictiveModelBuilder(String attributeToSplitOn) {

        this.attributeToSplitOn = attributeToSplitOn;
    }

    @Override
    public SplittingPredictiveModel buildPredictiveModel(final Iterable<? extends AbstractInstance> trainingData) {
        return null;
    }
}
