package quickml.supervised.rankingModels;

import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;

/**
 * Created by alexanderhawk on 8/13/15.
 */
public class LabelPredictionWeightForRanking extends LabelPredictionWeight<ItemToOutcomeMap, RankingPrediction> {

        public LabelPredictionWeightForRanking(ItemToOutcomeMap itemToOutcomeMap,RankingPrediction rankingPrediction, double weight) {
            super(itemToOutcomeMap, rankingPrediction, weight);
        }

    public LabelPredictionWeightForRanking(ItemToOutcomeMap itemToOutcomeMap,RankingPrediction rankingPrediction) {
        super(itemToOutcomeMap, rankingPrediction, 1.0);
    }
}
