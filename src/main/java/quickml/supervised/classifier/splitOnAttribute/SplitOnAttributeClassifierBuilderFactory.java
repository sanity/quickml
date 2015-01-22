package quickml.supervised.classifier.splitOnAttribute;

import com.google.common.collect.Maps;

import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.predictiveModelOptimizer.FieldValueRecommender;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.PredictiveModelBuilderFactory;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by chrisreeves on 6/10/14.
 */
public class SplitOnAttributeClassifierBuilderFactory implements PredictiveModelBuilderFactory<AttributesMap,SplitOnAttributeClassifier, SplitOnAttributeClassifierBuilder> {
    private final String attributeKey;
    private final PredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends PredictiveModelBuilder<AttributesMap, ? extends Classifier>>  wrappedBuilderFactory;
    private Map<Integer, SplitOnAttributeClassifierBuilder.SplitModelGroup> splitModelGroups;

    private final Integer defaultGroup;

    //TODO:  this method should not have any parameters.
    public SplitOnAttributeClassifierBuilderFactory(String attributeKey, Map<Integer, SplitOnAttributeClassifierBuilder.SplitModelGroup> splitModelGroups, Integer defaultGroup, final PredictiveModelBuilderFactory<AttributesMap, ? extends Classifier,? extends PredictiveModelBuilder<AttributesMap, ? extends Classifier>>  wrappedBuilder) {

        this.attributeKey = attributeKey;
        this.defaultGroup = defaultGroup;
        this.splitModelGroups = splitModelGroups;
        this.wrappedBuilderFactory = wrappedBuilder;

    }

    @Override
    public Map<String, FieldValueRecommender> createDefaultParametersToOptimize() {
        Map<String, FieldValueRecommender> parametersToOptimize = Maps.newHashMap();
        return parametersToOptimize;
    }

    @Override
    public SplitOnAttributeClassifierBuilder buildBuilder(final Map<String, Object> predictiveModelConfig) {
        return new SplitOnAttributeClassifierBuilder(attributeKey, splitModelGroups, defaultGroup,  wrappedBuilderFactory.buildBuilder(predictiveModelConfig));
    }
}
