package quickdt;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tree implements PredictiveModel {
    public final Node node;

    protected Tree(Node tree) {
        this.node = tree;
    }

    @Override
    public double getProbability(Attributes attributes, Serializable classification) {
        Leaf leaf = node.getLeaf(attributes);
        return leaf.getProbability(classification);
    }

    @Override
    public void dump(PrintStream printStream) {
        node.dump(printStream);
    }

    @Override
    public Serializable getClassificationByMaxProb(Attributes attributes) {
        Leaf leaf = node.getLeaf(attributes);
        return leaf.getBestClassification();
    }
}
