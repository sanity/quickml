package quickml.visualization;


import quickml.data.AttributesMap;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.tree.Branch;
import quickml.supervised.classifier.decisionTree.tree.Leaf;
import quickml.supervised.classifier.decisionTree.tree.Node;

public class ModelWalker {


    public static InstrumentedNode walk(Tree tree, AttributesMap attributes) {
        InstrumentedNode instrumentedNode = InstrumentedNode.create(tree.node, attributes, true);
        walk(tree.node, instrumentedNode, attributes);
        return instrumentedNode;
    }

    public static InstrumentedNode walkChosenPath(Tree tree, AttributesMap attributes) {
        InstrumentedNode instrumentedNode = InstrumentedNode.create(tree.node, attributes, true);
        walkChosenPath(tree.node, instrumentedNode, attributes);
        return instrumentedNode;
    }

    public static void walk(Node node, InstrumentedNode instrumentedNode, AttributesMap attributes) {
        if (!(node instanceof Leaf)) {
            Branch branch = (Branch) node;
            instrumentedNode.children[0] = InstrumentedNode.create(branch.falseChild, attributes, instrumentedNode.onPath && !branch.decide(attributes));
            instrumentedNode.children[1] = InstrumentedNode.create(branch.trueChild, attributes, instrumentedNode.onPath && branch.decide(attributes));
            walk(branch.falseChild, instrumentedNode.children[0], attributes);
            walk(branch.trueChild, instrumentedNode.children[1], attributes);
        }
    }

    public static void walkChosenPath(Node node, InstrumentedNode instrumentedNode, AttributesMap attributes) {
        if (!(node instanceof Leaf)) {
            Branch branch = (Branch) node;
            instrumentedNode.children[0] = InstrumentedNode.create(branch.falseChild, attributes, instrumentedNode.onPath && !branch.decide(attributes));
            instrumentedNode.children[1] = InstrumentedNode.create(branch.trueChild, attributes, instrumentedNode.onPath && branch.decide(attributes));
            if (!branch.decide(attributes)) {
                walkChosenPath(branch.falseChild, instrumentedNode.children[0], attributes);
                instrumentedNode.children[1].children = null;
            } else {
                walkChosenPath(branch.trueChild, instrumentedNode.children[1], attributes);
                instrumentedNode.children[0].children = null;
            }
        }
    }


}
