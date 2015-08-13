package quickml.supervised.rankingModels;

import quickml.supervised.crossValidation.LossChecker;
import quickml.supervised.crossValidation.lossfunctions.rankingLossFunctions.RankingLossFunction;

import java.util.List;

/**
 * Created by alexanderhawk on 8/12/15.
 */
public class RankingLossChecker<PM extends RankingModel, I extends RankingInstance> implements LossChecker<PM, I> {
        private RankingLossFunction lossFunction;

        public RankingLossChecker(RankingLossFunction lossFunction) {
            this.lossFunction = lossFunction;
        }

        @Override
        public double calculateLoss(PM predictiveModel, List<I> validationSet) {
            return lossFunction.getLoss(Utils.getLabelPredictionWeights(predictiveModel, validationSet));
        }

}

