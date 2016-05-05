package quickml.supervised.tree.decisionTree.scorers;

import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.*;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public class GRPenalizedGiniImpurityScorerFactory extends GRScorerFactory<ClassificationCounter> {

    public GRPenalizedGiniImpurityScorerFactory() {
    }

    public GRPenalizedGiniImpurityScorerFactory(double degreeOfGainRatioPenalty) {
        super(degreeOfGainRatioPenalty);
    }

    @Override
    public GRScorer<ClassificationCounter> getScorer(AttributeStats<ClassificationCounter> attributeStats) {
        return new GRPenalizedGiniImpurityScorer(degreeOfGainRatioPenalty, attributeStats);
    }

    @Override
    public ScorerFactory<ClassificationCounter> copy() {
        return new GRPenalizedGiniImpurityScorerFactory(degreeOfGainRatioPenalty);
    }
}
