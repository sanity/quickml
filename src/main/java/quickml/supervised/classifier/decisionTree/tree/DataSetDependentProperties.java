package quickml.supervised.classifier.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.*;

import java.util.List;
import java.util.Map;

import static quickml.supervised.classifier.decisionTree.tree.ForestOptions.*;

/**
 * Created by alexanderhawk on 3/20/15.
 */
public class DataSetDependentProperties<T extends InstanceWithAttributesMap> {
    ClassificationProperties classificationProperties;
    public AttributeValueIgnoringStrategy attributeValueIgnoringStrategy;

    public void initialize(List<? extends InstanceWithAttributesMap> instances, ForestConfigBuilder<T> config) {
        if (config.buildClassificationTrees && classificationProperties==null) {
            classificationProperties = ClassificationProperties.setDataProperties(instances);
        }
        setAttributeValueIgnoringStrategy(config);

    }

    private void setAttributeValueIgnoringStrategy(ForestConfigBuilder<T> config) {
        if (classificationProperties.classificationsAreBinary()) {
             attributeValueIgnoringStrategy =
                    new BinaryClassAttributeValueIgnoringStrategy(
                            (BinaryClassificationProperties)classificationProperties,
                             config.attributeValueObservationsThreshold);
        } else {
             attributeValueIgnoringStrategy = new MultiClassAtributeValueIgnoringStrategy(config.attributeValueObservationsThreshold);

        }
            this.attributeValueIgnoringStrategy = attributeValueIgnoringStrategy;
    }

    public void updateBuilderConfig(final Map<String, Object> cfg) {
        if (cfg.containsKey(ATTRIBUTE_VALUE_IGNORING_STRATEGY))
            attributeValueIgnoringStrategy= (AttributeValueIgnoringStrategy) cfg.get(ATTRIBUTE_VALUE_IGNORING_STRATEGY);

    }
}
