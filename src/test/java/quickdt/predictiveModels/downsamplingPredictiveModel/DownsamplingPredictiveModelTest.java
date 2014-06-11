package quickdt.predictiveModels.downsamplingPredictiveModel;

import junit.framework.Assert;
import org.testng.annotations.Test;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.predictiveModels.PredictiveModel;

import static org.mockito.Mockito.*;

/**
 * Created by ian on 4/24/14.
 */
public class DownsamplingPredictiveModelTest {
    @Test
    public void simpleTest() {
        final PredictiveModel wrappedPredictiveModel = mock(PredictiveModel.class);
        when(wrappedPredictiveModel.getProbability(any(Attributes.class), eq(Boolean.TRUE))).thenReturn(0.5);
        DownsamplingPredictiveModel downsamplingPredictiveModel = new DownsamplingPredictiveModel(wrappedPredictiveModel, Boolean.FALSE, Boolean.TRUE, 0.9);
        double corrected = downsamplingPredictiveModel.getProbability(new HashMapAttributes(), Boolean.TRUE);
        double error = Math.abs(corrected - 0.1/1.1);
        Assert.assertTrue(String.format("Error (%s) should be negligible", error), error < 0.0000001);
    }
}
