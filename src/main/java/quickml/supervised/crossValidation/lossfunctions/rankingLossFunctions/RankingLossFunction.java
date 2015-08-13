package quickml.supervised.crossValidation.lossfunctions.rankingLossFunctions;

import quickml.supervised.crossValidation.lossfunctions.LossFunction;
import quickml.supervised.rankingModels.ItemToOutcomeMap;
import quickml.supervised.rankingModels.LabelPredictionWeightForRanking;
import quickml.supervised.rankingModels.RankingPrediction;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by alexanderhawk on 8/12/15.
 */
public interface RankingLossFunction extends LossFunction<List<LabelPredictionWeightForRanking>> {
    /**Map keys are the rankings, where doubles are the numeric values of the actual outcomes*/
}
