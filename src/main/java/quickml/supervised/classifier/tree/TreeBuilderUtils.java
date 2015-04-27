package quickml.supervised.classifier.tree;

import quickml.supervised.classifier.tree.decisionTree.tree.Node;

/**
 * Created by alexanderhawk on 4/20/15.
 */
public class TreeBuilderUtils {

    public static Node getRoot(Node node) {
        while (node.parent!=null)
            node = node.parent;
        return node;
    }
}
