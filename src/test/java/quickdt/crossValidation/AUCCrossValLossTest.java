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

        //AUC Points at 0:0 .5:0 .5:.5 1:.5
        double expectedArea = .75;

        Assert.assertEquals(expectedArea, crossValLoss.getTotalLoss());
    }

    @Test(expected = IllegalStateException.class)
    public void testTotalLossNoData() {
        AUCCrossValLoss crossValLoss = new AUCCrossValLoss("test1");
        crossValLoss.getTotalLoss();
    }
}
