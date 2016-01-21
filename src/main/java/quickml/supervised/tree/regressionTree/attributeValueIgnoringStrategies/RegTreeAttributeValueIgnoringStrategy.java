package quickml.supervised.tree.regressionTree.attributeValueIgnoringStrategies;

import quickml.supervised.tree.attributeValueIgnoringStrategies.AttributeValueIgnoringStrategy;
import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class RegTreeAttributeValueIgnoringStrategy implements AttributeValueIgnoringStrategy<MeanValueCounter> {
    private final int minOccurancesOfAttributeValue;

    public RegTreeAttributeValueIgnoringStrategy(final int minOccurancesOfAttributeValue) {
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
    }

    public boolean shouldWeIgnoreThisValue(final MeanValueCounter termStatistics) {
        if (termStatistics.getWeightedNumValues() < minOccurancesOfAttributeValue) {
            return true;
        }
        return false;
    }

}
