package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.DataProperties;

import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface DataPropertiesTransformer<T extends InstanceWithAttributesMap, S extends SplitProperties, D extends DataProperties> {
    ForestConfig<T, S, D> createForestConfig(List<T> instances, ForestConfigBuilder<T,S, D> configBuilder);
    DataPropertiesTransformer<T, S, D> copy();
}
