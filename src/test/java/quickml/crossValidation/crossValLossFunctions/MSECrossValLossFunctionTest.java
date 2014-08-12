package quickml.crossValidation.crossValLossFunctions;

import junit.framework.Assert;
import org.junit.Test;
import quickml.data.MapWithDefaultOfZero;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chris on 5/5/2014.
 */
public class MSECrossValLossFunctionTest {

    @Test
    public void testGetTotalLoss() {
        ClassifierMSECrossValLossFunction crossValLoss = new ClassifierMSECrossValLossFunction();

        List<LabelPredictionWeight<MapWithDefaultOfZero>> labelPredictionWeights = new LinkedList<>();
        MapWithDefaultOfZero map = MapWithDefaultOfZero.newMap();
        map.put("test1", 0.75);
        labelPredictionWeights.add(new LabelPredictionWeight<MapWithDefaultOfZero>("test1", map, 2.0));
        map = MapWithDefaultOfZero.newMap();
        map.put("test1", 0.5);
        labelPredictionWeights.add(new LabelPredictionWeight<MapWithDefaultOfZero>("test1", map, 1.0));


        Assert.assertEquals(0.125, crossValLoss.getLoss(labelPredictionWeights));
    }
}
