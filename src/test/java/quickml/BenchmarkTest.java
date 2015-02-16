package quickml;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Before;
import org.junit.Test;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.classifier.decisionTree.Scorer;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.MSEScorer;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import quickml.supervised.crossValidation.ClassifierLossChecker;
import quickml.supervised.crossValidation.CrossValidator;
import quickml.supervised.crossValidation.data.FoldedData;
import quickml.supervised.crossValidation.lossfunctions.ClassifierLogCVLossFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static com.google.common.collect.Lists.newArrayList;

public class BenchmarkTest {

    private ClassifierLossChecker classifierLossChecker;
    private ArrayList<Scorer> scorers;
    private TreeBuilder treeBuilder;
    private RandomForestBuilder randomForestBuilder;

    @Before
    public void setUp() throws Exception {
        classifierLossChecker = new ClassifierLossChecker(new ClassifierLogCVLossFunction(0.000001));
        scorers = newArrayList(
                new SplitDiffScorer(),
                new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE),
                new MSEScorer(MSEScorer.CrossValidationCorrection.TRUE));
        treeBuilder = createTreeBuilder();
        randomForestBuilder = createRandomForestBuilder();
    }

    @Test
    public void testDiaInstances() throws Exception {
        testWithInstances("dia", loadDiabetesDataset());
    }

    @Test
    public void testMoboInstances() throws Exception {
        testWithInstances("mobo", loadMoboDataset());
    }

    @Test
    public void testIrisInstances() throws Exception {

    }

    private void testWithInstances(String dsName, final List<ClassifierInstance> instances) {
        FoldedData<ClassifierInstance> data = new FoldedData<>(instances, 4, 4);

        for (final Scorer scorer : scorers) {
            Map<String, Object> cfg = Maps.newHashMap();
            cfg.put(TreeBuilder.SCORER, scorer);
            CrossValidator<Classifier, ClassifierInstance> validator = new CrossValidator<>(treeBuilder, classifierLossChecker, data);
            System.out.println(dsName + ", single-tree, " + scorer + ", " + validator.getLossForModel(cfg));
            validator = new CrossValidator<>(randomForestBuilder, classifierLossChecker, data);
            System.out.println(dsName + ", random-forest, " + scorer + ", " + validator.getLossForModel(cfg));
        }
    }

    private List<ClassifierInstance> loadDiabetesDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(BenchmarkTest.class.getResourceAsStream("diabetesDataset.txt.gz")))));
        final List<ClassifierInstance> instances = Lists.newLinkedList();


        String line = br.readLine();
        while (line != null) {
            String[] splitLine = line.split("\\s");
            AttributesMap attributes = AttributesMap.newHashMap();
            for (int x = 0; x < 8; x++) {
                attributes.put("attr" + x, Double.parseDouble(splitLine[x]));
            }
            instances.add(new ClassifierInstance(attributes, splitLine[8]));
            line = br.readLine();
        }

        return instances;
    }


    private List<ClassifierInstance> loadMoboDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(BenchmarkTest.class.getResourceAsStream("mobo1.json.gz")))));

        final List<ClassifierInstance> instances = Lists.newLinkedList();

        String line = br.readLine();
        while (line != null) {
            final JSONObject jo = (JSONObject) JSONValue.parse(line);
            AttributesMap a = AttributesMap.newHashMap();
            a.putAll((JSONObject) jo.get("attributes"));
            String binaryClassification = jo.get("output").equals("none") ? "none" : "notNone";
            instances.add(new ClassifierInstance(a, binaryClassification));
            line = br.readLine();
        }
        return instances;
    }

    private TreeBuilder createTreeBuilder() {
        return new TreeBuilder().binaryClassification(true);
    }

    private RandomForestBuilder createRandomForestBuilder() {
        return new RandomForestBuilder(new TreeBuilder().ignoreAttributeAtNodeProbability(0.5).binaryClassification(true)).numTrees(100).executorThreadCount(8);
    }
}

