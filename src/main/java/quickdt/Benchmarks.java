package quickdt;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import quickdt.bagging.BaggedTree;
import quickdt.bagging.BaggingResult;
import quickdt.randomForest.RandomForest;
import quickdt.randomForest.RandomForestBuilder;
import quickdt.scorers.Scorer1;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class Benchmarks {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {
        final BufferedReader br = new BufferedReader(new InputStreamReader((new GZIPInputStream(new FileInputStream(
                new File(new File(System.getProperty("user.dir")), "testdata/mobo1.txt.gz"))))));

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


        final List<Instance> train = instances.subList(0, instances.size() / 2);
        final List<Instance> test = instances.subList(instances.size() / 2 + 1, instances.size() - 1);

        System.out.println("Read " + instances.size() + " instances");

        System.out.println("Testing scorers with single decision node");
        for (final Scorer scorer : Sets.newHashSet(new Scorer1())) {
            final TreeBuilder tb = new TreeBuilder(scorer);

            final long startTime = System.currentTimeMillis();
            final Node tree = tb.buildPredictiveModel(train).node;
            System.out.println(scorer.getClass().getSimpleName() + " build time "
                    + (System.currentTimeMillis() - startTime) + ", size: " + tree.size() + " mean depth: "
                    + tree.meanDepth());

            int correctlyClassified = 0;
            for (Instance testInstance : test) {
                String result = (String) tree.getLeaf(testInstance.getAttributes()).getBestClassification();
                if (result.equals(testInstance.getClassification())) {
                    correctlyClassified++;
                }
            }
            System.out.println(", accuracy: " + (double) correctlyClassified / test.size());

            System.out.println("Testing bagging predictors");

            for (int i = 2; i <= 20; i++) {
                BaggedTree baggedTree = BaggedTree.build(tb, i, train);

                correctlyClassified = 0;
                for (Instance testInstance : test) {
                    BaggingResult baggingResult = baggedTree.predict(testInstance.getAttributes());
                    Object result = baggingResult.getClassification().getValue0();
                    if (result.equals(testInstance.getClassification())) {
                        correctlyClassified++;
                    }
                }
                System.out.println("accuracy with " + i + " trees: " + (double) correctlyClassified / test.size());
                //ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("baggedTree.ser")));
                //out.writeObject(baggedTree);
            }

            System.out.println("Testing random forest");

            for (int i = 2; i <= 20; i++) {
                RandomForestBuilder rfBuilder = new RandomForestBuilder(new TreeBuilder());
                RandomForest randomForest = rfBuilder.buildPredictiveModel(train);
                correctlyClassified = 0;
                for (Instance testInstance : test) {
                    Serializable result = randomForest.getClassificationByMaxProb(testInstance.getAttributes());
                    if (result.equals(testInstance.getClassification())) {
                        correctlyClassified++;
                    }
                }
                System.out.println("accuracy with " + i + " trees: " + (double) correctlyClassified / test.size());
                //ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File("baggedTree.ser")));
                //out.writeObject(baggedTree);
            }

        }


    }

}
