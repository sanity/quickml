package quickml.supervised.classifier;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.InstanceWithAttributesMap;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;

import java.util.List;
import java.util.Map;

import static quickml.supervised.InstanceLoader.getAdvertisingInstances;


public class StaticBuildersTest {
    private static final Logger logger = LoggerFactory.getLogger(StaticBuildersTest.class);


    public void getOptimizedDownsampledRandomForestIntegrationTest() throws Exception {
        double fractionOfDataForValidation = .2;
        int rebuildsPerValidation = 1;
        List<InstanceWithAttributesMap> trainingData = getAdvertisingInstances().subList(0, 3000);
        OnespotDateTimeExtractor dateTimeExtractor = new OnespotDateTimeExtractor();
        Pair<Map<String, Object>, DownsamplingClassifier> downsamplingClassifierPair =
                StaticBuilders.getOptimizedDownsampledRandomForest(trainingData, rebuildsPerValidation, fractionOfDataForValidation, new WeightedAUCCrossValLossFunction(1.0), dateTimeExtractor);
        logger.info("logged weighted auc loss should be between 0.25 and 0.28");
    }
}