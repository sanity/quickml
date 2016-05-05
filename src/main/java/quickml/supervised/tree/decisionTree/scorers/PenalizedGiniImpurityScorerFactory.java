package quickml.supervised.tree.decisionTree.scorers;

import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.*;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public class PenalizedGiniImpurityScorerFactory extends GRImbalancedScorerFactory<ClassificationCounter> {

    public PenalizedGiniImpurityScorerFactory() {
    }

    public PenalizedGiniImpurityScorerFactory(double degreeOfGainRatioPenalty, double imbalancePenaltyPower) {
        super(degreeOfGainRatioPenalty, imbalancePenaltyPower);
    }

    @Override
    public GRScorer<ClassificationCounter> getScorer(AttributeStats<ClassificationCounter> attributeStats) {
        return new GRPenalizedGiniImpurityScorer(degreeOfGainRatioPenalty, attributeStats);
    }

    @Override
    public ScorerFactory<ClassificationCounter> copy() {
        return new PenalizedGiniImpurityScorerFactory(degreeOfGainRatioPenalty, imbalancePenaltyPower);
    }
}
