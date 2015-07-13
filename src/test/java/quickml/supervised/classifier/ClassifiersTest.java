
package quickml.supervised.classifier;

        import org.javatuples.Pair;
        import org.junit.Test;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import quickml.data.ClassifierInstance;
        import quickml.data.OnespotDateTimeExtractor;
        import quickml.supervised.classifier.downsampling.DownsamplingClassifier;
        import quickml.supervised.crossValidation.lossfunctions.WeightedAUCCrossValLossFunction;

        import java.io.Serializable;
        import java.util.List;
        import java.util.Map;

        import static quickml.InstanceLoader.getAdvertisingInstances;


public class ClassifiersTest {
    private static final Logger logger = LoggerFactory.getLogger(ClassifiersTest.class);


    public void getOptimizedDownsampledRandomForestIntegrationTest() throws Exception {
        double fractionOfDataForValidation = .2;
        int rebuildsPerValidation = 1;
        List<ClassifierInstance> trainingData = getAdvertisingInstances().subList(0, 3000);
        OnespotDateTimeExtractor dateTimeExtractor = new OnespotDateTimeExtractor();
        Pair<Map<String, Serializable>, DownsamplingClassifier> downsamplingClassifierPair =
                Classifiers.<ClassifierInstance>getOptimizedDownsampledRandomForest(trainingData, rebuildsPerValidation, fractionOfDataForValidation, new WeightedAUCCrossValLossFunction(1.0), dateTimeExtractor);
        logger.info("logged weighted auc loss should be between 0.25 and 0.28");
    }
}