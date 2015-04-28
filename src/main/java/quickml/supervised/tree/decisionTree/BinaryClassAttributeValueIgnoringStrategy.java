package quickml.supervised.tree.decisionTree;

import quickml.supervised.tree.decisionTree.ClassificationCounter;

import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class BinaryClassAttributeValueIgnoringStrategy implements AttributeValueIgnoringStrategy<ClassificationCounter> {
    private final int minOccurancesOfAttributeValue;
    private final BinaryClassifierDataProperties bcp;

    public BinaryClassAttributeValueIgnoringStrategy(BinaryClassifierDataProperties bcp, final int minOccurancesOfAttributeValue) {
        this.bcp = bcp;
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
    }

    public boolean shouldWeIgnoreThisValue(final ClassificationCounter termStatistics) {
        Map<Object, Double> counts = termStatistics.getCounts();
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

    private boolean hasSufficientStatisticsForBothClassifications(Map<Object, Double> counts) {
        return counts.get(bcp.majorityClassification) > 0.6 * bcp.majorityToMinorityRatio * minOccurancesOfAttributeValue
                && counts.get(bcp.minorityClassification) > 0.6 * minOccurancesOfAttributeValue;
    }

    private boolean hasBothClassifications(Map<Object, Double> counts) {
        return counts.containsKey(bcp.majorityClassification) && counts.containsKey(bcp.minorityClassification);
    }

    public Object getMinorityClassification() {
        return bcp.minorityClassification;
    }


}
