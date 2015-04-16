package quickml.supervised.classifier;

import quickml.supervised.classifier.tree.decisionTree.tree.GroupStatistics;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public interface AttributeValueIgnoringStrategy<DS extends GroupStatistics> {

    boolean shouldWeIgnoreThisValue(final DS dataSummerizer);

}
