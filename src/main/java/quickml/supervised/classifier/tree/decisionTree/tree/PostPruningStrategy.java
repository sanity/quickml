package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.Tree;

import java.util.List;

/**
 * Created by alexanderhawk on 4/4/15.
 */
public interface PostPruningStrategy<T extends InstanceWithAttributesMap> {
    Tree prune(Tree tree, List<T> holdOutData);
}
