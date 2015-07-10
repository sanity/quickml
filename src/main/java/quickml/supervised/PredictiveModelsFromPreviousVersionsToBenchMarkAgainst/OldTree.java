package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst;

import com.google.common.collect.Maps;
import quickml.data.AttributesMap;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldLeaf;
import quickml.supervised.classifier.AbstractClassifier;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldNode;

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
public class OldTree extends AbstractClassifier {
    static final long serialVersionUID = 56394564395635672L;
    public final OldNode oldNode;
    private Set<Serializable> classifications = new HashSet<>();

    protected OldTree(OldNode oldNode, Set<Serializable> classifications) {
        this.oldNode = oldNode;
        this.classifications = classifications;
    }

    public Set<Serializable> getClassifications() {
        return classifications;
    }

    @Override
    public double getProbability(AttributesMap attributes, Serializable classification) {
        OldLeaf oldLeaf = oldNode.getLeaf(attributes);
        return oldLeaf.getProbability(classification);
    }

    @Override
    public double getProbabilityWithoutAttributes(AttributesMap attributes, Serializable classification, Set<String> attributesToIgnore) {
        return oldNode.getProbabilityWithoutAttributes(attributes, classification, attributesToIgnore);
    }


    @Override
    public PredictionMap predict(AttributesMap attributes) {
        OldLeaf oldLeaf = oldNode.getLeaf(attributes);
        Map<Serializable, Double> probsByClassification = Maps.newHashMap();
        for (Serializable classification : oldLeaf.getClassifications()) {
            probsByClassification.put(classification, oldLeaf.getProbability(classification));
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
        OldLeaf oldLeaf = oldNode.getLeaf(attributes);
        return oldLeaf.getBestClassification();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OldTree oldTree = (OldTree) o;

        if (!oldNode.equals(oldTree.oldNode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return oldNode.hashCode();
    }
    
    @Override
    public String toString() {
        StringBuilder dump = new StringBuilder();
        oldNode.dump(dump);
        return dump.toString();
    }
}
