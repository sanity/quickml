package quickml.supervised.tree.scorers;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

/**
 * Created by alexanderhawk on 7/8/15.
 */
public interface Scorer<VC extends ValueCounter<VC>> {
    double scoreSplit(VC a, VC b);
}
