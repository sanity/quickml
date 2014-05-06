package quickdt.crossValidation;

import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

/**
 * Created by Chris on 5/5/2014.
 */
public class AUCCrossValLossTest {

    @Test(expected = RuntimeException.class)
    public void testOnlySupportBinaryClassifications() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        Mockito.when(predictiveModel.getClassificationByMaxProb(Mockito.<Attributes>any())).thenReturn("test1");
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
    public void testZeroFalsePositiveRate() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        Mockito.when(predictiveModel.getClassificationByMaxProb(Mockito.<Attributes>any())).thenReturn("test1");
        AbstractInstance instance = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance.getClassification()).thenReturn("test11");
        Mockito.when(instance.getWeight()).thenReturn(1.0);
        AbstractInstance instance2 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance2.getClassification()).thenReturn("test1");
        Mockito.when(instance2.getWeight()).thenReturn(1.0);
        AbstractInstance instance3 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance3.getClassification()).thenReturn("test1");
        Mockito.when(instance3.getWeight()).thenReturn(1.0);
        crossValLoss.addLoss(instance, predictiveModel);
        crossValLoss.addLoss(instance2, predictiveModel);
        crossValLoss.addLoss(instance3, predictiveModel);
        Assert.assertEquals(0.5, crossValLoss.getAverageLoss());
    }

    @Test
    public void testZeroTruePositiveRate() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        Mockito.when(predictiveModel.getClassificationByMaxProb(Mockito.<Attributes>any())).thenReturn("test1");
        AbstractInstance instance = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance.getClassification()).thenReturn("test2");
        Mockito.when(instance.getWeight()).thenReturn(1.0);
        AbstractInstance instance2 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance2.getClassification()).thenReturn("test2");
        Mockito.when(instance2.getWeight()).thenReturn(1.0);
        AbstractInstance instance3 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance3.getClassification()).thenReturn("test2");
        Mockito.when(instance3.getWeight()).thenReturn(1.0);
        crossValLoss.addLoss(instance, predictiveModel);
        crossValLoss.addLoss(instance2, predictiveModel);
        crossValLoss.addLoss(instance3, predictiveModel);
        Assert.assertEquals(1.0, crossValLoss.getAverageLoss());
    }

    @Test
    public void testGetRunningTotal() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        Attributes test1Attributes = Mockito.mock(Attributes.class);
        Attributes test2Attributes = Mockito.mock(Attributes.class);
        Mockito.when(predictiveModel.getClassificationByMaxProb(test1Attributes)).thenReturn("test1");
        Mockito.when(predictiveModel.getClassificationByMaxProb(test2Attributes)).thenReturn("test2");

        //true positive with weight 3
        AbstractInstance instance = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance.getClassification()).thenReturn("test1");
        Mockito.when(instance.getWeight()).thenReturn(3.0);
        Mockito.when(instance.getAttributes()).thenReturn(test1Attributes);

        //2 false negatives with weight 0.5
        AbstractInstance instance2 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance2.getClassification()).thenReturn("test1");
        Mockito.when(instance2.getWeight()).thenReturn(0.5);
        Mockito.when(instance2.getAttributes()).thenReturn(test2Attributes);

        AbstractInstance instance3 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance3.getClassification()).thenReturn("test1");
        Mockito.when(instance3.getWeight()).thenReturn(0.5);
        Mockito.when(instance3.getAttributes()).thenReturn(test2Attributes);

        crossValLoss.addLoss(instance, predictiveModel);
        crossValLoss.addLoss(instance2, predictiveModel);
        crossValLoss.addLoss(instance3, predictiveModel);

        //1 AUCPoint at (0,.75)
        double expectedArea = 1 - (.75 + (.25 / 2));

        Assert.assertEquals(expectedArea, crossValLoss.getAverageLoss());
    }

    @Test(expected = IllegalStateException.class)
    public void testTotalLossNoData() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        crossValLoss.getAverageLoss();
    }
}
