package quickml.supervised.tree.scorers;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;
import java.util.Map;

import static quickml.supervised.tree.constants.ForestOptions.IMBALANCE_PENALTY_POWER;

/**
 * Created by alexanderhawk on 7/8/15.
 */
public abstract class GRImbalancedScorerFactory<VC extends ValueCounter<VC>> extends GRScorerFactory<VC> {
    protected double imbalancePenaltyPower;


    public GRImbalancedScorerFactory(){}

    public GRImbalancedScorerFactory(double degreeOfGainRatioPenalty, double imbalancePenaltyPower) {
        super(degreeOfGainRatioPenalty);
        this.imbalancePenaltyPower = imbalancePenaltyPower;
    }

    @Override
    public void update(Map<String, Serializable> cfg) {
        super.update(cfg);
        if (cfg.containsKey(IMBALANCE_PENALTY_POWER.name()))
            imbalancePenaltyPower = (Double) cfg.get(IMBALANCE_PENALTY_POWER.name());
    }
}
