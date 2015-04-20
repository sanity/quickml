package quickml.supervised.classifier.tree.decisionTree.tree;

import quickml.supervised.classifier.DataProperties;
import quickml.supervised.classifier.tree.Tree;

/**
 * Created by alexanderhawk on 4/4/15.
 */
//how can we get the set of classifications?
public interface TreeFactory<Tr extends Tree> {
    Tr constructTree(Node head);
}
