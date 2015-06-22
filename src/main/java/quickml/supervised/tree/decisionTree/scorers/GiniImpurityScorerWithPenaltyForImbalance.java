package quickml.supervised.tree.decisionTree.scorers;

import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.scorers.Scorer;

/**
 * Created by alexanderhawk on 6/21/15.
 */
public class GiniImpurityScorerWithPenaltyForImbalance extends GiniImpurityScorer {
    @Override
    public double scoreSplit(ClassificationCounter a, ClassificationCounter b) {
        return super.scoreSplit(a, b)*getPenaltyFactorForImabalance(a, b);
    }

    @Override
    public Scorer<ClassificationCounter> createScorer() {
        return new GiniImpurityScorerWithPenaltyForImbalance();
    }
}
