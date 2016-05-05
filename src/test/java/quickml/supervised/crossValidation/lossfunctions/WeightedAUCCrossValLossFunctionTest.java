package quickml.supervised.crossValidation.lossfunctions;

import org.junit.Test;
import quickml.data.PredictionMap;
import quickml.supervised.crossValidation.PredictionMapResult;
import quickml.supervised.crossValidation.PredictionMapResults;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.WeightedAUCCrossValLossFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Chris on 5/5/2014.
 */
public class WeightedAUCCrossValLossFunctionTest {

    @Test(expected = RuntimeException.class)
    public void testOnlySupportBinaryClassifications() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        PredictionMap predictionMap = PredictionMap.newMap();
        ArrayList<PredictionMapResult> predictionMapResults = newArrayList();
        predictionMapResults.add(new PredictionMapResult(predictionMap, "label1", 1.0));
        predictionMapResults.add(new PredictionMapResult(predictionMap, "label2", 1.0));
        predictionMapResults.add(new PredictionMapResult(predictionMap, "label3", 1.0));

        crossValLoss.getLoss(new PredictionMapResults(predictionMapResults));
    }

    @Test
    public void testGetTotalLoss() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");

        List<PredictionMapResult> results = new LinkedList<>();

        results.add(createPredictionMapResult("test1", 0.5, "test1"));
        results.add(createPredictionMapResult("test1", 0.3, "test1"));
        results.add(createPredictionMapResult("test1", 0.4, "test2"));
        results.add(createPredictionMapResult("test1", 0.2, "test2"));

        //AUC Points at 0:0 0:.5 .5:.5 1:.5 1:1 - expected area should be .25
        assertEquals(.25, crossValLoss.getLoss(new PredictionMapResults(results)), 0.00001);
    }

    private PredictionMapResult createPredictionMapResult(final String label, final double prediction, final String actual) {
        PredictionMap map = PredictionMap.newMap();
        map.put(label, prediction);
        return new PredictionMapResult(map, actual, 1.0);
    }

    @Test
    public void testSortDataByProbability() {
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = getAucDataList();
        //order by probability ascending
        Collections.sort(aucDataList);
        double probability = 0;
        for (WeightedAUCCrossValLossFunction.AUCData aucData : aucDataList) {
            assertTrue(aucData.getProbabilityOfPositiveClassification() >= probability);
            probability = aucData.getProbabilityOfPositiveClassification();
        }
    }

    @Test
    public void testGetAUCPoint() {
        //FPR = FP / (FP + TN)
        //TRP = TP / (TP + FN)
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        WeightedAUCCrossValLossFunction.AUCPoint aucPoint = crossValLoss.getAUCPoint(2, 2, 0, 1);
        assertEquals(1.0, aucPoint.getFalsePositiveRate(), 0.001);
        assertEquals(2.0 / 3.0, aucPoint.getTruePositiveRate(), 0.001);
        aucPoint = crossValLoss.getAUCPoint(2, 1, 1, 1);
        assertEquals(0.5, aucPoint.getFalsePositiveRate(), 0.001);
        assertEquals(2.0 / 3.0, aucPoint.getTruePositiveRate(), 0.001);
        aucPoint = crossValLoss.getAUCPoint(2, 0, 0, 1);
        assertEquals(0.0, aucPoint.getFalsePositiveRate(), 0.001);
        assertEquals(2.0 / 3.0, aucPoint.getTruePositiveRate(), 0.001);
        aucPoint = crossValLoss.getAUCPoint(0, 1, 3, 0);
        assertEquals(0.25, aucPoint.getFalsePositiveRate(), 0.001);
        assertEquals(0.0, aucPoint.getTruePositiveRate(), 0.001);
    }



    private List<WeightedAUCCrossValLossFunction.AUCData> getAucDataList() {
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<>();
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.5));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test0", 1.0, 0.3));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test0", 1.0, 0.6));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.2));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.7));
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.8));
        return aucDataList;
    }


    @Test
    public void testAUCWhenAlwaysPredict0() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<>();
        int dataSize = 9000; //mahout only stores 10000 data points, test against less than what they consider
        for (int i = 0; i < dataSize; i++) {
            String classification = "test0";
            if (i % 5 == 0) {
                classification = "test1";
            }
            aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData(classification, 1.0, 0.0));
        }
        //order by probability ascending
        Collections.sort(aucDataList);
        ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        double aucCrossValLoss = crossValLoss.getAUCLoss(aucPoints);


        assertEquals(0.5, aucCrossValLoss, 1E-7);
    }
}
