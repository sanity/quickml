package quickml.supervised.crossValidation.lossfunctions.rankingLossFunctions;

import com.google.common.collect.Lists;
import quickml.supervised.rankingModels.ItemToOutcomeMap;
import quickml.supervised.rankingModels.LabelPredictionWeightForRanking;
import quickml.supervised.rankingModels.RankingPrediction;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by alexanderhawk on 8/13/15.
 */
public class NDCG implements RankingLossFunction {
    int k = Integer.MAX_VALUE;
    public NDCG(int k) {
        this.k = k;
    }
    public NDCG() {
    }

        /**normailized discounted cumulative gain*/

    @Override
    public Double getLoss(List<LabelPredictionWeightForRanking> results) {
        double loss = 0;
        for (LabelPredictionWeightForRanking lpw : results) {
            loss+=nDCGForInstance(lpw);
        }
        return -loss; //need to change this to be negative NDCG
    }

    @Override
    public String getName() {
        return "NDCG";
    }

    private double nDCGForInstance(LabelPredictionWeightForRanking lpw) {
        double dcg =dcg(lpw, k);
        double idcg = idcg(lpw, k);
        return dcg/idcg;
    }

    public static double dcg(LabelPredictionWeightForRanking lpw, int k) {
        ItemToOutcomeMap ito = lpw.getLabel();
        RankingPrediction rp = lpw.getPrediction();
        double dcg = 0;
        for (Serializable item : ito.getItemsWithOutcomes()) {
            double outcome = ito.getOutcome(item);
            int rank = rp.getRankOfItem(item);
            if (rank < k) {
                dcg += dcgSummand(outcome, rank);
            }
        }
        return dcg;
    }

    public static double dcgSummand(double outcome, int rank) {
        double numerator = Math.pow(2,outcome) -1;
        double denominator = Math.log(1+rank)/Math.log(2);
        return numerator/denominator;
    }

    public static double idcg(LabelPredictionWeightForRanking lpw, int k) {
        ItemToOutcomeMap ito = lpw.getLabel();
        List<Double> outcomes = Lists.newArrayList(ito.getOutcomes());
        //sort descending order
        if (outcomes.size()==1) {
            return dcgSummand(outcomes.get(0), 1);
        }

        Collections.sort(outcomes, new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return -Double.compare(o1, o2);
            }
        });
        double idcg = 0;
        for (int i = 0; i < outcomes.size(); i++) {
            if (i>k) {
                break;
            }
            Double outcome = outcomes.get(i);
            int rank = i + 1;
            idcg += dcgSummand(outcome, rank);
        }
        return idcg;
    }
}
