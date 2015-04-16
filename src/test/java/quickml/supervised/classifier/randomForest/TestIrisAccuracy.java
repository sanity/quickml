package quickml.supervised.classifier.randomForest;

/**
 * Created by alexanderhawk on 4/7/15.
 */
import quickml.PredictiveAccuracyTests;
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.scorers.GiniImpurityScorer;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;
import java.io.IOException;
import java.util.List;

public class TestIrisAccuracy {
    public static void main(String[] args) throws IOException {
        List<ClassifierInstance> irisDataset = PredictiveAccuracyTests.loadIrisDataset();
        //final RandomForest randomForest = new RandomForestBuilder().buildPredictiveModel(irisDataset);
        final RandomForest randomForest = new RandomForestBuilder(new TreeBuilder(new GiniImpurityScorer()).maxDepth(8).ignoreAttributeAtNodeProbability(0.7)).numTrees(2).buildPredictiveModel(irisDataset);
        AttributesMap attributes = new AttributesMap();
        attributes.put("sepal-length", 5.84);
        attributes.put("sepal-width", 3.05);
        attributes.put("petal-length", 3.76);
        attributes.put("petal-width", 1.20);
        System.out.println("Prediction: " + randomForest.predict(attributes));
        for (ClassifierInstance instance : irisDataset) {
            System.out.println("classification: " + randomForest.getClassificationByMaxProb(instance.getAttributes()));
        }
    }
}
