package quickdt.experiments;

import com.google.common.collect.Lists;
import org.apache.mahout.classifier.df.builder.DecisionTreeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.csvReader.CSVToMap;
import quickdt.data.AbstractInstance;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModelBuilderBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderhawk on 6/8/14.
 */
public class TreeTests {
    private static final Logger logger =  LoggerFactory.getLogger(TreeTests.class);


    public static void main(String[] args) {
        Iterable<? extends AbstractInstance> trainingData = Lists.newArrayList();
        try {
            trainingData = getTrainingData("redshift_training_data.csv");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(4).ignoreAttributeAtNodeProbability(.7).minCategoricalAttributeValueOccurances(0);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(20);
        RandomForest randomForest = randomForestBuilder.buildPredictiveModel(trainingData);
        randomForest.dump(System.out, 20);
    }

    private static Iterable<? extends AbstractInstance> getTrainingData(String filename) throws IOException {
        List<Map<String, Serializable>> instanceMaps = CSVToMap.loadRows(filename);
        List<Instance> instances = CsvReaderExp.convertRawMapToInstance(instanceMaps);
        logger.info("Read " + instances.size() + " instances");
        return instances;
    }
}