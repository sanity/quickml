package quickml.supervised.classifier.twoStageModel;

import com.google.common.collect.Maps;
import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.PredictiveModelBuilderFactory;
import quickml.supervised.UpdatablePredictiveModelBuilder;
import quickml.supervised.UpdatablePredictiveModelBuilderFactory;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;

import java.util.Map;

/**
 * Created by alexanderhawk on 10/28/14.
 */
public class TwoStageBuilderFactory implements UpdatablePredictiveModelBuilderFactory <AttributesMap, TwoStageModel, TwoStageModelBuilder> {

    private final PredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends PredictiveModelBuilder<AttributesMap, ? extends Classifier>> wrappedBuilderFactory1;
    private final PredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends PredictiveModelBuilder<AttributesMap, ? extends Classifier>> wrappedBuilderFactory2;


    public TwoStageBuilderFactory(PredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends PredictiveModelBuilder<AttributesMap, ? extends Classifier>> wrappedBuilderBuilder1,
                                                PredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends PredictiveModelBuilder<AttributesMap, ? extends Classifier>> wrappedBuilderBuilder2
                                                ) {
        this.wrappedBuilderFactory1 = wrappedBuilderBuilder1;
        this.wrappedBuilderFactory2 = wrappedBuilderBuilder2;
    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        parametersToOptimize.putAll(wrappedBuilderFactory1.createDefaultParametersToOptimize());
        parametersToOptimize.putAll(wrappedBuilderFactory2.createDefaultParametersToOptimize());
        return parametersToOptimize;
    }

    @Override
    public TwoStageModelBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new TwoStageModelBuilder(wrappedBuilderFactory1.buildBuilder(predictiveModelConfig), wrappedBuilderFactory2.buildBuilder(predictiveModelConfig));
    }
}
