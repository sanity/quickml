package quickml.supervised;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.BenchmarkTest;
import quickml.data.AttributesMap;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.SimpleCrossValidator;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.lossfunctions.classifierLossFunctions.ClassifierRMSELossFunction;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static org.junit.Assert.assertTrue;

/**
 * Created by ian on 7/4/14.
 */
public class PredictiveAccuracyTests {
    private static final Logger logger = LoggerFactory.getLogger(PredictiveAccuracyTests.class);

    @Test
    public void irisTest() throws Exception {

        final FoldedData<ClassifierInstance> data = new FoldedData<ClassifierInstance>(loadIrisDataset(), 4, 4);

        final SimpleCrossValidator<RandomDecisionForest, ClassifierInstance> validator = new SimpleCrossValidator<RandomDecisionForest, ClassifierInstance>(
                new RandomDecisionForestBuilder<ClassifierInstance>(new DecisionTreeBuilder<>().minAttributeValueOccurences(10).maxDepth(12)), new ClassifierLossChecker<ClassifierInstance, RandomDecisionForest>(new ClassifierRMSELossFunction()), data);

        final double crossValidatedLoss = validator.getLossForModel();
        double previousLoss = 0.62;//0.673;
        logger.info("Cross Validated Lost: {}", crossValidatedLoss);
        assertTrue(String.format("Current loss is %s, but previous loss was %s, this is a regression", crossValidatedLoss, previousLoss), crossValidatedLoss <= previousLoss*1.15);

    }


    public static  List<ClassifierInstance> loadIrisDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(BenchmarkTest.class.getResourceAsStream("iris.data.gz")))));
        final List<ClassifierInstance> instances = Lists.newLinkedList();

        String[] headings = new String[]{"sepal-length", "sepal-width", "petal-length", "petal-width"};

        String line = br.readLine();
        while (line != null) {
            String[] splitLine = line.split(",");

            AttributesMap attributes = AttributesMap.newHashMap();
            for (int x = 0; x < splitLine.length - 1; x++) {
                attributes.put(headings[x], Double.valueOf((String)splitLine[x]));
            }
            instances.add(new ClassifierInstance(attributes, splitLine[splitLine.length - 1]));
            line = br.readLine();
        }

        return instances;
    }
}
