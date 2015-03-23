package quickml.supervised.classifier.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;

import java.util.List;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface LeafBuilder<T extends InstanceWithAttributesMap> {
    Leaf buildLeaf(List<T> instances);
}
