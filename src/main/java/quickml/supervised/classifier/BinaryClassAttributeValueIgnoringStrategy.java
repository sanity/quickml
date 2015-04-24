package quickml.supervised.classifier;

import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class BinaryClassAttributeValueIgnoringStrategy<T extends InstanceWithAttributesMap> implements AttributeValueIgnoringStrategy<ClassificationCounter> {
    private final int minOccurancesOfAttributeValue;
    private final BinaryClassifierDataProperties<T> bcp;

    public BinaryClassAttributeValueIgnoringStrategy(BinaryClassifierDataProperties<T> bcp, final int minOccurancesOfAttributeValue) {
        this.bcp = bcp;
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
    }

    public boolean shouldWeIgnoreThisValue(final ClassificationCounter termStatistics) {
        Map<Serializable, Double> counts = termStatistics.getCounts();
        if (counts.containsKey(bcp.minorityClassification) &&
                counts.get(bcp.minorityClassification) > minOccurancesOfAttributeValue) {
            return false;
        }

        if (counts.containsKey(bcp.majorityClassification) &&
                counts.get(bcp.majorityClassification) > bcp.majorityToMinorityRatio * minOccurancesOfAttributeValue) {
            return false;
        }

        if (hasBothClassifications(counts)
                && hasSufficientStatisticsForBothClassifications(counts)) {
            return false;
        }

        return true;
    }

    private boolean hasSufficientStatisticsForBothClassifications(Map<Serializable, Double> counts) {
        return counts.get(bcp.majorityClassification) > 0.6 * bcp.majorityToMinorityRatio * minOccurancesOfAttributeValue
                && counts.get(bcp.minorityClassification) > 0.6 * minOccurancesOfAttributeValue;
    }

    private boolean hasBothClassifications(Map<Serializable, Double> counts) {
        return counts.containsKey(bcp.majorityClassification) && counts.containsKey(bcp.minorityClassification);
    }

    public Serializable getMinorityClassification(){
        return bcp.minorityClassification;
    }


}
