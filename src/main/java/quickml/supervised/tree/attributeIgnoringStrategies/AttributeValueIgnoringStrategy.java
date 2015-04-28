package quickml.supervised.tree.attributeIgnoringStrategies;

import quickml.supervised.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.tree.branchSplitStatistics.TermStatistics;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public interface AttributeValueIgnoringStrategy<TS extends TermStatistics> {

    boolean shouldWeIgnoreThisValue(final TS termStatistics);

}
