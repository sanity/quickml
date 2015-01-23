package quickml.supervised.crossValidation.crossValLossFunctions;

import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import quickml.data.AttributesMap;
import quickml.data.Instance;
import quickml.data.PredictionMap;
import quickml.supervised.PredictiveModel;
import org.apache.mahout.classifier.evaluation.Auc;
import quickml.supervised.Utils;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Chris on 5/5/2014.
 */
public class WeightedAUCCrossValLossFunctionTest {

    @Test(expected = RuntimeException.class)
    public void testOnlySupportBinaryClassifications() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        Instance<AttributesMap, Serializable> instance = Mockito.mock(Instance.class);
        Mockito.when(instance.getLabel()).thenReturn("instance1");
        Mockito.when(instance.getWeight()).thenReturn(1.0);
        Instance<AttributesMap, Serializable> instance2 = Mockito.mock(Instance.class);
        Mockito.when(instance2.getLabel()).thenReturn("instance2");
        Mockito.when(instance2.getWeight()).thenReturn(1.0);
        Instance<AttributesMap, Serializable> instance3 = Mockito.mock(Instance.class);
        Mockito.when(instance3.getLabel()).thenReturn("instance3");
        Mockito.when(instance3.getWeight()).thenReturn(1.0);
        List<Instance<AttributesMap, Serializable>> instances = new LinkedList<>();
        instances.add(instance);
        instances.add(instance2);
        instances.add(instance3);
        crossValLoss.getLoss(Utils.createLabelPredictionWeights(instances, predictiveModel));
    }

    @Test
    public void testGetTotalLoss() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");

        List<LabelPredictionWeight<Serializable, PredictionMap>> labelPredictionWeights = new LinkedList<>();
        PredictionMap map = PredictionMap.newMap();
        map.put("test1", 0.5);
        labelPredictionWeights.add(new LabelPredictionWeight<Serializable, PredictionMap>("test1", map, 1.0));
        map = PredictionMap.newMap();
        map.put("test1", 0.3);
        labelPredictionWeights.add(new LabelPredictionWeight<Serializable, PredictionMap>("test1", map, 1.0));
        map = PredictionMap.newMap();
        map.put("test1", 0.4);
        labelPredictionWeights.add(new LabelPredictionWeight<Serializable, PredictionMap>("test0", map, 1.0));
        map = PredictionMap.newMap();
        map.put("test1", 0.2);
        labelPredictionWeights.add(new LabelPredictionWeight<Serializable, PredictionMap>("test0", map, 1.0));

        //AUC Points at 0:0 0:.5 .5:.5 1:.5 1:1
        double expectedArea = .25;

        Assert.assertEquals(expectedArea, crossValLoss.getLoss(labelPredictionWeights));
    }

    @Test
    public void testSortDataByProbability() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = getAucDataList();
        crossValLoss.sortDataByProbability(aucDataList);
        double probability = 0;
        for(WeightedAUCCrossValLossFunction.AUCData aucData : aucDataList) {
            Assert.assertTrue(aucData.getProbability() >= probability);
            probability = aucData.getProbability();
        }
    }

    @Test
    public void testGetAUCPoint() {
        //FPR = FP / (FP + TN)
        //TRP = TP / (TP + FN)
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        WeightedAUCCrossValLossFunction.AUCPoint aucPoint = crossValLoss.getAUCPoint(2, 2, 0, 1);
        Assert.assertEquals(1.0, aucPoint.getFalsePositiveRate());
        Assert.assertEquals(2.0/3.0, aucPoint.getTruePositiveRate());
        aucPoint = crossValLoss.getAUCPoint(2, 1, 1, 1);
        Assert.assertEquals(0.5, aucPoint.getFalsePositiveRate());
        Assert.assertEquals(2.0/3.0, aucPoint.getTruePositiveRate());
        aucPoint = crossValLoss.getAUCPoint(2, 0, 0, 1);
        Assert.assertEquals(0.0, aucPoint.getFalsePositiveRate());
        Assert.assertEquals(2.0/3.0, aucPoint.getTruePositiveRate());
        aucPoint = crossValLoss.getAUCPoint(0, 1, 3, 0);
        Assert.assertEquals(0.25, aucPoint.getFalsePositiveRate());
        Assert.assertEquals(0.0, aucPoint.getTruePositiveRate());
    }

    @Test
    public void testGetAucPointsFromData() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = getAucDataList();
        crossValLoss.sortDataByProbability(aucDataList);
        ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        //We should have the same number of points as data plus 1 for threshold 0
        Assert.assertEquals(aucDataList.size()+1, aucPoints.size());
        //0 false negative, 0 true negative, 4 true positive, 2 false positive: FPR = 2 / 2, TRP = 4 / 4 get(0)
        //1 false negative, 0 true negative, 3 true positive, 2 false positive: FPR = 2 / 2, TRP = 3 / 4 get(1)
        //1 false negative, 1 true negative, 3 true positive, 1 false positive: FPR = 1 / 2, TRP = 3 / 4 get(2)
        //2 false negative, 1 true negative, 2 true positive, 1 false positive: FPR = 1 / 2, TRP = 2 / 4 get(3)
        //2 false negative, 2 true negative, 2 true positive, 0 false positive: FPR = 0, TRP = 2 / 4 get(4)
        //3 false negative, 2 true negative, 1 true positive, 0 false positive: FPR = 0, TRP = 1 / 4 get(5)
        //4 false negative, 2 true negative, 0 true positive, 0 false positive: FPR = 0, TRP = 0 get(6)
        Assert.assertEquals(1.0, aucPoints.get(0).getFalsePositiveRate());
        Assert.assertEquals(1.0, aucPoints.get(0).getTruePositiveRate());
        Assert.assertEquals(1.0, aucPoints.get(1).getFalsePositiveRate());
        Assert.assertEquals(3.0/4.0, aucPoints.get(1).getTruePositiveRate());
        Assert.assertEquals(0.5, aucPoints.get(2).getFalsePositiveRate());
        Assert.assertEquals(3.0/4.0, aucPoints.get(2).getTruePositiveRate());
        Assert.assertEquals(0.5, aucPoints.get(3).getFalsePositiveRate());
        Assert.assertEquals(2.0/4.0, aucPoints.get(3).getTruePositiveRate());
        Assert.assertEquals(0.0, aucPoints.get(4).getFalsePositiveRate());
        Assert.assertEquals(2.0/4.0, aucPoints.get(4).getTruePositiveRate());
        Assert.assertEquals(0.0, aucPoints.get(5).getFalsePositiveRate());
        Assert.assertEquals(1.0/4.0, aucPoints.get(5).getTruePositiveRate());
        Assert.assertEquals(0.0, aucPoints.get(6).getFalsePositiveRate());
        Assert.assertEquals(0.0, aucPoints.get(6).getTruePositiveRate());
        aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData("test1", 1.0, 0.8));
        aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        //Added data with same probability, should not result in new number of points but will change rates
        Assert.assertEquals(aucDataList.size(), aucPoints.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testTotalLossNoData() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        crossValLoss.getLoss(Collections.EMPTY_LIST);
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
    public void testAgainstMahout() {
        WeightedAUCCrossValLossFunction crossValLoss = new WeightedAUCCrossValLossFunction("test1");
        List<WeightedAUCCrossValLossFunction.AUCData> aucDataList = new ArrayList<>();
        int dataSize = 9000; //mahout only stores 10000 data points, test against less than what they consider
        for(int i = 0; i < dataSize; i++) {
            String classification = "test0";
            if (i % 5 == 0) {
                classification = "test1";
            }
            aucDataList.add(new WeightedAUCCrossValLossFunction.AUCData(classification, 1.0, Math.random()));
        }
        crossValLoss.sortDataByProbability(aucDataList);
        ArrayList<WeightedAUCCrossValLossFunction.AUCPoint> aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        double aucCrossValLoss = crossValLoss.getAUCLoss(aucPoints);

        Auc auc = new Auc();
        for(WeightedAUCCrossValLossFunction.AUCData aucData : aucDataList) {
            auc.add("test1".equals(aucData.getClassification()) ? 1 : 0, aucData.getProbability());
        }

        double mahoutAucLoss = 1.0 - auc.auc();
        //These aren't matching exactly, but the difference is minimal
        double acceptableDifference = 0.000000000001;
        Assert.assertTrue(Math.abs(mahoutAucLoss - aucCrossValLoss) < acceptableDifference);
    }
}
