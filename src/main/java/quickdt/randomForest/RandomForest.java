package quickdt.randomForest;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import quickdt.Attributes;
import quickdt.Leaf;
import quickdt.Node;

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
public class RandomForest implements Serializable {
    public final List<Node> trees;

    protected RandomForest(List<Node> trees) {
        this.trees = trees;
    }

    public double getProbability(Attributes attributes, Serializable classification) {
        double total = 0;
        for (Node tree : trees) {
            total += tree.getLeaf(attributes).getProbability(classification);
        }
        return total / trees.size();
    }

    public Serializable getClassificationByMaxProb(Attributes attributes) {
        Map<Serializable, AtomicDouble> probTotals = Maps.newHashMap();
        for (Node tree : trees) {
            Leaf leaf =tree.getLeaf(attributes);
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

}
