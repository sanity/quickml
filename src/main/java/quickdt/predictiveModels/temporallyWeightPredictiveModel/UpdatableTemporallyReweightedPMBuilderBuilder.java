package quickdt.predictiveModels.temporallyWeightPredictiveModel;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.PredictiveModelWithDataBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilderUtils;
import quickdt.predictiveModels.splitOnAttributePredictiveModel.SplitOnAttributePMBuilder;
import quickdt.predictiveModels.splitOnAttributePredictiveModel.SplitOnAttributePMBuilderBuilder;

import java.util.Map;


public class UpdatableTemporallyReweightedPMBuilderBuilder implements PredictiveModelBuilderBuilder {
    private final TemporallyReweightedPMBuilderBuilder temporallyReweightedPMBuilderBuilder;

    public UpdatableTemporallyReweightedPMBuilderBuilder(TemporallyReweightedPMBuilderBuilder temporallyReweightedPMBuilderBuilder) {
        this.temporallyReweightedPMBuilderBuilder = temporallyReweightedPMBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> map = temporallyReweightedPMBuilderBuilder.createDefaultParametersToOptimize();
        UpdatablePredictiveModelBuilderBuilderUtils.addUpdatableParamters(map);
        return map;
    }

    @Override
    public PredictiveModelWithDataBuilder buildBuilder(Map predictiveModelConfig) {
        TemporallyReweightedPMBuilder temporallyReweightedPMBuilder = temporallyReweightedPMBuilderBuilder.buildBuilder(predictiveModelConfig);
        PredictiveModelWithDataBuilder wrappedBuilder = new PredictiveModelWithDataBuilder(temporallyReweightedPMBuilder);
        UpdatablePredictiveModelBuilderBuilderUtils.applyUpdatableConfig(wrappedBuilder, predictiveModelConfig);
        return wrappedBuilder;
    }
}
