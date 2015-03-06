package quickml.supervised.classifier;

import junit.framework.TestCase;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;

import static quickml.supervised.InstanceLoader.getAdvertisingInstances;

public class StaticBuildersTest extends TestCase {
    private static final Logger logger = LoggerFactory.getLogger(StaticBuildersTest.class);
    public void getOptimizedDownsampledRandomForestIntegrationTest() throws Exception {
        DownsamplingClassifier downsamplingClassifier = StaticBuilders.getOptimizedDownsampledRandomForest(getAdvertisingInstances(), 1, .2, new WeightedAUCCrossValLossFunction(1.0));
        logger.info("weighte auc loss should be between 0.36 and 0.42");
    }
}