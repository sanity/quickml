package quickml.supervised.classifier.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;

import java.util.List;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface DataPropertiesTransformer<T extends InstanceWithAttributesMap> {
    ForestConfig<T> createForestConfig(List<T> instances, ForestConfigBuilder configBuilder);
    DataPropertiesTransformer<T> copy();
}
