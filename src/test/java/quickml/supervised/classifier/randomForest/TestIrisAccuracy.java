package quickml.supervised.classifier.randomForest;

/**
 * Created by alexanderhawk on 4/7/15.
 */
import org.testng.Assert;
import quickml.PredictiveAccuracyTests;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class TestIrisAccuracy {
    public static void main(String[] args) throws IOException {
        List<ClassifierInstance> irisDataset = PredictiveAccuracyTests.loadIrisDataset();
//        final RandomForest randomForest = new RandomForestBuilder().buildPredictiveModel(irisDataset);
          final RandomForest randomForest = new RandomForestBuilder(new TreeBuilder(new GiniImpurityScorer()).maxDepth(5).ignoreAttributeAtNodeProbability(0)).numTrees(5).buildPredictiveModel(irisDataset);
        AttributesMap attributes = new AttributesMap();
        attributes.put("sepal-length", 5.84);
        attributes.put("sepal-width", 3.05);
        attributes.put("petal-length", 3.76);
        attributes.put("petal-width", 1.20);
        System.out.println("Prediction: " + randomForest.predict(attributes));

        double numCorrect = 0f;
        String[] labels = new String[]{"Iris-virginica", "Iris-setosa", "Iris-versicolor"};
        for (ClassifierInstance instance : irisDataset) {
            double maxProb = -1;
            Serializable bestAnswer = null;
            for (String candidate : labels) {
                if (randomForest.getProbability(instance.getAttributes(), candidate) > maxProb) {
                    bestAnswer = candidate;
                }
            }
            System.out.println(bestAnswer);
            if (instance.getLabel().toString().equals(bestAnswer.toString())) {
                numCorrect++;
            }
        }

        double accuracy = numCorrect / irisDataset.size();
        System.out.println("Oracle accuracy is " + accuracy);
        Assert.assertTrue(accuracy > 0.9, "Oracle accuracy is too low. Expected at least 0.9.");
    }
}
