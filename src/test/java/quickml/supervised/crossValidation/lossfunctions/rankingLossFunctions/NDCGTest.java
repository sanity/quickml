package quickml.supervised.crossValidation.lossfunctions.rankingLossFunctions;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import quickml.supervised.rankingModels.ItemToOutcomeMap;
import quickml.supervised.rankingModels.LabelPredictionWeightForRanking;
import quickml.supervised.rankingModels.RankingPrediction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by alexanderhawk on 8/13/15.
 */
public class NDCGTest {
    ItemToOutcomeMap itemToOutcomeMap;
    RankingPrediction rankingPrediction;

    @Before
    public void setUp() {
        HashMap<Serializable, Double> itemToOutcomes = new HashMap<>();
        itemToOutcomes.put("c", 1.0); //has loss 1/2
        itemToOutcomes.put("a", 2.0); //has loss 3
        itemToOutcomeMap = new ItemToOutcomeMap(itemToOutcomes);
        ArrayList<Serializable> rankedList = new ArrayList<Serializable>();
        rankedList.add("a");
        rankedList.add("b");
        rankedList.add("c");
        rankedList.add("d");
        rankingPrediction = new RankingPrediction(rankedList);
    }

    @Test
    public void testDcg() throws Exception {
        double dcg = NDCG.dcg(new LabelPredictionWeightForRanking(itemToOutcomeMap, rankingPrediction), 8);
        Assert.assertEquals(dcg, 3.50, 1E-5);
    }

    @Test
    public void testIdcg() throws Exception {
        double idcg = NDCG.idcg(new LabelPredictionWeightForRanking(itemToOutcomeMap, rankingPrediction), 8);
        Assert.assertEquals(idcg, 3.6309297535714573, 1E-5);
    }
}