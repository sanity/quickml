package quickdt.experiments;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickdt.csvReader.CSVToMap;
import quickdt.data.Attributes;
import quickdt.data.HashMapAttributes;
import quickdt.data.Instance;
import quickdt.predictiveModels.PredictiveModel;
import quickdt.predictiveModels.PredictiveModelBuilder;
import quickdt.predictiveModels.calibratedPredictiveModel.CalibratedPredictiveModel;
import quickdt.predictiveModels.calibratedPredictiveModel.PAVCalibratedPredictiveModelBuilder;
import quickdt.predictiveModels.decisionTree.TreeBuilder;
import quickdt.predictiveModels.downsamplingPredictiveModel.Utils;
import quickdt.predictiveModels.randomForest.RandomForest;
import quickdt.predictiveModels.randomForest.RandomForestBuilder;

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
        List<Instance> instances = convertRawMapToInstance(instanceMaps);
        logger.info("got instances");

        HashMap<String, ClicksAndImps> uniqueBrowsersRawData = uniqueBrowserData(instances);
        RandomForestBuilder randomForestBuilder = getRandomForestBuilder(4, 32);
        RandomForest randomForest = randomForestBuilder.buildPredictiveModel(instances);
        CalibratedPredictiveModel calibratedPredictiveModel = (new PAVCalibratedPredictiveModelBuilder(randomForestBuilder).binsInCalibrator(5)).buildPredictiveModel(instances);
        HashMap<String, ClicksAndImps> uniqueBrowsersRF = uniqueBrowserDataPredicted(randomForest, instances);
        HashMap<String, ClicksAndImps> uniqueBrowserPredicted = uniqueBrowserDataPredicted(calibratedPredictiveModel, instances);

        int x = 0;
    }

    private static HashMap<String, ClicksAndImps> uniqueBrowserData(List<Instance> instances) {
        HashMap<String, ClicksAndImps> uniqueBrowsers = new HashMap<>();

        for (Instance instance : instances) {
            String browser = (String) instance.getAttributes().get("an_browser");
            double click = (Double) instance.getClassification();

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

    private static HashMap<String, ClicksAndImps> uniqueBrowserDataPredicted(PredictiveModel predictiveModel,  List<Instance> instances) {
        HashMap<String, ClicksAndImps> uniqueBrowsers = new HashMap<>();
        for (Instance instance : instances) {
            String browser = (String) instance.getAttributes().get("an_browser");
            double clickProb = Utils.correctProbability(.99, predictiveModel.getProbability(instance.getAttributes(), 1.0));


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

    public static List<Instance> convertRawMapToInstance(List<Map<String,Serializable>> rawMaps) {
        List<Instance> instances = Lists.newArrayList();
        Attributes attributes;
        Instance instance;
        int numInstances = 30000;
        int count = 0;
        for (Map<String, Serializable> rawMap : rawMaps) {
            count++;
            if (count < 8000) {
                attributes = new HashMapAttributes();
                for (String key : rawMap.keySet()) {
                    if (!key.equals("is_click")) {
                        attributes.put(key, rawMap.get(key));
                    }
                }
                double clickVal = (rawMap.get("is_click").equals("f")) ? 0.0 : 1.0;
                instance = new Instance(attributes, clickVal);
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
