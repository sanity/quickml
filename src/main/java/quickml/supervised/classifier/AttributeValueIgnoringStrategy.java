package quickml.supervised.classifier;

import quickml.supervised.classifier.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.AttributeIgnoringStrategy;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public interface AttributeValueIgnoringStrategy {

    boolean shouldWeIgnoreThisValue(final ClassificationCounter testValCounts);
    AttributeValueIgnoringStrategy copy();

}
