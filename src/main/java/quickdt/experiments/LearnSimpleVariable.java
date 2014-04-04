package quickdt.experiments;

import com.google.common.collect.Lists;
import quickdt.data.HashMapAttributes;
import quickdt.data.Instance;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.util.List;
import java.util.Random;

/**
 * Created by alexanderhawk on 1/15/14.
 */
public class LearnSimpleVariable {
    public static Random generator;

    public static void main(String[] args) {
        int instances = 100000;
        int maxAge = 30;

        generator = new Random();

        List<Instance> trainingData = createTrainingData(instances, maxAge);
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(2);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(100);
        RandomForest randomForest = randomForestBuilder.buildPredictiveModel(trainingData);
        System.out.println("Probability of a click: "+randomForest.getProbability(HashMapAttributes.create("age", 11),"click"));
    }

    private static List<Instance> createTrainingData(int instances, int maxAge) {
        List<Instance> trainingData = Lists.newArrayList();
        for (int i=0; i<instances; i++)  {
            int instanceAge = generator.nextInt(maxAge);
            trainingData.add(Instance.create(sampleClickValue(instanceAge), "age", instanceAge));
        }
        return trainingData;
    }


    private static String sampleClickValue(int age) {
        return generator.nextDouble() < clickProbFromAge(age) ? "click" : "no-click";
    }

    private static double clickProbFromAge(int age) {
        return age > 10 ? .995 : .6;
    }


}
