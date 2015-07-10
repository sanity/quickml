package quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldScorers;

import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.OldScorer;
import quickml.supervised.PredictiveModelsFromPreviousVersionsToBenchMarkAgainst.oldTree.OldClassificationCounter;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by chrisreeves on 6/24/14.
 */
public class GiniImpurityOldScorer implements OldScorer {
    @Override
    public double scoreSplit(OldClassificationCounter a, OldClassificationCounter b) {
        OldClassificationCounter parent = OldClassificationCounter.merge(a, b);
        double parentGiniIndex = getGiniIndex(parent);
        double aGiniIndex = getGiniIndex(a) * a.getTotal() / parent.getTotal() ;
        double bGiniIndex = getGiniIndex(b) * b.getTotal() / parent.getTotal();
        return parentGiniIndex - aGiniIndex - bGiniIndex;
    }

    private double getGiniIndex(OldClassificationCounter cc) {
        double sum = 0.0d;
        for (Map.Entry<Serializable, Double> e : cc.getCounts().entrySet()) {
            double error = (cc.getTotal() > 0) ? e.getValue() / cc.getTotal() : 0;
            sum += error * error;
        }
        return 1.0d - sum;
    }

    @Override
    public String toString() {
        return "GiniImpurity";
    }
}
