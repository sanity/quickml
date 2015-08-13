package quickml.supervised.rankingModels;

import quickml.data.AttributesMap;
import quickml.supervised.PredictiveModel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alexanderhawk on 8/13/15.
 */
public interface RankingModel extends PredictiveModel<AttributesMap, RankingPrediction> {
}
