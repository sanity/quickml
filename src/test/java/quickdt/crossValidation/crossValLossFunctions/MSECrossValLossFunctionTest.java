package quickdt.crossValidation.crossValLossFunctions;

import junit.framework.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Chris on 5/5/2014.
 */
public class MSECrossValLossFunctionTest {

    @Test
    public void testGetTotalLoss() {
        ClassifierMSECrossValLossFunction crossValLoss = new ClassifierMSECrossValLossFunction();

        List<LabelPredictionWeight<Map<Serializable,Double>>> labelPredictionWeights = new LinkedList<>();
        Map<Serializable,Double> map = new HashMap<>();
        map.put("test1", 0.75);
        labelPredictionWeights.add(new LabelPredictionWeight<Map<Serializable, Double>>("test1", map, 2.0));
        map = new HashMap<>();
        map.put("test1", 0.5);
        labelPredictionWeights.add(new LabelPredictionWeight<Map<Serializable, Double>>("test1", map, 1.0));


        Assert.assertEquals(0.125, crossValLoss.getLoss(labelPredictionWeights));
    }
}
