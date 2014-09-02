package quickml.visualization;

import org.apache.commons.lang3.StringUtils;
import quickml.data.AttributesMap;
import quickml.supervised.classifier.decisionTree.tree.CategoricalBranch;
import quickml.supervised.classifier.decisionTree.tree.Leaf;
import quickml.supervised.classifier.decisionTree.tree.Node;
import quickml.supervised.classifier.decisionTree.tree.NumericBranch;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Set;

/**
 * Node used to visualize a predictive model
 */
public class InstrumentedNode {

    protected String type;
    protected String name;
    public boolean onPath;
    public InstrumentedNode[] children = new InstrumentedNode[2];


    public static DecimalFormat df;
    public static final Double EVENT = Double.valueOf(1);
    public static final String MISSING_VALUE = "NO_VALUE";

    static {
        df = new DecimalFormat("#");
        df.setMaximumFractionDigits(8);
    }

    public static InstrumentedNode create(Node node, AttributesMap attributes, boolean onPath) {
        if (node instanceof CategoricalBranch) {
            return new CategoryNode((CategoricalBranch) node, attributes, onPath);
        } else if (node instanceof NumericBranch) {
            return new NumericNode((NumericBranch) node, attributes, onPath);
        } else if (node instanceof Leaf) {
            return new LeafNode((Leaf) node, onPath);
        }

        throw new RuntimeException("Bad Node Type");
    }

    public InstrumentedNode(String type, String name, boolean onPath) {
        this.type = type;
        this.name = name;
        this.onPath = onPath;
    }

    public static class CategoryNode extends InstrumentedNode {

        protected String inset;
        protected String value;

        public CategoryNode(CategoricalBranch node, AttributesMap attributes, boolean onPath) {
            super("category", node.attribute, onPath);
            this.inset = summarizeInset(node.inSet);
            this.value = attributes.get(node.attribute) == null ? MISSING_VALUE : attributes.get(node.attribute).toString();
        }
    }

    public static class NumericNode extends InstrumentedNode {
        protected String value;
        protected double threshold;

        public NumericNode(NumericBranch node, AttributesMap attributes, boolean onPath) {
            super("numeric", node.attribute, onPath);
            this.threshold = node.threshold;
            this.value = attributes.get(node.attribute) == null ? MISSING_VALUE : attributes.get(node.attribute).toString();
        }
    }

    public static class LeafNode extends InstrumentedNode {

        private double samples;
        private String probability;

        public LeafNode(Leaf node, boolean onPath) {
            super("leaf", null, onPath);
            this.samples = node.classificationCounts.getTotal();
            probability = df.format(node.classificationCounts.getCount(EVENT) / samples);
            this.children = null;
        }

    }

    private static String summarizeInset(Set<Serializable> inSet) {
        int counter = 1;
        ArrayList<String> subset = new ArrayList<>();
        for (Serializable serializable : inSet) {
            subset.add(serializable.toString());
            if (counter++ > 5) {
                subset.add(".........." + (inSet.size() - 5) + " more ");
                break;
            }
        }
        return StringUtils.join(subset);
    }

}
