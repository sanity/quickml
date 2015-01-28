package quickml.supervised.predictiveModelOptimizer;

import com.google.common.io.CharSource;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import quickml.TrainingInstance;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import quickml.supervised.crossValidation.ClassifierOutOfTimeCrossValidator;
import quickml.supervised.crossValidation.OutOfTimeCrossValidator;
import quickml.supervised.crossValidation.crossValLossFunctions.WeightedAUCCrossValLossFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static java.nio.charset.Charset.defaultCharset;

/**
 * Created by ian on 3/1/14.
 */
public class PredictiveModelOptimizerTest {
    private static final  Logger logger =  LoggerFactory.getLogger(PredictiveModelOptimizerTest.class);

//    @Test
//    public void irisTest() throws IOException {
//        final List<Instance<AttributesMap>> instances = Benchmarks.loadIrisDataset();
//        testWithTrainingSet(instances);
//    }
//
//
//    @Test(enabled = false)
//    public void diabetesTest() throws IOException {
//        final List<Instance<AttributesMap>> instances = Benchmarks.loadDiabetesDataset();
//        testWithTrainingSet(instances);
//    }

    @Test(enabled = false)
    public void testOnespotTest() throws Exception {
        List<TrainingInstance> trainingInstances = loadOnespotInstances();
        testWithTrainingSet(trainingInstances);

    }

    //TODO[mk] this test will need to be updated
    private void testWithTrainingSet(final List<TrainingInstance> instances) {
//        RandomForestBuilderFactory randomForestBuilderFactory = new RandomForestBuilderFactory();
//        ClassifierOutOfTimeCrossValidator crossVal = new ClassifierOutOfTimeCrossValidator(new WeightedAUCCrossValLossFunction(1.0), 0.25, 1, new OutOfTimeCrossValidator.TestDateTimeExtractor());
//        PredictiveModelOptimizer predictiveModelOptimizer = new PredictiveModelOptimizer(randomForestBuilderFactory, instances, crossVal);
//        final Map<String, Object> optimalParameters = predictiveModelOptimizer.determineOptimalConfiguration();
//        logger.info("Optimal parameters: " + optimalParameters);
//        RandomForestBuilder defaultRFBuilder = new RandomForestBuilder();
//        RandomForestBuilder optimalRFBuilder = randomForestBuilderFactory.buildBuilder(optimalParameters);
//        double defaultLoss = crossVal.getCrossValidatedLoss(defaultRFBuilder, instances);
//        double optimizedLoss = crossVal.getCrossValidatedLoss(optimalRFBuilder, instances);
//        logger.info("Default PM loss: "+defaultLoss+", optimized PM loss: "+optimizedLoss);
//        Assert.assertTrue(optimizedLoss <= defaultLoss, "Default PM loss (" + defaultLoss + ") should be higher or equal to optimized PM loss (" + optimizedLoss + ")");
    }

    private List<TrainingInstance> loadOnespotInstances() throws IOException {
        CharSource charSource = asCharSource(getResource("small_training_instances.json"), defaultCharset());
        BufferedReader br = new BufferedReader(charSource.openBufferedStream());
        Gson gson = new GsonBuilder().create();
        ArrayList<TrainingInstance> list = gson.fromJson(br, new TypeToken<List<TrainingInstance>>() {}.getType());
        for (TrainingInstance trainingInstance : list) {
            trainingInstance.convertAttributes();
        }

        return list;
    }





}
