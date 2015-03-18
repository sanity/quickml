package quickml.supervised.classifier;

import com.google.common.base.Preconditions;
import quickml.supervised.classifier.decisionTree.tree.ClassificationCounter;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public class BinaryClassAttributeValueIgnoringStrategy implements AttributeValueIgnoringStrategy {
    private final int minOccurancesOfAttributeValue;
    private final BinaryClassificationProperties bcp;

    public BinaryClassAttributeValueIgnoringStrategy(BinaryClassificationProperties bcp, final int minOccurancesOfAttributeValue) {
        this.bcp = bcp;
        this.minOccurancesOfAttributeValue = minOccurancesOfAttributeValue;
    }

    public boolean shouldWeIgnoreThisValue(final ClassificationCounter testValCounts) {
        Map<Serializable, Double> counts = testValCounts.getCounts();
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


}
