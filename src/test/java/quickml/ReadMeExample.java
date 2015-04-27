package quickml;

import com.google.common.collect.Sets;
import quickml.data.*;
import quickml.data.InstanceWithAttributesMap;
import quickml.supervised.classifier.tree.DecisionTree;
import quickml.supervised.classifier.tree.TreeBuilder;
import quickml.supervised.classifier.tree.decisionTree.tree.attributeIgnoringStrategies.IgnoreAttributesWithConstantProbability;
import quickml.supervised.classifier.randomForest.RandomDecisionForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import java.io.Serializable;
import java.util.Set;

/**
 * Code examples for the README file.
 * 
 * @author Philipp Katz
 */
public class ReadMeExample {
    
    public static void main(String[] args) {
        final Set<InstanceWithAttributesMap> instances = Sets.newHashSet();
        // A male weighing 168lb that is 55 inches tall, they are overweight
        AttributesMap  attributes = AttributesMap.newHashMap() ;
        attributes.put("height",55);
        attributes.put("weight", 168);
        attributes.put("gender", "male");
        instances.add(new InstanceWithAttributesMap(attributes, "overweight"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("height",75);
        attributes.put("weight", 168);
        attributes.put("gender", "female");
        instances.add(new InstanceWithAttributesMap(attributes, "healthy"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("height",74);
        attributes.put("weight", 143);
        attributes.put("gender", "male");
        instances.add(new InstanceWithAttributesMap(attributes, "underweight"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("height",49);
        attributes.put("weight", 144);
        attributes.put("gender", "female");
        instances.add(new InstanceWithAttributesMap(attributes, "underweight"));
        attributes = AttributesMap.newHashMap() ;
        attributes.put("height",83);
        attributes.put("weight", 223);
        attributes.put("gender", "male");
        instances.add(new InstanceWithAttributesMap(attributes, "healthy"));
        
        {
            
            TreeBuilder treeBuilder = new TreeBuilder();
            DecisionTree decisionTree = treeBuilder.buildPredictiveModel(instances);

            attributes = AttributesMap.newHashMap() ;
            attributes.put("height",62);
            attributes.put("weight", 201);
            attributes.put("gender", "female");
            Serializable classification = decisionTree.getClassificationByMaxProb(attributes);
            if (classification.equals("healthy")) {
                System.out.println("They are healthy!");
            } else if (classification.equals("underweight")) {
                System.out.println("They are underweight!");
            } else {
                System.out.println("They are overweight!");
            }
            
            decisionTree.root.dump(System.out);
            
        }
        
        {
        
            TreeBuilder treeBuilder = new TreeBuilder()
                .attributeIgnoringStrategy(new IgnoreAttributesWithConstantProbability(0.7));
            RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder)
                .numTrees(50);
            RandomDecisionForest randomDecisionForest = randomForestBuilder.buildPredictiveModel(instances);

            attributes = AttributesMap.newHashMap() ;
            attributes.put("height",62);
            attributes.put("weight", 201);
            attributes.put("gender", "female");
            Serializable classification = randomDecisionForest.getClassificationByMaxProb(attributes);
            System.out.println("Assigned class: " + classification); 
        
        }
        
    }

}
