package quickdt.experiments;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.csvReader.CSVToMap;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by chrisreeves on 5/29/14.
 */
public class BuildAndTestModel {

    private static final Logger logger =  LoggerFactory.getLogger(BuildAndTestModel.class);

    static double[] skipProbs = {0.7,0.8,0.9};
    static int[] maxDepth = {4,6,8, Integer.MAX_VALUE};
    static int[] minLeaves = {0,10,100};

    public static void main(String[] args) {
        try {
            Iterable<? extends AbstractInstance> trainingData = getTrainingData("/Users/chrisreeves/temp/training_data.csv");
            Iterable<? extends AbstractInstance> testData = getTrainingData("/Users/chrisreeves/temp/test_data.csv");
            for(double skipProb : skipProbs) {
                for(int depth : maxDepth) {
                    for(int minLeaf : minLeaves) {
                        PredictiveModelBuilder predictiveModelBuilder = getPredictiveModelBuilder(skipProb, depth, minLeaf);
                        PredictiveModel predictiveModel = predictiveModelBuilder.buildPredictiveModel(trainingData);


                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error", e);
        }
    }

    private static PredictiveModelBuilder getPredictiveModelBuilder(double skipProb, int depth, int minLeafInstances) {
        TreeBuilder treeBuilder = new TreeBuilder().ignoreAttributeAtNodeProbability(skipProb).maxDepth(depth).minLeafInstances(minLeafInstances);
        return new RandomForestBuilder(treeBuilder).numTrees(32);
    }

    private static Iterable<? extends AbstractInstance> getTrainingData(String filename) throws IOException {
        List<Map<String, Serializable>> instanceMaps = CSVToMap.loadRows(filename);
        List<Instance> instances = csvReaderExp.convertRawMapToInstance(instanceMaps);
        logger.info("Read " + instances.size() + " instances");
        return instances;
    }
}
