package quickml.supervised.tree.decisionTree;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class MultiClassAtributeValueIgnoringStrategy implements AttributeValueIgnoringStrategy {
    private int minOccurancesOfAttributeValue;
    public MultiClassAtributeValueIgnoringStrategy(int minOccurancesOfAttributeValue) {
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

    @Override
    public MultiClassAtributeValueIgnoringStrategy copy() {
        return new MultiClassAtributeValueIgnoringStrategy(this.minOccurancesOfAttributeValue);
    }
}
