package quickml.supervised.classifier.randomForest;

/**
 * Created by alexanderhawk on 4/7/15.
 */
import quickml.data.AttributesMap;
import quickml.data.ClassifierInstance;
import quickml.supervised.PredictiveAccuracyTests;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForest;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.RandomDecisionForestBuilder;
import quickml.supervised.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.tree.decisionTree.DecisionTreeBuilder;
import quickml.supervised.tree.decisionTree.scorers.GRPenalizedGiniImpurityScorerFactory;
import quickml.supervised.tree.scorers.GRScorerFactory;


import java.io.IOException;
import java.util.List;

public class TestIrisAccuracy {
    public static void main(String[] args) throws IOException {
        List<ClassifierInstance> irisDataset = PredictiveAccuracyTests.loadIrisDataset();
        //final RandomForest randomForest = new RandomForestBuilder().buildPredictiveModel(irisDataset);
        final RandomDecisionForest randomForest = new RandomDecisionForestBuilder<ClassifierInstance>(new DecisionTreeBuilder<ClassifierInstance>()
                .scorerFactory(new GRPenalizedGiniImpurityScorerFactory(1.0))
                .maxDepth(8)
                .minAttributeValueOccurences(0)
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.2)))
                .numTrees(2)
                .buildPredictiveModel(irisDataset);

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
