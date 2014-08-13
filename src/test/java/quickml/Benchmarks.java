package quickml;

import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import quickml.supervised.crossValidation.StationaryCrossValidator;
import quickml.supervised.crossValidation.crossValLossFunctions.ClassifierLogCVLossFunction;
import quickml.data.*;
import quickml.supervised.classifier.decisionTree.Scorer;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.MSEScorer;
import quickml.supervised.classifier.decisionTree.scorers.SplitDiffScorer;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Benchmarks {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {
        List<Instance<Map<String, Serializable>>> diaInstances = loadDiabetesDataset();

        testWithInstances("diabetes", diaInstances);

        final List<Instance<Map<String, Serializable>>> moboInstances = loadMoboDataset();

        testWithInstances("mobo", moboInstances);


    }

    private static void testWithInstances(String dsName, final List<Instance<Map<String, Serializable>>> instances) {
        StationaryCrossValidator crossValidator = new StationaryCrossValidator(new ClassifierLogCVLossFunction());

        for (final Scorer scorer : Lists.newArrayList(new SplitDiffScorer(), new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE), new MSEScorer(MSEScorer.CrossValidationCorrection.TRUE))) {
            final TreeBuilder singleTreeBuilder = new TreeBuilder(scorer).binaryClassification(true);
            System.out.println(dsName+", single-tree, "+scorer+", "+crossValidator.getCrossValidatedLoss(singleTreeBuilder, instances));

            TreeBuilder forestTreeBuilder = new TreeBuilder(scorer).ignoreAttributeAtNodeProbability(0.5).binaryClassification(true);
            RandomForestBuilder randomForestBuilder = new RandomForestBuilder(forestTreeBuilder).numTrees(100).executorThreadCount(8);
            System.out.println(dsName+", random-forest, "+scorer+", "+crossValidator.getCrossValidatedLoss(randomForestBuilder, instances));
        }
    }

    public static List<Instance<Map<String, Serializable>>> loadDiabetesDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(Benchmarks.class.getResourceAsStream("diabetesDataset.txt.gz")))));
        final List<Instance<Map<String, Serializable>>> instances = Lists.newLinkedList();

        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            String[] splitLine = line.split("\\s");
            Map hashMapAttributes = new HashMap();
            for (int x=0; x<8; x++) {
                hashMapAttributes.put("attr"+x, Double.parseDouble(splitLine[x]));
            }
            final Instance<Map<String, Serializable>> instance = new InstanceImpl(hashMapAttributes, splitLine[8]);
            instances.add(instance);

        }

        return instances;
    }

    public static List<Instance<Map<String, Serializable>>> loadIrisDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(Benchmarks.class.getResourceAsStream("iris.data.gz")))));
        final List<Instance<Map<String, Serializable>>> instances = Lists.newLinkedList();

        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            String[] splitLine = line.split(",");
            Map hashMapAttributes = new HashMap();
            for (int x=0; x<splitLine.length - 1; x++) {
                hashMapAttributes.put("attr"+x, splitLine[x]);
            }
            final Instance<Map<String, Serializable>> instance = new InstanceImpl(hashMapAttributes, splitLine[splitLine.length-1]);
            instances.add(instance);

        }

        return instances;
    }

    public static List<Instance<Map<String, Serializable>>> loadMoboDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(Benchmarks.class.getResourceAsStream("mobo1.json.gz")))));

        final List<Instance<Map<String, Serializable>>> instances = Lists.newLinkedList();

        int count = 0;
        while (true) {
            count++;
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            final JSONObject jo = (JSONObject) JSONValue.parse(line);
            Map a = new HashMap();
            a.putAll((JSONObject) jo.get("attributes"));
            String binaryClassification = ((String) jo.get("output")).equals("none") ? "none" : "notNone";
            Instance<Map<String, Serializable>> instance = new InstanceImpl(a,binaryClassification);
            instances.add(instance);
        }

        return instances;
    }
}

