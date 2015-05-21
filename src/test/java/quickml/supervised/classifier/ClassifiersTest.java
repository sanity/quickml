package quickml.supervised.classifier;

import com.beust.jcommander.internal.Sets;
import org.javatuples.Pair;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.ClassifierInstance;
import quickml.data.OnespotDateTimeExtractor;
import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static quickml.supervised.InstanceLoader.getAdvertisingInstances;


public class ClassifiersTest {
    private static final Logger logger = LoggerFactory.getLogger(ClassifiersTest.class);


@Test
    public void getOptimizedDownsampledRandomForestIntegrationTest() throws Exception {
        double fractionOfDataForValidation = .2;
        int rebuildsPerValidation = 1;
        Set<String> exemptAttributes = Sets.newHashSet();
        exemptAttributes.addAll(Arrays.asList("seenClick", "seenCampaignClick", "seenPixel", "seenCampaignPixel", "seenCreativeClick", "seenCampaignClick"));

    List<ClassifierInstance> trainingData = getAdvertisingInstances().subList(0, 3000);
        OnespotDateTimeExtractor dateTimeExtractor = new OnespotDateTimeExtractor();
        Pair<Map<String, Object>, DownsamplingClassifier> downsamplingClassifierPair =
                Classifiers.<ClassifierInstance>getOptimizedDownsampledRandomForest(trainingData, rebuildsPerValidation, fractionOfDataForValidation, new WeightedAUCCrossValLossFunction(1.0), dateTimeExtractor, exemptAttributes);
        logger.info("logged weighted auc loss should be between 0.25 and 0.28");
    }
}