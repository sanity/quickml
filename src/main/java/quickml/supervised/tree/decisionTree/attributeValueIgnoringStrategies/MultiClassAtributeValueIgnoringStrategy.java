package quickml.supervised.tree.decisionTree.attributeValueIgnoringStrategies;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class MultiClassAtributeValueIgnoringStrategy implements AttributeValueIgnoringStrategy<ClassificationCounter> {
    private int minOccurancesOfAttributeValue;
    public MultiClassAtributeValueIgnoringStrategy(int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
    }

    public boolean shouldWeIgnoreThisValue(final ClassificationCounter testValCounts) {
        Map<Object, Double> counts = testValCounts.getCounts();

        for (Object key : counts.keySet()) {
            if (counts.get(key).doubleValue() < minOccurancesOfAttributeValue) {
                return true;
            }
        }

        return false;
    }

}
