package quickdt;

import org.testng.annotations.Test;
import quickdt.data.HashMapAttributes;
import quickdt.data.Instance;
import quickdt.predictiveModels.decisionTree.Tree;
import quickdt.predictiveModels.decisionTree.TreeBuilder;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by huascarf on 5/30/14.
 */
public class SparseVectorTest {

    @Test
    public void sparse()  {

        Random r = new Random();
        LinkedList<Instance> trainSparseVectors = new LinkedList();
        LinkedList<Instance> trainDenseVectors = new LinkedList();

        for (int instance = 0; instance < 1000; instance++) {
            HashMapAttributes sparseInstanceHmAttributes = new HashMapAttributes();
            HashMapAttributes denseInstanceHmAttributes = new HashMapAttributes();
            for (int feat = 0; feat < 1000; feat++) {
                String key = ((Integer)feat).toString();
//                String key = featureExtractionPipeline.getFeatureName((Integer)e.getKey());
                if(feat == instance){
                    denseInstanceHmAttributes.put(key, 1.0);
                    sparseInstanceHmAttributes.put(key, 1.0);
                } else
                    denseInstanceHmAttributes.put(key, 0);

            }
            final Serializable label = r.nextBoolean();
            trainSparseVectors.add(sparseInstanceHmAttributes.classification(label.toString()));
            trainDenseVectors.add(denseInstanceHmAttributes.classification(label.toString()));
        }


        {
            TreeBuilder treeBuilder = new TreeBuilder();
            long start = System.currentTimeMillis();
            Tree tree = treeBuilder.buildPredictiveModel(trainSparseVectors);
            System.out.println("Training with sparse vectors took: " + (System.currentTimeMillis()-start) + " milliseconds");
            tree.node.dump(System.out);
        }

        {
            TreeBuilder treeBuilder = new TreeBuilder();
            long start = System.currentTimeMillis();
            Tree tree = treeBuilder.buildPredictiveModel(trainDenseVectors);
            System.out.println("Training with dense vectors took: " + (System.currentTimeMillis()-start) + " milliseconds");
            tree.node.dump(System.out);
        }

        System.exit(0);
    }
}
