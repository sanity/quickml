package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.nodes.Branch;

import java.util.List;

/**
 * Created by alexanderhawk on 3/22/15.
 */
public interface LeafBuilder<GS extends GroupStatistics> {
    Leaf buildLeaf(Branch parent, GroupStatistics groupStatistics);
}
