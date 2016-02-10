package quickml.supervised.tree.regressionTree;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.data.AttributesMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.tree.Tree;
import quickml.supervised.tree.nodes.Branch;
import quickml.supervised.tree.nodes.Leaf;
import quickml.supervised.tree.nodes.LeafDepthStats;
import quickml.supervised.tree.nodes.Node;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

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
public class RegressionTree implements Tree<Double> {
    static final long serialVersionUID = 56394564395635672L;
    public final Node<MeanValueCounter> root;

    public RegressionTree(Node<MeanValueCounter> root) {
        this.root = root;
    }
    

    @Override
    public Double predict(AttributesMap attributes) {
        Leaf<MeanValueCounter> dtLeaf = root.getLeaf(attributes);
        MeanValueCounter valueCounter = dtLeaf.getValueCounter();
        return valueCounter.getAccumulatedValue() / valueCounter.getTotal();
    }

    public double calcMeanDepth(){
        LeafDepthStats leafDepthStats = new LeafDepthStats();
        root.calcLeafDepthStats(leafDepthStats);
        return (1.0*leafDepthStats.ttlDepth)/leafDepthStats.ttlSamples;
    }

    public double calcMedianDepth() {
        LeafDepthStats leafDepthStats = new LeafDepthStats();
        root.calcLeafDepthStats(leafDepthStats);
        long counts = 0;
        int depth = 0;
        while (counts < leafDepthStats.ttlSamples/2) {
            if (leafDepthStats.depthDistribution.containsKey(depth)) {
                counts += leafDepthStats.depthDistribution.get(depth);
            }
            if (counts < leafDepthStats.ttlSamples/2) {
                depth++;
            }

        }
        return depth;
    }


    @Override
    public Double predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        return getProbabilityWithoutAttributesHelper(root, attributes, attributesToIgnore);
    }

    private double getProbabilityWithoutAttributesHelper(Node<MeanValueCounter> node, AttributesMap attributes, Set<String> attributesToIgnore) {
       //return getProbabilityOfPositiveClassification(attributes, classification);

        if (node instanceof Branch) {
            Branch branch = (Branch) node;
            if (attributesToIgnore.contains(branch.attribute)) {
                return branch.getProbabilityOfTrueChild() * getProbabilityWithoutAttributesHelper(branch.getTrueChild(), attributes,attributesToIgnore) +
                        (1.0 - branch.getProbabilityOfTrueChild()) * getProbabilityWithoutAttributesHelper(branch.getFalseChild(), attributes,  attributesToIgnore);
            } else {
                if (branch.decide(attributes)) {
                    return getProbabilityWithoutAttributesHelper(branch.getTrueChild(), attributes, attributesToIgnore);
                } else {
                    return getProbabilityWithoutAttributesHelper(branch.getFalseChild(), attributes,  attributesToIgnore);
                }
            }
        } else if (node instanceof Leaf) {
            Leaf<MeanValueCounter> leaf = (Leaf<MeanValueCounter>) node;
            MeanValueCounter meanValueCounter = leaf.getValueCounter();
            double prob = meanValueCounter.getAccumulatedValue()/ meanValueCounter.getTotal();
            return prob;
        }
        else {
            throw new RuntimeException("node not a branch or a leaf");
        }
    }



    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final quickml.supervised.tree.decisionTree.DecisionTree decisionTree = (quickml.supervised.tree.decisionTree.DecisionTree) o;

        if (!root.equals(decisionTree.root)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }


    protected transient volatile Map.Entry<Serializable, Double> bestClassificationEntry = null;

}
