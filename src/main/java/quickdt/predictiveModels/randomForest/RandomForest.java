package quickdt.predictiveModels.randomForest;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.tree.Leaf;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ian
 * Date: 4/18/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomForest implements PredictiveModel {

    static final long serialVersionUID = 56394564395638954L;

    public final List<Tree> trees;

    protected RandomForest(List<Tree> trees) {
        Preconditions.checkArgument(trees.size() > 0, "We must have at least one tree");
        this.trees = trees;
    }


    public void dump(PrintStream printStream, int numTrees) {
        double meanDepth = 0;
        for (int i = 0; i < numTrees; i++) {
            meanDepth += trees.get(i).node.meanDepth();
        }
        printStream.print("meanDepth " + meanDepth/numTrees + "\n");
        for (Tree tree : trees) {
            printStream.print("depth " + tree.node.meanDepth() + "\n");
        }
        for (int i = 0; i < numTrees; i++)
           trees.get(i).dump(printStream);
    }

    @Override
    public void dump(PrintStream printStream) {
        trees.get(0).dump(printStream);
    }

    @Override
    public double getProbability(Attributes attributes, Serializable classification) {
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

    @Override
    public Map<Serializable, Double> getProbabilitiesByClassification(final Attributes attributes) {
        Map<Serializable, Double> sumsByClassification = Maps.newHashMap();
        for (Tree tree : trees) {
            final Map<Serializable, Double> treeProbs = tree.getProbabilitiesByClassification(attributes);
            for (Map.Entry<Serializable, Double> tpe : treeProbs.entrySet()) {
                Double sum = sumsByClassification.get(tpe.getKey());
                if (sum == null) sum = 0.0;
                sum += tpe.getValue();
                sumsByClassification.put(tpe.getKey(), sum);
            }
        }

        Map<Serializable, Double> probsByClassification = Maps.newHashMap();
        for (Map.Entry<Serializable, Double> sumEntry : sumsByClassification.entrySet()) {
            probsByClassification.put(sumEntry.getKey(), sumEntry.getValue() / trees.size());
        }

        return probsByClassification;
    }

    @Override
    public Serializable getClassificationByMaxProb(Attributes attributes) {
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
