package quickml.supervised.classifier.randomForest;

/**
 * Created by alexanderhawk on 4/7/15.
 */
import quickml.data.*;
import quickml.data.instances.ClassifierInstance;
import quickml.supervised.*;
import quickml.supervised.ensembles.randomForest.randomDecisionForest.*;
import quickml.supervised.tree.attributeIgnoringStrategies.*;
import quickml.supervised.tree.decisionTree.*;

import java.io.*;
import java.util.*;

public class TestIrisAccuracy {
    public static void main(String[] args) throws IOException {
        List<ClassifierInstance> irisDataset = PredictiveAccuracyTests.loadIrisDataset();
        final RandomDecisionForest randomForest = new RandomDecisionForestBuilder<>(new DecisionTreeBuilder<>()
                // The default isn't desirable here because this dataset has so few attributes
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.2)))
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
