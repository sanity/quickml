package quickml;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import quickml.data.*;
import quickml.data.ClassifierInstance;
import quickml.supervised.classifier.decisionTree.Tree;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import javax.management.Attribute;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Code examples for the README file.
 * 
 * @author Philipp Katz
 */
public class ReadMeExample {
    
    public static void main(String[] args) throws IOException {
        {
            List<ClassifierInstance> irisDataset = PredictiveAccuracyTests.loadIrisDataset();
            final RandomForest randomForest = new RandomForestBuilder().buildPredictiveModel(irisDataset);
            AttributesMap attributes = new AttributesMap();
            attributes.put("sepal-length", 5.84);
            attributes.put("sepal-width", 3.05);
            attributes.put("petal-length", 3.76);
            attributes.put("petal-width", 1.2);
            System.out.println("Prediction: " + randomForest.getProbability(attributes, "Iris-virginica"));
        }


        final Set<ClassifierInstance> instances = Sets.newHashSet();
        // A male weighing 168lb that is 55 inches tall, they are overweight
        AttributesMap  attributes = AttributesMap.newHashMap() ;
        attributes.put("height",55);
        attributes.put("weight", 168);
        attributes.put("gender", "male");
        instances.add(new ClassifierInstance(attributes, "overweight"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("height",75);
        attributes.put("weight", 168);
        attributes.put("gender", "female");
        instances.add(new ClassifierInstance(attributes, "healthy"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("height",74);
        attributes.put("weight", 143);
        attributes.put("gender", "male");
        instances.add(new ClassifierInstance(attributes, "underweight"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("height",49);
        attributes.put("weight", 144);
        attributes.put("gender", "female");
        instances.add(new ClassifierInstance(attributes, "underweight"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("height",83);
        attributes.put("weight", 223);
        attributes.put("gender", "male");
        instances.add(new ClassifierInstance(attributes, "healthy"));
        
        {
            
            TreeBuilder treeBuilder = new TreeBuilder();
            Tree tree = treeBuilder.buildPredictiveModel(instances);

            attributes = AttributesMap.newHashMap() ;
            attributes.put("height",62);
            attributes.put("weight", 201);
            attributes.put("gender", "female");
            Serializable classification = tree.getClassificationByMaxProb(attributes);
            if (classification.equals("healthy")) {
                System.out.println("They are healthy!");
            } else if (classification.equals("underweight")) {
                System.out.println("They are underweight!");
            } else {
                System.out.println("They are overweight!");
            }
            
            tree.node.dump(System.out);
            
        }
        
        {
        
            TreeBuilder treeBuilder = new TreeBuilder()
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7));
            RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder)
                .numTrees(50);
            RandomForest randomForest = randomForestBuilder.buildPredictiveModel(instances);

            attributes = AttributesMap.newHashMap() ;
            attributes.put("height",62);
            attributes.put("weight", 201);
            attributes.put("gender", "female");
            Serializable classification = randomForest.getClassificationByMaxProb(attributes);
            System.out.println("Assigned class: " + classification); 
        
        }
        
    }

}
