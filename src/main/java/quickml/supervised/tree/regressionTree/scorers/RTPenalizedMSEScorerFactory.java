package quickml.supervised.tree.regressionTree.scorers;

import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.regressionTree.valueCounters.MeanValueCounter;
import quickml.supervised.tree.scorers.GRImbalancedScorer;
import quickml.supervised.tree.scorers.GRImbalancedScorerFactory;
import quickml.supervised.tree.scorers.ScorerFactory;

/**
 * Created by alexanderhawk on 7/9/15.
 */
public class RTPenalizedMSEScorerFactory extends GRImbalancedScorerFactory<MeanValueCounter> {

    public RTPenalizedMSEScorerFactory() {
    }

    public RTPenalizedMSEScorerFactory(double degreeOfGainRatioPenalty, double imbalancePenaltyPower) {
        super(degreeOfGainRatioPenalty, imbalancePenaltyPower);
    }

    @Override
    public GRImbalancedScorer<MeanValueCounter> getScorer(AttributeStats<MeanValueCounter> attributeStats) {
        return new PenalizedMSEScorer(degreeOfGainRatioPenalty, imbalancePenaltyPower, attributeStats);
    }

    @Override
    public ScorerFactory<MeanValueCounter> copy() {
        return new RTPenalizedMSEScorerFactory(degreeOfGainRatioPenalty, imbalancePenaltyPower);
    }
}
