package quickml.supervised.classifier.randomForest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.classifier.tree.DecisionTree;
import quickml.supervised.classifier.tree.decisionTree.tree.DTLeaf;

import java.io.Serializable;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomDecisionForest extends AbstractClassifier {

    static final long serialVersionUID = 56394564395638954L;

    public final List<DecisionTree> decisionTrees;
    private Set<Serializable> classifications = new HashSet<>();
    private boolean binaryClassification = true;

    protected RandomDecisionForest(List<DecisionTree> decisionTrees, Set<Serializable> classifications) {
        Preconditions.checkArgument(decisionTrees.size() > 0, "We must have at least one tree");
        this.decisionTrees = decisionTrees;
        this.classifications = classifications;
        if (classifications.size() > 2) {
            binaryClassification = false;
        } else if (classifications.size() < 1) {
            throw new RuntimeException("no classes listed in classifications");
        }
    }

    @Override
    public double getProbability(AttributesMap attributes, Serializable classification) {
        double total = 0;
        for (DecisionTree decisionTree : decisionTrees) {
            final double probability = decisionTree.getProbability(attributes, classification);
            if (Double.isInfinite(probability) || Double.isNaN(probability)) {
                throw new RuntimeException("Probability must be a normal number, not "+probability);
            }
            total += probability;
        }
        return total / decisionTrees.size();
    }

    public double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore) {
        double total = 0;
        for (DecisionTree decisionTree : decisionTrees) {
            final double probability = decisionTree.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
            if (Double.isInfinite(probability) || Double.isNaN(probability)) {
                throw new RuntimeException("Probability must be a normal number, not "+probability);
            }
            total += probability;
        }
        return total / decisionTrees.size();
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
        for (DecisionTree decisionTree : decisionTrees) {
            final PredictionMap treeProbs = decisionTree.predict(attributes);
            for (Map.Entry<Serializable, Double> tpe : treeProbs.entrySet()) {
                Double sum = sumsByClassification.get(tpe.getKey());
                if (sum == null) sum = 0.0;
                sum += tpe.getValue();
                sumsByClassification.put(tpe.getKey(), sum);
            }
        }
        PredictionMap probsByClassification = new PredictionMap(new HashMap<Serializable, Double>());
        for (Map.Entry<Serializable, Double> sumEntry : sumsByClassification.entrySet()) {
            probsByClassification.put(sumEntry.getKey(), sumEntry.getValue() / decisionTrees.size());
        }
        return probsByClassification;
    }

    @Override
    public PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        PredictionMap sumsByClassification = new PredictionMap(new HashMap<Serializable, Double>());
        for (DecisionTree decisionTree : decisionTrees) {
            final PredictionMap treeProbs = decisionTree.predictWithoutAttributes(attributes, attributesToIgnore);
            for (Map.Entry<Serializable, Double> tpe : treeProbs.entrySet()) {
                Double sum = sumsByClassification.get(tpe.getKey());
                if (sum == null) sum = 0.0;
                sum += tpe.getValue();
                sumsByClassification.put(tpe.getKey(), sum);
            }
        }
        PredictionMap probsByClassification = new PredictionMap(new HashMap<Serializable, Double>());
        for (Map.Entry<Serializable, Double> sumEntry : sumsByClassification.entrySet()) {
            probsByClassification.put(sumEntry.getKey(), sumEntry.getValue() / decisionTrees.size());
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
        for (DecisionTree decisionTree : decisionTrees) {
            DTLeaf DTLeaf = decisionTree.root.getLeaf(attributes);
            for (Serializable classification : DTLeaf.getClassifications()) {
                AtomicDouble ttlProb = probTotals.get(classification);
                if (ttlProb == null) {
                    ttlProb = new AtomicDouble(0);
                    probTotals.put(classification, ttlProb);
                }
                ttlProb.addAndGet(DTLeaf.getProbability(classification));
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

        final RandomDecisionForest that = (RandomDecisionForest) o;

        if (!decisionTrees.equals(that.decisionTrees)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return decisionTrees.hashCode();
    }

}
