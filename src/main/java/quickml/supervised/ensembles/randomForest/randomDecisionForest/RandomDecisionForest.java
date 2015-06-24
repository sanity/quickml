package quickml.supervised.ensembles.randomForest.randomDecisionForest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;

import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.ensembles.randomForest.RandomForest;
import quickml.supervised.tree.decisionTree.DecisionTree;
import quickml.supervised.tree.decisionTree.nodes.DTLeaf;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.nodes.Leaf;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomDecisionForest  extends AbstractClassifier implements RandomForest<PredictionMap, DecisionTree> {

    static final long serialVersionUID = 56394564395638954L;

    public final List<DecisionTree> decisionTrees;
    private Set<Object> classifications = new HashSet<>();
    private boolean binaryClassification = true;

    protected RandomDecisionForest(List<DecisionTree> decisionTrees, Set<Object> classifications) {
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
    public double getProbability(AttributesMap attributes, Object classification) {
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

    public double getProbabilityWithoutAttributes(AttributesMap attributes, Object classification, Set<String> attributesToIgnore) {
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
        PredictionMap sumsByClassification = new PredictionMap(new HashMap<Object, Double>());
        for (DecisionTree decisionTree : decisionTrees) {
            final PredictionMap treeProbs = decisionTree.predict(attributes);
            for (Map.Entry<Object, Double> tpe : treeProbs.entrySet()) {
                Double sum = sumsByClassification.get(tpe.getKey());
                if (sum == null) sum = 0.0;
                sum += tpe.getValue();
                sumsByClassification.put(tpe.getKey(), sum);
            }
        }
        PredictionMap probsByClassification = new PredictionMap(new HashMap<Object, Double>());
        for (Map.Entry<Object, Double> sumEntry : sumsByClassification.entrySet()) {
            probsByClassification.put(sumEntry.getKey(), sumEntry.getValue() / decisionTrees.size());
        }
        return probsByClassification;
    }

    @Override
    public PredictionMap predictWithoutAttributes(AttributesMap attributes, Set<String> attributesToIgnore) {
        PredictionMap sumsByClassification = new PredictionMap(new HashMap<Object, Double>());
        for (DecisionTree decisionTree : decisionTrees) {
            final PredictionMap treeProbs = decisionTree.predictWithoutAttributes(attributes, attributesToIgnore);
            for (Map.Entry<Object, Double> tpe : treeProbs.entrySet()) {
                Double sum = sumsByClassification.get(tpe.getKey());
                if (sum == null) sum = 0.0;
                sum += tpe.getValue();
                sumsByClassification.put(tpe.getKey(), sum);
            }
        }
        PredictionMap probsByClassification = new PredictionMap(new HashMap<Object, Double>());
        for (Map.Entry<Object, Double> sumEntry : sumsByClassification.entrySet()) {
            probsByClassification.put(sumEntry.getKey(), sumEntry.getValue() / decisionTrees.size());
        }
        return probsByClassification;
    }

    private PredictionMap getPredictionForTwoClasses(AttributesMap attributes) {
        PredictionMap probsByClassification = PredictionMap.newMap();
        Iterator<Object> classIterator = classifications.iterator();
        if (!classIterator.hasNext()) {
            throw new RuntimeException("no class labels present in classification set");
        }
        Object firstClassification = classIterator.next();
        double firstProbability = getProbability(attributes, firstClassification);
        probsByClassification.put(firstClassification, firstProbability);
        if (classIterator.hasNext()) {
            Object secondClassification = classIterator.next();
            probsByClassification.put(secondClassification, 1.0 - firstProbability);
        }
        return probsByClassification;
    }

    @Override
    public Object getClassificationByMaxProb(AttributesMap attributes) {
        Map<Object, AtomicDouble> probTotals = Maps.newHashMap();
        for (DecisionTree decisionTree : decisionTrees) {
            PredictionMap predictionMap = decisionTree.predict(attributes);
            for (Object key : predictionMap.keySet()) {
                if (probTotals.containsKey(key)) {
                    probTotals.put(key, new AtomicDouble(probTotals.get(key).getAndAdd(predictionMap.get(key))));
                } else {
                    probTotals.put(key, new AtomicDouble(predictionMap.get(key)));
                }
            }
        }

        Object bestClassification = null;
        double bestClassificationTtlProb = 0;
        for (Map.Entry<Object, AtomicDouble> classificationProb : probTotals.entrySet()) {
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
