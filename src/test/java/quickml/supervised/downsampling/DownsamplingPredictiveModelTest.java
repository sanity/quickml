package quickml.supervised.downsampling;

import junit.framework.Assert;
import org.testng.annotations.Test;
import quickml.data.AttributesMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;

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
        when(classifier.getProbability(any(AttributesMap.class), eq(Boolean.TRUE))).thenReturn(0.5);
        DownsamplingClassifier downsamplingClassifier = new DownsamplingClassifier(classifier, Boolean.FALSE, Boolean.TRUE, 0.9);
        double corrected = downsamplingClassifier.getProbability(AttributesMap.newHashMap(), Boolean.TRUE);
        double error = Math.abs(corrected - 0.1/1.1);
        Assert.assertTrue(String.format("Error (%s) should be negligible", error), error < 0.0000001);
    }
}
