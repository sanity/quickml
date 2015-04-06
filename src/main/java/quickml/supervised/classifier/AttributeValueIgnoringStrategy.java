package quickml.supervised.classifier;

import quickml.supervised.classifier.tree.decisionTree.tree.ClassificationCounter;
import quickml.supervised.classifier.tree.decisionTree.tree.DataSummerizer;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public interface AttributeValueIgnoringStrategy<DS extends DataSummerizer> {

    boolean shouldWeIgnoreThisValue(final DS dataSummerizer);

}
