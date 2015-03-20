package quickml;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.data.AttributesMap;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierRMSELossFunction;

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

        final FoldedData<InstanceWithAttributesMap> data = new FoldedData<>(loadIrisDataset(), 4, 4);

        final CrossValidator<Classifier, InstanceWithAttributesMap> validator = new CrossValidator<>(new RandomForestBuilder<>(), new ClassifierLossChecker<>(new ClassifierRMSELossFunction()), data);

        final double crossValidatedLoss = validator.getLossForModel();
        double previousLoss = 0.673;
        logger.info("Cross Validated Lost: {}", crossValidatedLoss);
        assertTrue(String.format("Current loss is %s, but previous loss was %s, this is a regression", crossValidatedLoss, previousLoss), crossValidatedLoss <= previousLoss);
        assertTrue(String.format("Current loss is %s, but previous loss was %s, this is a significant improvement, previousLoss should be updated", crossValidatedLoss, previousLoss), crossValidatedLoss > previousLoss * 0.95);

    }


    private List<InstanceWithAttributesMap> loadIrisDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(BenchmarkTest.class.getResourceAsStream("iris.data.gz")))));
        final List<InstanceWithAttributesMap> instances = Lists.newLinkedList();

        String[] headings = new String[]{"sepal-length", "sepal-width", "petal-length", "petal-width"};

        String line = br.readLine();
        while (line != null) {
            String[] splitLine = line.split(",");

            AttributesMap attributes = AttributesMap.newHashMap();
            for (int x = 0; x < splitLine.length - 1; x++) {
                attributes.put(headings[x], splitLine[x]);
            }
            instances.add(new InstanceWithAttributesMap(attributes, splitLine[splitLine.length - 1]));
            line = br.readLine();
        }

        return instances;
    }
}
