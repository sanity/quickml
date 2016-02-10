package quickml.supervised.tree.regressionTree;

import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.PrintStream;

/**
 * Created by ian on 7/20/15.
 */
public class RegressionTreeVisualizer {

    public static final int INDENT_AMOUNT = 3;

    public void visualize(RegressionTree tree, PrintStream out) {
        visualize(tree.root, out, 0);
    }

    private void visualize(final Node<MeanValueCounter> node, final PrintStream out, final int depth) {
        StringBuilder indentBuilder = new StringBuilder();
        for (int x = 0; x < depth; x++) {
            indentBuilder.append(' ');
        }
        String indent = indentBuilder.toString();

        if (node instanceof Branch) {
            Branch<MeanValueCounter> branch = (Branch<MeanValueCounter>) node;
            out.println(indent + branch.toString() + " TRUE:");
            visualize(branch.getTrueChild(), out, depth + INDENT_AMOUNT);
            out.println(indent + branch.toString() + " FALSE:");
            visualize(branch.getFalseChild(), out, depth + INDENT_AMOUNT);
        } else if (node instanceof Leaf) {
            out.println(indent + "LEAF: " + node.toString());
        }
    }

}
