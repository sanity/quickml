package quickdt.predictiveModels.splitOnAttributePredictiveModel;

import quickdt.predictiveModelOptimizer.FieldValueRecommender;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.PredictiveModelWithDataBuilder;
import quickdt.predictiveModels.UpdatablePredictiveModelBuilderBuilderUtils;

import java.util.Map;

/**
 * Created by chrisreeves on 6/10/14.
 */
public class UpdatableSplitOnAttributePMBuilderBuilder implements PredictiveModelBuilderBuilder {
    private final SplitOnAttributePMBuilderBuilder splitOnAttributePMBuilderBuilder;

    public UpdatableSplitOnAttributePMBuilderBuilder(SplitOnAttributePMBuilderBuilder splitOnAttributePMBuilderBuilder) {
        this.splitOnAttributePMBuilderBuilder = splitOnAttributePMBuilderBuilder;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> map = splitOnAttributePMBuilderBuilder.createDefaultParametersToOptimize();
        UpdatablePredictiveModelBuilderBuilderUtils.addUpdatableParamters(map);
        return map;
    }

    @Override
    public PredictiveModelWithDataBuilder buildBuilder(Map predictiveModelConfig) {
        SplitOnAttributePMBuilder splitOnAttributePMBuilder = splitOnAttributePMBuilderBuilder.buildBuilder(predictiveModelConfig);
        PredictiveModelWithDataBuilder wrappedBuilder = new PredictiveModelWithDataBuilder(splitOnAttributePMBuilder);
        UpdatablePredictiveModelBuilderBuilderUtils.applyUpdatableConfig(wrappedBuilder, predictiveModelConfig);
        return wrappedBuilder;
    }
}
