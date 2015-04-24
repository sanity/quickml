package quickml.supervised.classifier;

import quickml.supervised.classifier.tree.decisionTree.tree.TermStatistics;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public interface AttributeValueIgnoringStrategy<TS extends TermStatistics> {

    boolean shouldWeIgnoreThisValue(final TS termStatistics);

}
