package quickml.supervised.crossValidation.lossfunctions;

import org.junit.Assert;
import org.junit.Test;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierMSELossFunction;

import static com.google.common.collect.Lists.newArrayList;

public class ClassifierMSELossFunctionTest {

    @Test
    public void testGetTotalLoss() {
        ClassifierMSELossFunction crossValLoss = new ClassifierMSELossFunction();
        PredictionMapResult result1 = createPredictionMapResult("test1", 0.75, 2.0);
        PredictionMapResult result2 = createPredictionMapResult("test1", 0.5, 1.0);
        PredictionMapResults predictionMapResults = new PredictionMapResults(newArrayList(result1, result2));

        Assert.assertEquals(0.125, crossValLoss.getLoss(predictionMapResults), 0.0001);
    }

    private PredictionMapResult createPredictionMapResult(final String label, final double prediction, final double weight) {
        PredictionMap map = PredictionMap.newMap();
        map.put(label, prediction);
        return new PredictionMapResult(map, label, weight);
    }

}