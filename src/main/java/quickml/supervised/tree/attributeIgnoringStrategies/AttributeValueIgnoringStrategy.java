package quickml.supervised.tree.attributeIgnoringStrategies;

import quickml.supervised.tree.decisionTree.tree.TermStatistics;
import quickml.supervised.tree.branchSplitStatistics.ValueStatistics;

/**
 * Created by alexanderhawk on 3/18/15.
 */
public interface AttributeValueIgnoringStrategy<TS extends ValueStatistics> {

    boolean shouldWeIgnoreThisValue(final TS termStatistics);

}
