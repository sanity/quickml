package quickml.supervised.classifier.decisionTree;

import com.google.common.collect.Maps;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.decisionTree.tree.Leaf;
import quickml.supervised.classifier.decisionTree.tree.Node;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: janie
 * Date: 6/26/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tree extends AbstractClassifier {
    static final long serialVersionUID = 56394564395635672L;
    public final Node root;
    private Set<Serializable> classifications = new HashSet<>();

    protected Tree(Node root, Set<Serializable> classifications) {
        this.root = root;
        this.classifications = classifications;
    }

    public Set<Serializable> getClassifications() {
        return classifications;
    }

    @Override
    public double getProbability(AttributesMap attributes, Serializable classification) {
        Leaf leaf = root.getLeaf(attributes);
        return leaf.getProbability(classification);
    }

    @Override
    public double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore) {
        return root.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
    }


    @Override
    public PredictionMap predict(AttributesMap attributes) {
        Leaf leaf = root.getLeaf(attributes);
        Map<Serializable, Double> probsByClassification = Maps.newHashMap();
        for (Serializable classification : leaf.getClassifications()) {
            probsByClassification.put(classification, leaf.getProbability(classification));
        }
        return new PredictionMap(probsByClassification);
    }

    @Override
    public PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        Map<Serializable, Double> probsByClassification = Maps.newHashMap();
        for (Serializable classification : classifications) {
            probsByClassification.put(classification, getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore));
        }
        return new PredictionMap(probsByClassification);
    }

    @Override
    public Serializable getClassificationByMaxProb(AttributesMap attributes) {
        Leaf leaf = root.getLeaf(attributes);
        return leaf.getBestClassification();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Tree tree = (Tree) o;

        if (!root.equals(tree.root)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }
    
    @Override
    public String toString() {
        StringBuilder dump = new StringBuilder();
        root.dump(dump);
        return dump.toString();
    }
}
