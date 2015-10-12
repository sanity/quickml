
package quickml.supervised.rankingModels;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.supervised.crossValidation.lossfunctions.LabelPredictionWeight;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 8/13/15.
 */
public class Utils {
    public static final String RANKED_ITEMS = "rankedItems";
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);


    public static List<LabelPredictionWeightForRanking> getLabelPredictionWeights(RankingModel predictiveModel, List<? extends RankingInstance> validationSet) {
        List<LabelPredictionWeightForRanking> results = Lists.newArrayList();
        int resultsContainingValue = 0;
        for (RankingInstance instance : validationSet) {
            RankingPrediction prediction = predictiveModel.predict(instance.getAttributes());
            LabelPredictionWeightForRanking labelPredictionWeightForRanking = new LabelPredictionWeightForRanking(instance.getLabel(), prediction, instance.getWeight());
            results.add(labelPredictionWeightForRanking);
            if (prediction.getRankOfItem(instance.getFirstItem())!= Integer.MAX_VALUE) {
                resultsContainingValue++;
            } else {
       //         logger.info("predictions {}, label {}", prediction.getRankOrder().toString(), instance.getFirstItem());
            }
        }
        logger.info("results containing non zero value {}, out n examples {} ",resultsContainingValue, validationSet.size());
        return results;
    }
}
