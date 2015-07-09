package quickml.supervised.tree.scorers;

import quickml.supervised.tree.reducers.AttributeStats;
import quickml.supervised.tree.summaryStatistics.ValueCounter;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by alexanderhawk on 7/8/15.
 */
public interface ScorerFactory<VC extends ValueCounter<VC>> extends Serializable{

    Scorer<VC> getScorer(AttributeStats<VC> attributeStats);

    ScorerFactory<VC> copy();

    void update(Map<String, Serializable> cfg);

}

