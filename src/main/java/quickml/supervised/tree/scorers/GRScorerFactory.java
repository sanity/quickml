package quickml.supervised.tree.scorers;

import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;
import java.util.Map;

import static quickml.supervised.tree.constants.ForestOptions.DEGREE_OF_GAIN_RATIO_PENALTY;
import static quickml.supervised.tree.constants.ForestOptions.IMBALANCE_PENALTY_POWER;

/**
 * Created by alexanderhawk on 7/8/15.
 */
public abstract class GRScorerFactory<VC extends ValueCounter<VC>> implements ScorerFactory<VC>{
    protected double degreeOfGainRatioPenalty;


    public GRScorerFactory(){}

    public GRScorerFactory(double degreeOfGainRatioPenalty) {
        this.degreeOfGainRatioPenalty = degreeOfGainRatioPenalty;
    }

    @Override
    public void update(Map<String, Serializable> cfg) {
        if (cfg.containsKey(DEGREE_OF_GAIN_RATIO_PENALTY.name()))
            degreeOfGainRatioPenalty = (Double) cfg.get(DEGREE_OF_GAIN_RATIO_PENALTY.name());
    }
}
