package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Maps;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.tree.decisionTree.tree.Node;

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
public class DecisionTree extends AbstractClassifier implements Tree<PredictionMap> {
    static final long serialVersionUID = 56394564395635672L;
    public final Node root;
    private Set<Object> classifications = new HashSet<>();

    public DecisionTree(Node root, Set<Object> classifications) {
        this.root = root;
        this.classifications = classifications;
    }

    public Set<Object> getClassifications() {
        return classifications;
    }

    @Override
    public double getProbability(AttributesMap attributes, Object classification) {
        DTLeaf DTLeaf = root.getLeaf(attributes);
        return DTLeaf.getProbability(classification);
    }

    @Override
    public double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attributesToIgnore) {
        return root.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
    }


    @Override
    public PredictionMap predict(AttributesMap attributes) {
        DTLeaf DTLeaf = root.getLeaf(attributes);
        Map<Object, Double> probsByClassification = Maps.newHashMap();
        for (Object classification : DTLeaf.getClassifications()) {
            probsByClassification.put(classification, DTLeaf.getProbability(classification));
        }
        return new PredictionMap(probsByClassification);
    }

    @Override
    public PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        Map<Object, Double> probsByClassification = Maps.newHashMap();
        for (Object classification : classifications) {
            probsByClassification.put(classification, getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore));
        }
        return new PredictionMap(probsByClassification);
    }

    @Override
    public Object getClassificationByMaxProb(AttributesMap attributes) {
        DTLeaf DTLeaf = root.getLeaf(attributes);
        return DTLeaf.getBestClassification();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final DecisionTree decisionTree = (DecisionTree) o;

        if (!root.equals(decisionTree.root)) return false;

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
