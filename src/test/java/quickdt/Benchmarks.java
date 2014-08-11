package quickdt;

import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import quickdt.crossValidation.StationaryCrossValidator;
import quickdt.data.*;
import quickdt.predictiveModels.decisionTree.Scorer;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.decisionTree.scorers.MSEScorer;
import quickdt.predictiveModels.decisionTree.scorers.SplitDiffScorer;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Benchmarks {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {
        List<Instance> diaInstances = loadDiabetesDataset();

        testWithInstances("diabetes", diaInstances);

        final List<Instance> moboInstances = loadMoboDataset();

        testWithInstances("mobo", moboInstances);


    }

    private static void testWithInstances(String dsName, final List<Instance> instances) {
        StationaryCrossValidator crossValidator = new StationaryCrossValidator();

        for (final Scorer scorer : Lists.newArrayList(new SplitDiffScorer(), new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE), new MSEScorer(MSEScorer.CrossValidationCorrection.TRUE))) {
            final TreeBuilder singleTreeBuilder = new TreeBuilder(scorer).binaryClassification(true);
            System.out.println(dsName+", single-tree, "+scorer+", "+crossValidator.getCrossValidatedLoss(singleTreeBuilder, instances));

            TreeBuilder forestTreeBuilder = new TreeBuilder(scorer).ignoreAttributeAtNodeProbability(0.5).binaryClassification(true);
            RandomForestBuilder randomForestBuilder = new RandomForestBuilder(forestTreeBuilder).numTrees(100).executorThreadCount(8);
            System.out.println(dsName+", random-forest, "+scorer+", "+crossValidator.getCrossValidatedLoss(randomForestBuilder, instances));
        }
    }

    public static List<Instance> loadDiabetesDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(Benchmarks.class.getResourceAsStream("diabetesDataset.txt.gz")))));
        final List<Instance> instances = Lists.newLinkedList();

        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            String[] splitLine = line.split("\\s");
            HashMapAttributes hashMapAttributes = new HashMapAttributes();
            for (int x=0; x<8; x++) {
                hashMapAttributes.put("attr"+x, Double.parseDouble(splitLine[x]));
            }
            final Instance<Map<String, Serializable>> instance = new InstanceWithMapOfRegressors(hashMapAttributes, splitLine[8]);
            instances.add(instance);

        }

        return instances;
    }

    public static List<Instance> loadIrisDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(Benchmarks.class.getResourceAsStream("iris.data.gz")))));
        final List<Instance> instances = Lists.newLinkedList();

        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            String[] splitLine = line.split(",");
            HashMapAttributes hashMapAttributes = new HashMapAttributes();
            for (int x=0; x<splitLine.length - 1; x++) {
                hashMapAttributes.put("attr"+x, splitLine[x]);
            }
            final Instance instance = new InstanceWithMapOfRegressors(hashMapAttributes, splitLine[splitLine.length-1]);
            instances.add(instance);

        }

        return instances;
    }

    public static List<Instance> loadMoboDataset() throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(Benchmarks.class.getResourceAsStream("mobo1.json.gz")))));

        final List<Instance> instances = Lists.newLinkedList();

        int count = 0;
        while (true) {
            count++;
            final String line = br.readLine();
            if (line == null) {
                break;
            }
            final JSONObject jo = (JSONObject) JSONValue.parse(line);
            final HashMapAttributes a = new HashMapAttributes();
            a.putAll((JSONObject) jo.get("attributes"));
            String binaryClassification = ((String) jo.get("output")).equals("none") ? "none" : "notNone";
            Instance instance = new InstanceWithMapOfRegressors(a,binaryClassification);
            instances.add(instance);
        }

        return instances;
    }
}

