package quickdt.crossValidation;

import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;
import org.apache.mahout.classifier.evaluation.Auc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 5/5/2014.
 */
public class AUCCrossValLossTest {

    @Test(expected = RuntimeException.class)
    public void testOnlySupportBinaryClassifications() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        AbstractInstance instance = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance.getClassification()).thenReturn("instance1");
        Mockito.when(instance.getWeight()).thenReturn(1.0);
        AbstractInstance instance2 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance2.getClassification()).thenReturn("instance2");
        Mockito.when(instance2.getWeight()).thenReturn(1.0);
        AbstractInstance instance3 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance3.getClassification()).thenReturn("instance3");
        Mockito.when(instance3.getWeight()).thenReturn(1.0);
        crossValLoss.addLoss(instance, predictiveModel);
        crossValLoss.addLoss(instance2, predictiveModel);
        crossValLoss.addLoss(instance3, predictiveModel);
    }

    @Test
    public void testGetTotalLoss() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        Attributes test1Attributes = Mockito.mock(Attributes.class);
        Attributes test2Attributes = Mockito.mock(Attributes.class);
        Mockito.when(predictiveModel.getProbability(test1Attributes, "test1")).thenReturn(0.2);
        Mockito.when(predictiveModel.getProbability(test1Attributes, "test0")).thenReturn(0.3);
        Mockito.when(predictiveModel.getProbability(test2Attributes, "test1")).thenReturn(0.4);
        Mockito.when(predictiveModel.getProbability(test2Attributes, "test0")).thenReturn(0.5);

        AbstractInstance instance = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance.getClassification()).thenReturn("test1");
        Mockito.when(instance.getWeight()).thenReturn(1.0);
        Mockito.when(instance.getAttributes()).thenReturn(test1Attributes);

        AbstractInstance instance2 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance2.getClassification()).thenReturn("test1");
        Mockito.when(instance2.getWeight()).thenReturn(1.0);
        Mockito.when(instance2.getAttributes()).thenReturn(test2Attributes);

        AbstractInstance instance3 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance3.getClassification()).thenReturn("test0");
        Mockito.when(instance3.getWeight()).thenReturn(1.0);
        Mockito.when(instance3.getAttributes()).thenReturn(test2Attributes);

        AbstractInstance instance4 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance4.getClassification()).thenReturn("test0");
        Mockito.when(instance4.getWeight()).thenReturn(1.0);
        Mockito.when(instance4.getAttributes()).thenReturn(test1Attributes);

        crossValLoss.addLoss(instance, predictiveModel);
        crossValLoss.addLoss(instance2, predictiveModel);
        crossValLoss.addLoss(instance3, predictiveModel);
        crossValLoss.addLoss(instance4, predictiveModel);

        //AUC Points at 0:0 .5:0 .5:.5 1:.5 1:1
        double expectedArea = .75;

        Assert.assertEquals(expectedArea, crossValLoss.getTotalLoss());
    }

    @Test
    public void testSortDataByProbability() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        List<AUCCrossValLoss.AUCData> aucDataList = getAucDataList();
        crossValLoss.sortDataByProbability(aucDataList);
        double probability = 0;
        for(AUCCrossValLoss.AUCData aucData : aucDataList) {
            Assert.assertTrue(aucData.getProbability() >= probability);
            probability = aucData.getProbability();
        }
    }

    @Test
    public void testGetAUCPoint() {
        //FPR = FP / (FP + TN)
        //TRP = TP / (TP + FN)
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        AUCCrossValLoss.AUCPoint aucPoint = crossValLoss.getAUCPoint(2, 2, 0, 1);
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
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        List<AUCCrossValLoss.AUCData> aucDataList = getAucDataList();
        crossValLoss.sortDataByProbability(aucDataList);
        ArrayList<AUCCrossValLoss.AUCPoint> aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
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
        aucDataList.add(new AUCCrossValLoss.AUCData("test1", 1.0, 0.8));
        aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        //Added data with same probability, should not result in new number of points but will change rates
        Assert.assertEquals(aucDataList.size(), aucPoints.size());
    }

    @Test(expected = IllegalStateException.class)
    public void testTotalLossNoData() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        crossValLoss.getTotalLoss();
    }

    private List<AUCCrossValLoss.AUCData> getAucDataList() {
        List<AUCCrossValLoss.AUCData> aucDataList = new ArrayList<>();
        aucDataList.add(new AUCCrossValLoss.AUCData("test1", 1.0, 0.5));
        aucDataList.add(new AUCCrossValLoss.AUCData("test0", 1.0, 0.3));
        aucDataList.add(new AUCCrossValLoss.AUCData("test0", 1.0, 0.6));
        aucDataList.add(new AUCCrossValLoss.AUCData("test1", 1.0, 0.2));
        aucDataList.add(new AUCCrossValLoss.AUCData("test1", 1.0, 0.7));
        aucDataList.add(new AUCCrossValLoss.AUCData("test1", 1.0, 0.8));
        return aucDataList;
    }

    @Test
    public void testAgainstMahout() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        List<AUCCrossValLoss.AUCData> aucDataList = getAucDataList();
        crossValLoss.sortDataByProbability(aucDataList);
        ArrayList<AUCCrossValLoss.AUCPoint> aucPoints = crossValLoss.getAUCPointsFromData(aucDataList);
        double aucCrossValLoss = crossValLoss.getAUCLoss(aucPoints);

        Auc auc = new Auc();
        auc.add(1, 0.2);
        auc.add(0, 0.3);
        auc.add(1, 0.5);
        auc.add(0, 0.6);
        auc.add(1, 0.7);
        auc.add(1, 0.8);
        double mahoutAucLoss = 1.0 - auc.auc();
        Assert.assertEquals(mahoutAucLoss, aucCrossValLoss);
    }
}
