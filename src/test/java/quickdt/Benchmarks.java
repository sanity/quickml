package quickdt;

import com.google.common.collect.Lists;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import quickdt.experiments.crossValidation.CrossValidator;
import quickdt.randomForest.RandomForestBuilder;
import quickdt.scorers.MSEScorer;
import quickdt.scorers.SplitDiffScorer;

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
        CrossValidator crossValidator = new CrossValidator();

        for (final Scorer scorer : Lists.newArrayList(new SplitDiffScorer(), new MSEScorer(MSEScorer.CrossValidationCorrection.FALSE), new MSEScorer(MSEScorer.CrossValidationCorrection.TRUE))) {
            final TreeBuilder singleTreeBuilder = new TreeBuilder(scorer);
            System.out.println(dsName+", single-tree, "+scorer+", "+crossValidator.test(singleTreeBuilder, instances));

            TreeBuilder forestTreeBuilder = new TreeBuilder(scorer).ignoreAttributeAtNodeProbability(0.5);
            RandomForestBuilder randomForestBuilder = new RandomForestBuilder(forestTreeBuilder).numTrees(100).executorThreadCount(8).useBagging(false);
            System.out.println(dsName+", random-forest, "+scorer+", "+crossValidator.test(randomForestBuilder, instances));
        }
    }

    private static List<Instance> loadDiabetesDataset() throws IOException {
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
                hashMapAttributes.put("attr"+x, splitLine[x]);
            }
            final Instance instance = new Instance(hashMapAttributes, splitLine[8]);
            instances.add(instance);

        }

        return instances;
    }

    private static List<Instance> loadMoboDataset() throws IOException {
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
            Instance instance = new Instance(a, (String) jo.get("output"));
            instances.add(instance);
        }

        return instances;
    }
}

