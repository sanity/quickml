package quickml.supervised.classifier;

import quickml.supervised.classifier.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class MultiClassAtributeIgnoringStrategy implements AttributeValueIgnoringStrategy{
    private int minOccurancesOfAttributeValue;
    public MultiClassAtributeIgnoringStrategy(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
    }

    public boolean shouldWeIgnoreThisValue(final ClassificationCounter testValCounts) {
        Map<Serializable, Double> counts = testValCounts.getCounts();

        for (Serializable key : counts.keySet()) {
            if (counts.get(key).doubleValue() < minOccurancesOfAttributeValue) {
                return true;
            }
        }

        return false;
    }
}
