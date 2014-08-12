package quickml.experiments;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickml.csvReader.CSVToMap;
import quickml.data.Instance;
import quickml.data.InstanceImpl;
import quickml.supervised.classifier.Classifier;
import quickml.supervised.PredictiveModelBuilder;
import quickml.supervised.classifier.decisionTree.TreeBuilder;
import quickml.supervised.classifier.downsamplingPredictiveModel.Utils;
import quickml.supervised.classifier.randomForest.RandomForest;
import quickml.supervised.classifier.randomForest.RandomForestBuilder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by alexanderhawk on 1/16/14.
 */
public class CsvReaderExp {
    private static final Logger logger =  LoggerFactory.getLogger(CsvReaderExp.class);


    public static void main(String[] args) {
        int numTraniningExamples = 40100;
        String inputFile = "redshift_training_data.csv";
        List<Map<String, Serializable>> instanceMaps = CSVToMap.loadRows(inputFile);
        List<Instance<Map<String,Serializable>>> instances = convertRawMapToInstance(instanceMaps);
        logger.info("got instances");

        HashMap<String, ClicksAndImps> uniqueBrowsersRawData = uniqueBrowserData(instances);
        RandomForestBuilder randomForestBuilder = getRandomForestBuilder(4, 32);
        RandomForest randomForest = randomForestBuilder.buildPredictiveModel(instances);
        HashMap<String, ClicksAndImps> uniqueBrowsersRF = uniqueBrowserDataPredicted(randomForest, instances);

        int x = 0;
    }

    private static HashMap<String, ClicksAndImps> uniqueBrowserData(List<Instance<Map<String,Serializable>>> instances) {
        HashMap<String, ClicksAndImps> uniqueBrowsers = new HashMap<>();

        for (Instance<Map<String,Serializable>> instance : instances) {
            String browser = (String) instance.getRegressors().get("an_browser");
            double click = (Double) instance.getLabel();

            if (browser != null) {
                int count = 0;
                if (!uniqueBrowsers.containsKey(browser)) {
                    uniqueBrowsers.put(browser, new ClicksAndImps());
                }
                uniqueBrowsers.get(browser).imps++;
                uniqueBrowsers.get(browser).clicks += click;
            }
        }
        for (String key : uniqueBrowsers.keySet()) {
            uniqueBrowsers.get(key).setCtr();
        }

        return uniqueBrowsers;
    }

    private static HashMap<String, ClicksAndImps> uniqueBrowserDataPredicted(Classifier predictiveModel,  List<Instance<Map<String,Serializable>>> instances) {
        HashMap<String, ClicksAndImps> uniqueBrowsers = new HashMap<>();
        for (Instance<Map<String,Serializable>> instance : instances) {
            String browser = (String) instance.getRegressors().get("an_browser");
            double clickProb = Utils.correctProbability(.99, predictiveModel.getProbability(instance.getRegressors(), 1.0));


            if (browser != null) {
                int count = 0;
                if (!uniqueBrowsers.containsKey(browser)) {
                    uniqueBrowsers.put(browser, new ClicksAndImps());
                }
                uniqueBrowsers.get(browser).imps++;
                uniqueBrowsers.get(browser).clicks += clickProb;
            }
        }
        for (String key : uniqueBrowsers.keySet()) {
            uniqueBrowsers.get(key).setCtr();
        }
        return uniqueBrowsers;

    }

    public static List<Instance<Map<String,Serializable>>> convertRawMapToInstance(List<Map<String,Serializable>> rawMaps) {
        List<Instance<Map<String,Serializable>>> instances = Lists.newArrayList();
        Map<String, Serializable> attributes;
        Instance<Map<String,Serializable>> instance;
        int numInstances = 30000;
        int count = 0;
        for (Map<String, Serializable> rawMap : rawMaps) {
            count++;
            if (count < 8000) {
                attributes = new HashMap();
                for (String key : rawMap.keySet()) {
                    if (!key.equals("is_click")) {
                        attributes.put(key, rawMap.get(key));
                    }
                }
                double clickVal = (rawMap.get("is_click").equals("f")) ? 0.0 : 1.0;
                instance = new InstanceImpl(attributes, clickVal);
                instances.add(instance);
                // if (count++ > numInstances) break;
            }
        }
        return instances;
    }


    private static RandomForestBuilder getRandomForestBuilder(int maxDepth, int numTrees) {
        TreeBuilder treeBuilder = new TreeBuilder().maxDepth(maxDepth).ignoreAttributeAtNodeProbability(.9);//;.minLeafInstances(20);
        return new RandomForestBuilder(treeBuilder).numTrees(numTrees);
    }

    private static PredictiveModelBuilder getUpdatableRandomForestBuilder(int maxDepth, int numTrees) {
        TreeBuilder treeBuilder = new TreeBuilder().ignoreAttributeAtNodeProbability(.7).minLeafInstances(20);
        RandomForestBuilder randomForestBuilder = new RandomForestBuilder(treeBuilder).numTrees(numTrees);
        //   UpdatableRandomForestBuilder updatableRandomForestBuilder = (UpdatableRandomForestBuilder) new UpdatableRandomForestBuilder(randomForestBuilder).rebuildThreshold(2).splitNodeThreshold(2);
        return randomForestBuilder;//updatableRandomForestBuilder;
    }


}
