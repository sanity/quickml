package quickdt;

import com.google.common.collect.Sets;
import quickdt.data.*;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.io.Serializable;
import java.util.Set;

/**
 * Code examples for the README file.
 * 
 * @author Philipp Katz
 */
public class ReadMeExample {
    
    public static void main(String[] args) {
        final Set<Instance> instances = Sets.newHashSet();
        // A male weighing 168lb that is 55 inches tall, they are overweight
        instances.add(HashMapAttributes.create("height", 55, "weight", 168, "gender", "male").classification("overweight"));
        instances.add(HashMapAttributes.create("height", 75, "weight", 168, "gender", "female").classification("healthy"));
        instances.add(HashMapAttributes.create("height", 74, "weight", 143, "gender", "male").classification("underweight"));
        instances.add(HashMapAttributes.create("height", 49, "weight", 144, "gender", "female").classification("underweight"));
        instances.add(HashMapAttributes.create("height", 83, "weight", 223, "gender", "male").classification("healthy"));
        
        {
            
            TreeBuilder treeBuilder = new TreeBuilder();
            Tree tree = treeBuilder.buildPredictiveModel(instances);
            
            Attributes attributes = HashMapAttributes.create("height", 62, "weight", 201, "gender", "female");
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
                .ignoreAttributeAtNodeProbability(0.7);
            RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder)
                .numTrees(50);
            RandomForest randomForest = randomForestBuilder.buildPredictiveModel(instances);
            
            Attributes attributes = HashMapAttributes.create("height", 62, "weight", 201, "gender", "female");
            Serializable classification = randomForest.getClassificationByMaxProb(attributes);
            System.out.println("Assigned class: " + classification); 
        
        }
        
    }

}
