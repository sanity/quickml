package quickml.supervised.tree.decisionTree.scorers;

import quickml.supervised.tree.decisionTree.valueCounters.ClassificationCounter;
import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.scorers.GRImbalancedScorer;
import quickml.supervised.tree.scorers.GRImbalancedScorerFactory;
import quickml.supervised.tree.scorers.ScorerFactory;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public class PenalizedInformationGainScorerFactory extends GRImbalancedScorerFactory<ClassificationCounter> {

    public PenalizedInformationGainScorerFactory() {
    }

    public PenalizedInformationGainScorerFactory(double degreeOfGainRatioPenalty, double imbalancePenaltyPower) {
        super(degreeOfGainRatioPenalty, imbalancePenaltyPower);
    }

    @Override
    public GRImbalancedScorer<ClassificationCounter> getScorer(AttributeStats<ClassificationCounter> attributeStats) {
        return new PenalizedInformationGainScorer(degreeOfGainRatioPenalty, imbalancePenaltyPower, attributeStats);
    }

    @Override
    public ScorerFactory<ClassificationCounter> copy() {
        return new PenalizedInformationGainScorerFactory(degreeOfGainRatioPenalty, imbalancePenaltyPower);
    }
}
