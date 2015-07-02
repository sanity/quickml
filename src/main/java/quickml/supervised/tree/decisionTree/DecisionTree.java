package quickml.supervised.tree.decisionTree;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.tree.Tree;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.Node;

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
public class DecisionTree extends AbstractClassifier implements Tree<PredictionMap> {
    static final long serialVersionUID = 56394564395635672L;
    public final Node<ClassificationCounter> root;
    private HashSet<Serializable> classifications = new HashSet<>();

    public DecisionTree(Node<ClassificationCounter> root, Set<Serializable> classifications) {
        this.root = root;
        this.classifications = Sets.newHashSet(classifications);
    }

    public Set<Serializable> getClassifications() {
        return classifications;
    }


    @Override
    public double getProbability(AttributesMap attributes, Serializable classification) {
        Leaf<ClassificationCounter> dtLeaf = root.getLeaf(attributes);
        ClassificationCounter valueCounter = dtLeaf.getValueCounter();
        return valueCounter.getCount(classification) / valueCounter.getTotal();
    }


    @Override
    public double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore) {
        return getProbabilityWithoutAttributesHelper(root, attributes, classification, attributesToIgnore);
    }

    private double getProbabilityWithoutAttributesHelper(Node<ClassificationCounter> node, AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore) {
        if (node instanceof Branch) {
            Branch branch = (Branch) node;
            if (attributesToIgnore.contains(branch.attribute)) {
                return branch.getProbabilityOfTrueChild() * getProbabilityWithoutAttributesHelper(branch.getTrueChild(), attributes, classification, attributesToIgnore) +
                        (1 - branch.getProbabilityOfTrueChild()) * getProbabilityWithoutAttributesHelper(branch.getFalseChild(), attributes, classification, attributesToIgnore);
            } else {
                if (branch.decide(attributes)) {
                    return getProbabilityWithoutAttributesHelper(branch.getTrueChild(), attributes, classification, attributesToIgnore);
                } else {
                    return getProbabilityWithoutAttributesHelper(branch.getFalseChild(), attributes, classification, attributesToIgnore);
                }
            }
        } else {
            Leaf<ClassificationCounter> leaf = (Leaf<ClassificationCounter>) node;
            ClassificationCounter classificationCounter = leaf.getValueCounter();
            return classificationCounter.getCount(classification) / classificationCounter.getTotal();
        }
    }

    @Override
    public PredictionMap predict(AttributesMap attributes) {
        Leaf<ClassificationCounter> dtLeaf = root.getLeaf(attributes);
        ClassificationCounter valueCounter = dtLeaf.getValueCounter();
        Map<Serializable, Double> probsByClassification = Maps.newHashMap();
        for (Serializable classification : valueCounter.allClassifications()) {
            double probability = valueCounter.getCount(classification) / valueCounter.getTotal();
            probsByClassification.put(classification, probability);
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
        Leaf<ClassificationCounter> leaf = root.getLeaf(attributes);
        ClassificationCounter classificationCounter = leaf.getValueCounter();
        return classificationCounter.mostPopular().getValue0();//returns best class.
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


    protected transient volatile Map.Entry<Serializable, Double> bestClassificationEntry = null;

}
