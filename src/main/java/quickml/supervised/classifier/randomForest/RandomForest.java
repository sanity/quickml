package quickml.supervised.classifier.randomForest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.tree.Leaf;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomForest extends AbstractClassifier {

    static final long serialVersionUID = 56394564395638954L;

    public final List<Tree> trees;
    private Set<Serializable> classifications = new HashSet<>();
    private boolean binaryClassification = true;

    protected RandomForest(List<Tree> trees, Set<Serializable> classifications) {
        Preconditions.checkArgument(trees.size() > 0, "We must have at least one tree");
        this.trees = trees;
        this.classifications = classifications;
        if (classifications.size() > 2) {
            binaryClassification = false;
        } else if (classifications.size() < 1) {
            throw new RuntimeException("no classes listed in classifications");
        }
    }

    public void dump(Appendable appendable, int numTrees) {
        double meanDepth = 0;
        for (int i = 0; i < numTrees; i++) {
            meanDepth += trees.get(i).node.meanDepth();
        }
        try {
            appendable.append("meanDepth " + meanDepth / numTrees + "\n");
            for (Tree tree : trees) {
                appendable.append("depth " + tree.node.meanDepth() + "\n");
            }
            for (int i = 0; i < numTrees; i++)
                trees.get(i).dump(appendable);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void dump(Appendable appendable) {
        trees.get(0).dump(appendable);
    }

    @Override
    public double getProbability(AttributesMap attributes, Serializable classification) {
        double total = 0;
        for (Tree tree : trees) {
            final double probability = tree.getProbability(attributes, classification);
            if (Double.isInfinite(probability) || Double.isNaN(probability)) {
                throw new RuntimeException("Probability must be a normal number, not "+probability);
            }
            total += probability;
        }
        return total / trees.size();
    }

    public double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore) {
        double total = 0;
        for (Tree tree : trees) {
            final double probability = tree.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
            if (Double.isInfinite(probability) || Double.isNaN(probability)) {
                throw new RuntimeException("Probability must be a normal number, not "+probability);
            }
            total += probability;
        }
        return total / trees.size();
    }

    @Override
    public PredictionMap predict(final AttributesMap attributes) {
        if (binaryClassification)  {
            return getPredictionForTwoClasses(attributes);
        }
        else {
            return getPredictionForNClasses(attributes);
        }
    }

    private PredictionMap getPredictionForNClasses(AttributesMap attributes) {
        PredictionMap sumsByClassification = new PredictionMap(new HashMap<Serializable, Double>());
        for (Tree tree : trees) {
            final PredictionMap treeProbs = tree.predict(attributes);
            for (Map.Entry<Serializable, Double> tpe : treeProbs.entrySet()) {
                Double sum = sumsByClassification.get(tpe.getKey());
                if (sum == null) sum = 0.0;
                sum += tpe.getValue();
                sumsByClassification.put(tpe.getKey(), sum);
            }
        }
        PredictionMap probsByClassification = new PredictionMap(new HashMap<Serializable, Double>());
        for (Map.Entry<Serializable, Double> sumEntry : sumsByClassification.entrySet()) {
            probsByClassification.put(sumEntry.getKey(), sumEntry.getValue() / trees.size());
        }
        return probsByClassification;
    }

    @Override
    public PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        PredictionMap sumsByClassification = new PredictionMap(new HashMap<Serializable, Double>());
        for (Tree tree : trees) {
            final PredictionMap treeProbs = tree.predictWithoutAttributes(attributes, attributesToIgnore);
            for (Map.Entry<Serializable, Double> tpe : treeProbs.entrySet()) {
                Double sum = sumsByClassification.get(tpe.getKey());
                if (sum == null) sum = 0.0;
                sum += tpe.getValue();
                sumsByClassification.put(tpe.getKey(), sum);
            }
        }
        PredictionMap probsByClassification = new PredictionMap(new HashMap<Serializable, Double>());
        for (Map.Entry<Serializable, Double> sumEntry : sumsByClassification.entrySet()) {
            probsByClassification.put(sumEntry.getKey(), sumEntry.getValue() / trees.size());
        }
        return probsByClassification;
    }

    private PredictionMap getPredictionForTwoClasses(AttributesMap attributes) {
        PredictionMap probsByClassification = PredictionMap.newMap();
        Iterator<Serializable> classIterator = classifications.iterator();
        if (!classIterator.hasNext()) {
            throw new RuntimeException("no class labels present in classification set");
        }
        Serializable firstClassification = classIterator.next();
        double firstProbability = getProbability(attributes, firstClassification);
        probsByClassification.put(firstClassification, firstProbability);
        if (classIterator.hasNext()) {
            Serializable secondClassification = classIterator.next();
            probsByClassification.put(secondClassification, 1.0 - firstProbability);
        }
        return probsByClassification;
    }

    @Override
    public Serializable getClassificationByMaxProb(AttributesMap attributes) {
        Map<Serializable, AtomicDouble> probTotals = Maps.newHashMap();
        for (Tree tree : trees) {
            Leaf leaf =tree.node.getLeaf(attributes);
            for (Serializable classification : leaf.getClassifications()) {
                AtomicDouble ttlProb = probTotals.get(classification);
                if (ttlProb == null) {
                    ttlProb = new AtomicDouble(0);
                    probTotals.put(classification, ttlProb);
                }
                ttlProb.addAndGet(leaf.getProbability(classification));
            }
        }
        Serializable bestClassification = null;
        double bestClassificationTtlProb = 0;
        for (Map.Entry<Serializable, AtomicDouble> classificationProb : probTotals.entrySet()) {
            if (bestClassification == null || classificationProb.getValue().get() > bestClassificationTtlProb) {
                bestClassification = classificationProb.getKey();
                bestClassificationTtlProb = classificationProb.getValue().get();
            }
        }
        return bestClassification;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final RandomForest that = (RandomForest) o;

        if (!trees.equals(that.trees)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return trees.hashCode();
    }

}
