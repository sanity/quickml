package quickml.supervised.classifier.downsampling;

import junit.framework.Assert;
import org.testng.annotations.Test;
import quickml.supervised.classifier.Classifier;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Created by ian on 4/24/14.
 */
public class DownsamplingPredictiveModelTest {
    @Test
    public void simpleTest() {
        final Classifier classifier = mock(Classifier.class);
        when(classifier.getProbability(any(Map.class), eq(Boolean.TRUE))).thenReturn(0.5);
        DownsamplingClassifier downsamplingClassifier = new DownsamplingClassifier(classifier, Boolean.FALSE, Boolean.TRUE, 0.9);
        double corrected = downsamplingClassifier.getProbability(new HashMap(), Boolean.TRUE);
        double error = Math.abs(corrected - 0.1/1.1);
        Assert.assertTrue(String.format("Error (%s) should be negligible", error), error < 0.0000001);
    }
}
