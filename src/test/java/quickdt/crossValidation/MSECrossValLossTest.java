package quickdt.crossValidation;

import junit.framework.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import quickdt.data.AbstractInstance;
import quickdt.data.Attributes;
import quickdt.predictiveModels.PredictiveModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Chris on 5/5/2014.
 */
public class MSECrossValLossTest {

    @Test
    public void testGetTotalLoss() {
        MSECrossValLoss crossValLoss = new MSECrossValLoss();
        PredictiveModel predictiveModel = Mockito.mock(PredictiveModel.class);
        Attributes test1Attributes = Mockito.mock(Attributes.class);
        Attributes test2Attributes = Mockito.mock(Attributes.class);
        Mockito.when(predictiveModel.getProbability(test1Attributes, "test1")).thenReturn(0.75);
        Mockito.when(predictiveModel.getProbability(test2Attributes, "test1")).thenReturn(0.5);

        AbstractInstance instance = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance.getClassification()).thenReturn("test1");
        Mockito.when(instance.getWeight()).thenReturn(2.0);
        Mockito.when(instance.getAttributes()).thenReturn(test1Attributes);

        AbstractInstance instance2 = Mockito.mock(AbstractInstance.class);
        Mockito.when(instance2.getClassification()).thenReturn("test1");
        Mockito.when(instance2.getWeight()).thenReturn(1.0);
        Mockito.when(instance2.getAttributes()).thenReturn(test2Attributes);

        List<AbstractInstance> instances = new LinkedList<>();
        instances.add(instance);
        instances.add(instance2);

        Assert.assertEquals(0.125, crossValLoss.getLoss(instances, predictiveModel));
    }
}
